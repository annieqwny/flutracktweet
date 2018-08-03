package test.einstein.flutracktweet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Object contains Location attributes.
 * @author annie
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Location {

	//Coordinates identified near locations in the format of comma separated latt,long
	String lattlong;
	
	//Woeid for a location : Where On Earth ID
	String woeid; 
	
	//actual latt_long for a location
	String latt_long;
	 
	String location_type;
	
	String title;
	
	//distance from lattlong.
	String distance;
	
	public String getLattlong() {
		return lattlong;
	}
	public void setLattlong(String lattlong) {
		this.lattlong = lattlong;
	}
	public String getWoeid() {
		return woeid;
	}
	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}
	public String getLatt_long() {
		return latt_long;
	}
	public void setLatt_long(String latt_long) {
		this.latt_long = latt_long;
	}
	public String getLocation_type() {
		return location_type;
	}
	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}

}
