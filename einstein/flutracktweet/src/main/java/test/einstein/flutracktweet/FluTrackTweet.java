package test.einstein.flutracktweet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * FluTrackTweet contains FluTrackTweet info along with 5 day average min and max temp 
 * for the nearest location according to the coordinates where the tweet is sent.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class FluTrackTweet {
	String user_name;
	String tweet_text;
	String latitude;
	String longitude;
	String tweet_date;
	String aggravation;
	
	String avgMax;
	String avgMin;
	
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getTweet_text() {
		return tweet_text;
	}
	public void setTweet_text(String tweet_text) {
		this.tweet_text = tweet_text;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getTweet_date() {
		return tweet_date;
	}
	public void setTweet_date(String tweet_date) {
		this.tweet_date = tweet_date;
	}
	public String getAggravation() {
		return aggravation;
	}
	public void setAggravation(String aggravation) {
		this.aggravation = aggravation;
	}
	public String getAvgMax() {
		return avgMax;
	}
	public void setAvgMax(String avgMax) {
		this.avgMax = avgMax;
	}
	public String getAvgMin() {
		return avgMin;
	}
	public void setAvgMin(String avgMin) {
		this.avgMin = avgMin;
	}
	 
}
