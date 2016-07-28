package com.roughindustries.air;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.roughindustries.air.client.AirlinesMapper;
import com.roughindustries.air.model.Airlines;
import com.roughindustries.air.model.AirlinesExample;
import com.roughindustries.air.model.Airports;
import com.roughindustries.air.resources.GlobalProperties;
import com.roughindustries.air.scrapers.AirportPageForAirportInfoParser;
import com.roughindustries.air.scrapers.AirportScraper;

/**
 * Hello world!
 *
 */
public class App {

	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(App.class);

	/**
	 * 
	 */
	static GlobalProperties Props = GlobalProperties.getInstance();

	public CopyOnWriteArrayList<Airports> full_al = new CopyOnWriteArrayList<Airports>();
	public ConcurrentHashMap<String, Airports> al = new ConcurrentHashMap<String, Airports>();
	final static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(25);
	AirportScraper as = new AirportScraper();

	public static void main(String[] args) {
		App app = new App();
		// read the yaml in
		app.readYamlToFile("airports.yml");

		// This method is designed to get the airports as quick as
		// possible. It is not really good for anything else. The
		// idea is blow through these so that things that need to
		// be throttled can be.
		app.parseAirports();
		// app = null;
		System.gc();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (queue.size() > 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// write the results out so we don't have to scrape them in the future
		app.writeYamlToFile("airports.yml");

		// Load the yaml here instead of scraping

		// app.parseLatLong();
		// app.parseLocationServed();
		app.processLocationServed();

		logger.debug("" + app.al.size());

	}

	public App() {

	}

	public synchronized void updateAirline(int recordNumber, Airlines al) {
		SqlSession ses = null;
		try {
			ses = Props.getSqlSessionFactory().openSession();
			AirlinesMapper mapper = ses.getMapper(AirlinesMapper.class);
			AirlinesExample example = new AirlinesExample();
			example.createCriteria().andIataCodeEqualTo(al.getIataCode());
			int updates = mapper.updateByExample(al, example);
			if (updates < 1) {
				ses.insert("com.roughindustries.air.client.AirlinesMapper.insertSelective", al);
			}
			ses.commit();
		} finally {
			if (ses != null) {
				ses.close();
				ses = null;
			}
		}
	}

	public void parseAirports() {
		try {
			logger.debug(Props.getAirportPage());
			Elements airports = as.parseIATAAlphaGroups(as.getIATAAlphaGroups(as.getAirportListPage()));
			full_al = as.parseAirportsElementList(airports);

			// Blow through the airports so that we can gwt them into yaml to
			// work on them
			ExecutorService executorService = new ThreadPoolExecutor(13, 25, 0L, TimeUnit.MILLISECONDS, queue);
			for (int i = 0; i < full_al.size(); i++) {
			//for (int i = 0; i < 50; i++) {

				boolean submitted = false;
				while (!submitted) {
					try {
						Airports airport = full_al.get(i);
						Runnable call = new AirportPageForAirportInfoParser(i, this);
						executorService.execute(call);
						submitted = true;
					} catch (RejectedExecutionException ree) {
						// logger.debug("Queue is full");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			executorService.shutdown();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseLatLong() {
		for (int i = 0; i < al.size(); i++) {
			Airports new_ai = as.parseAirportPageForLatLong(al.get(i));
			al.put(new_ai.getIataCode(), new_ai);
		}
	}

	class LocationServedTimerTask extends TimerTask {

		App app;
		int i = 0;
		boolean finished = false;

		public LocationServedTimerTask(App app) {
			this.app = app;
		}

		@Override
		public void run() {
			if (!finished) {
				Airports ai = (Airports) al.values().toArray()[i];
				Airports new_ai = as.parseGeonamesWSLocServ(ai);
				al.put(new_ai.getIataCode(), new_ai);
				i++;
			}
			if (i >= al.values().size()) {
				finished = true;
			}
		}

		public boolean isFinished() {
			return finished;
		}
	}

	public void processLocationServed() {
		// run this task as a background/daemon thread
		TimerTask timerTask = new LocationServedTimerTask(this);
		Timer timer = new Timer(true);
		// 5 seconds
		timer.scheduleAtFixedRate(timerTask, 0, 5 * 1000);
		// 5 minutes
		// timer.scheduleAtFixedRate(timerTask, 0, 5*60*1000);
	}

	public void parseLocationServed() {
		Iterator<Entry<String, Airports>> it = al.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i >= 25) {
				break;
			}
			i++;
			Entry<String, Airports> pair = it.next();
			Airports new_ai = as.parseGeonamesWSLocServ(pair.getValue());
			al.put(new_ai.getIataCode(), new_ai);
		}
	}

	public void writeYamlToFile(String filename) {
		try {
			YamlWriter writer = new YamlWriter(new FileWriter(filename));
			writer.getConfig().writeConfig.setEscapeUnicode(false);
			writer.write(al);
			writer.close();
		} catch (YamlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void readYamlToFile(String filename) {
		try {
			FileReader fr = null;

			File locatedFile = new File(filename);
			if (locatedFile.exists()) {
				fr = new FileReader(locatedFile);
				YamlReader reader = new YamlReader(fr);
				al = (ConcurrentHashMap<String, Airports>) reader.read();
				if(al == null){
					al = new ConcurrentHashMap<String, Airports>();
				}
				reader.close();
			}
		} catch (YamlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
