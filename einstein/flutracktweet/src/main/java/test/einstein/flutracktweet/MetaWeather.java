package test.einstein.flutracktweet;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Weather meta data with ConsolidateWeather info.
 * @author annie
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MetaWeather {
	//Coordinates identified near locations in the format of comma separated latt,long
	String  lattlong;
	
	//Woeid for a location: Where On Earth ID
	String woeid; 
	
	//actual latt,long for a location.
	String latt_long;
	 
	//consolidated_weather with five days forecast
	List<ConsolidateWeather> consolidated_weather;

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

	public List<ConsolidateWeather> getConsolidated_weather() {
		return consolidated_weather;
	}

	public void setConsolidated_weather(List<ConsolidateWeather> consolidated_weather) {
		this.consolidated_weather = consolidated_weather;
	}
	
}
