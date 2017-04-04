package gr.iti.mklab.twitterapi;

import java.util.ArrayList;
import java.util.List;

import gr.iti.mklab.utils.TwitterUtils;

import org.json.JSONObject;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class TwitterAPIConnector {

	
	private static TwitterAPIConnector sInstance = new TwitterAPIConnector();

	public static TwitterAPIConnector getInstance() {
		
		if (sInstance == null) {	
			sInstance = new TwitterAPIConnector();
		}
		return sInstance;

	}
	
	public Twitter connectToTwitterAPI() {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(TwitterUtils.CONSUMER_KEY)
		.setOAuthConsumerSecret(TwitterUtils.CONSUMER_SECRET)
		.setOAuthAccessToken(TwitterUtils.ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(TwitterUtils.ACCESS_TOKEN_SECRET);
		
		cb.setJSONStoreEnabled(true);
					
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
	
		return twitter;
	}
	
	public JSONObject getSingleTweetJSON(String tweetId) throws InterruptedException{
		
		JSONObject json = new JSONObject();
		
		
		Twitter twitter = TwitterAPIConnector.getInstance().connectToTwitterAPI();
	
		Status status;
		try {
			status = twitter.showStatus(Long.parseLong(tweetId));
			
			if (status != null) {
				String jsonString = DataObjectFactory.getRawJSON(status); 	
				json = new JSONObject(jsonString);
			   }//end if
		
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		return json;
		
	}//end method
	
	public List<JSONObject> getTweetsJSONs(List<String> tweetIds) throws InterruptedException{
		
		List<JSONObject> jsons = new ArrayList<JSONObject>();
		
		int numOfCalls = 0;
		Twitter twitter = TwitterAPIConnector.getInstance().connectToTwitterAPI();
		
		for (String tweetId:tweetIds) {
			if (numOfCalls < 179) {
				Status status;
				try {
					status = twitter.showStatus(Long.parseLong(tweetId));
					
					if (status != null) {
						String jsonString = DataObjectFactory.getRawJSON(status); 	
						JSONObject json = new JSONObject(jsonString);
						jsons.add(json); 
					   }//end if
				
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Waiting for 15 minutes...Get some rest or grab some coffee ;)");
				Thread.sleep(15 * 60 * 1000);
				numOfCalls = 0;
			}
			
		}//end for
			
		return jsons;
		
	}//end method
}
