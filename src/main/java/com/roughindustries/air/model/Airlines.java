package com.roughindustries.air.model;

import java.util.HashMap;
import java.util.Map;

public class Airlines {

	public Map<String, Airports> destinations = new HashMap<String, Airports>();
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.airlines.internal_airline_id
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	private Integer internalAirlineId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.airlines.iata_code
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	private String iataCode;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.airlines.name
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	private String name;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column public.airlines.wiki_url
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	private String wikiUrl;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.airlines.internal_airline_id
	 * @return  the value of public.airlines.internal_airline_id
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public Integer getInternalAirlineId() {
		return internalAirlineId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.airlines.internal_airline_id
	 * @param internalAirlineId  the value for public.airlines.internal_airline_id
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public void setInternalAirlineId(Integer internalAirlineId) {
		this.internalAirlineId = internalAirlineId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.airlines.iata_code
	 * @return  the value of public.airlines.iata_code
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public String getIataCode() {
		return iataCode;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.airlines.iata_code
	 * @param iataCode  the value for public.airlines.iata_code
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public void setIataCode(String iataCode) {
		this.iataCode = iataCode;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.airlines.name
	 * @return  the value of public.airlines.name
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.airlines.name
	 * @param name  the value for public.airlines.name
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column public.airlines.wiki_url
	 * @return  the value of public.airlines.wiki_url
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public String getWikiUrl() {
		return wikiUrl;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column public.airlines.wiki_url
	 * @param wikiUrl  the value for public.airlines.wiki_url
	 * @mbggenerated  Sat Apr 02 15:30:07 CDT 2016
	 */
	public void setWikiUrl(String wikiUrl) {
		this.wikiUrl = wikiUrl;
	}
}