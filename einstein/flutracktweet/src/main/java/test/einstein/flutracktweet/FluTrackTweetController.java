package test.einstein.flutracktweet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class FluTrackTweetController {
	@Autowired
	private Environment env;
	Logger logger = LoggerFactory.getLogger(FluTrackTweetController.class);

	//s is search string can be "feverANDcoughORfever", the number of tweets returned will be controlled in a properties file,
	//it will return whatever returned by default if it is not set.
	//Single Thread version
	@RequestMapping("/getFluTrackTweetsWithWeather")
	public List<FluTrackTweet> getFluTrackTweetsWithWeather(@RequestParam(name="s") String s )
	{		
		String tweetNum = getProperty("NUMBER_OF_FLU_TRACK_TWEETS");
		String tweetNumParam = (tweetNum==null||tweetNum.isEmpty())?"":"&&limit="+ tweetNum;
		final String uri=getProperty("URL_GET_FLU_TRACK_TWEETS") + s + tweetNumParam;
		
		List<FluTrackTweet> fttList= queryFluTrackTweetsUsingRestTemplate(uri);
		
		//for each flutracktweet, get ConsolidateWeather and calculate/ set avgMax and avgMin.
		for(FluTrackTweet ftt:fttList)
		{
			findAvgMinMaxForFluTrackTweet(ftt);
		}
		
		return fttList;
	}
	
	//s is search string can be "feverANDcoughORfever", the number of tweets returned will be controlled in a properties file, 
	//it will return whatever returned by default if it is not set.
	//Multithread version
	@RequestMapping("/getFluTrackTweetsWithWeatherMThreads")
	public List<FluTrackTweet> getFluTrackTweetsWithWeatherMThreads(@RequestParam(name="s") String s ) throws InterruptedException
	{
		String tweetNum = getProperty("NUMBER_OF_FLU_TRACK_TWEETS");
		String tweetNumParam = (tweetNum==null||tweetNum.isEmpty())?"":"&&limit="+ tweetNum;
		
		final String uri=getProperty("URL_GET_FLU_TRACK_TWEETS")+s+tweetNumParam;
		logger.info("MainWorker thread START name:"+ Thread.currentThread().getName());
		
		List<FluTrackTweet> fttList= queryFluTrackTweetsUsingRestTemplate(uri);
		if(!fttList.isEmpty())
		{
			ExecutorService executor = Executors.newFixedThreadPool(fttList.size());
			CountDownLatch latch = new CountDownLatch(fttList.size());
		
			//for each fluetracktweet, get ConsolidateWeather and calculate/set avgMax and avgMin.
			for(FluTrackTweet ftt:fttList)
			{
				FluTrackTweetWeatherRunnable myRunnable = new FluTrackTweetWeatherRunnable(ftt,latch);
				executor.submit(myRunnable);
			}
			latch.await();
		
			executor.shutdown();
		}
		logger.info("MainWorker thread END name:"+ Thread.currentThread().getName());
		return fttList;
	}
	
	private void findAvgMinMaxForFluTrackTweet(FluTrackTweet ftt)
	{
		MetaWeather mw = queryMetaWeatherByLattlong(ftt.getLatitude()+","+ ftt.getLongitude());
		
		List<ConsolidateWeather> cwList = mw.getConsolidated_weather();
		
		int  avgmin =(int) cwList
			    .stream()
			    .filter(cw -> !isToday(cw.getApplicable_date()))
			    .mapToInt((ConsolidateWeather::getMinTemp))
			    .average()
			    .getAsDouble();
		
		int avgmax =(int) cwList
			    .stream()
			    .filter(cw ->  !isToday(cw.getApplicable_date()))
			    .mapToInt((ConsolidateWeather::getMaxTemp))
			    .average()
			    .getAsDouble();
		
		ftt.setAvgMax(String.valueOf(avgmax));
		ftt.setAvgMin(String.valueOf(avgmin));
	}
	
	private class FluTrackTweetWeatherRunnable implements Runnable {

		FluTrackTweet ftt ;
		CountDownLatch latch;
		
		private FluTrackTweetWeatherRunnable(FluTrackTweet ftt,CountDownLatch latch) {
			this.ftt =ftt;
			this.latch =latch;
		}
		@Override
		public void run() {
			logger.info("Runnable thread START name:"+ Thread.currentThread().getName());
			findAvgMinMaxForFluTrackTweet(ftt);
			latch.countDown();
			logger.info("Runnable thread END name:"+ Thread.currentThread().getName());
		}
	}
	
		
	private List<FluTrackTweet> queryFluTrackTweetsUsingRestTemplate(String uri)
	{
		RestTemplate  restTemplate = new RestTemplate();
		ResponseEntity<List<FluTrackTweet>> response= restTemplate.exchange(uri, HttpMethod.GET,null, new ParameterizedTypeReference<List<FluTrackTweet>>() {
		});
		return response.getBody();
	}
	
		
	private MetaWeather queryMetaWeatherByLattlong( String lattlong )
	{
		Location loc = queryNearestLocationByLattLong( lattlong);
		return queryMetaWeatherByWoeidUsingRestTemplate(loc.getWoeid());
	}	
	
	
	private List<Location> queryLocsByLattLongUsingRestTemplate( String lattlong)
	{
		final String uri=getProperty("URL_GET_LOCATIONS_BY_LATT_LONG")+lattlong;
		RestTemplate  restTemplate = new RestTemplate();
		logger.info("WEB Request QCoord out: "+uri);
		ResponseEntity<List<Location>> response= restTemplate.exchange(uri, HttpMethod.GET,null, new ParameterizedTypeReference<List<Location>>() {
		});
		logger.info("WEB response QCoord back: "+uri);
		return response.getBody();
	}
	
	//Find nearst location using distance
	private Location queryNearestLocationByLattLong(String lattlong)
	{
		List<Location> locList = queryLocsByLattLongUsingRestTemplate(lattlong);
		Comparator<Location> c = (Location l1,Location l2)->Integer.valueOf(l1.getDistance()).compareTo(Integer.valueOf(l2.getDistance()));
		return Collections.min(locList,c);
	}	
	

	private MetaWeather queryMetaWeatherByWoeidUsingRestTemplate( String woeid )
	{
		final String uri=getProperty("URL_GET_META_WEATHER_BY_WOEID")+woeid;
		
		RestTemplate  restTemplate = new RestTemplate();
		logger.info("WEB Request QMeta out: "+uri);
		ResponseEntity<MetaWeather> response= restTemplate.exchange(uri, HttpMethod.GET,null, new ParameterizedTypeReference<MetaWeather>() {
		});
		logger.info("WEB response QMeta back: "+uri);
		MetaWeather mw= response.getBody();
				
		return mw;
	}
	
	
	//get property from properties file
	private String getProperty(String propertyName) {
		return env.getProperty(propertyName);
	}
			
	//test applicable_date from ConsolidateWeather to filter the weather forecast entries. 
	private boolean isToday(String date)
	{
		//2018-08-01
		String dateFormat = getProperty("DATE_FORMAT"); 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		LocalDate today =LocalDate.now();
		return date.equals(today.format(formatter));
	}
	
	/**
	 * search for locations near by a given lattitude and longitude,
	 * a location is identified by Woeid: Where On Earth ID
	 * @param lattlong in the format of comma separated lattitude and longitude e.g. "36.96,-122.02".
	 * @return a list of locations including Woeid.
	 */
	@RequestMapping("/getLocationsByLattLong")
	public List<Location> getLocationsByLattLong(@RequestParam(name="lattlong") String lattlong)
	{
		return queryLocsByLattLongUsingRestTemplate(lattlong);
	}
	
	@RequestMapping("/getNearestLocationByLattLong")
	public Location getNearestLocationByLattLong(@RequestParam(name="lattlong") String lattlong)
	{
		return queryNearestLocationByLattLong(lattlong);
	}
	
	@RequestMapping("/getMetaWeatherByLattlong")
	public MetaWeather getMetaWeatherByLattlong(@RequestParam(name="lattlong") String lattlong )
	{
		return queryMetaWeatherByLattlong(lattlong);
	}
	
	@RequestMapping("/getFluTrackTweets")
	private List<FluTrackTweet> getFluTrackTweets(@RequestParam(name="s") String s,@RequestParam(name="limit") String limit)
	{
		final String uri=getProperty("URL_GET_FLU_TRACK_TWEETS") + s + "&&limit="+ limit;
		return queryFluTrackTweetsUsingRestTemplate(uri);
	}

}

