package test.einstein.flutracktweet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * ConsolidateWeather with min_temp , max_temp for applicable_date.
 * @author annie
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsolidateWeather {
	String id;
	String applicable_date;
	String min_temp;
	String max_temp;
	String the_temp;
    String created;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApplicable_date() {
		return applicable_date;
	}
	public void setApplicable_date(String applicable_date) {
		this.applicable_date = applicable_date;
	}
	public String getMin_temp() {
		return min_temp;
	}
	public void setMin_temp(String min_temp) {
		this.min_temp = min_temp;
	}
	public String getMax_temp() {
		return max_temp;
	}
	public void setMax_temp(String max_temp) {
		this.max_temp = max_temp;
	}
	public String getThe_temp() {
		return the_temp;
	}
	public void setThe_temp(String the_temp) {
		this.the_temp = the_temp;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	public int getMinTemp( )
	{
		return (int)Double.parseDouble(min_temp);
	}
    
	public int getMaxTemp( )
	{
		return (int)Double.parseDouble(max_temp);
	}
    
}
