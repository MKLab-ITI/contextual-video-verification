package gr.iti.mklab.twitterapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterAPIHandler {

	private static Properties prop = new Properties();
	private Twitter twitterInstance;

	public static Properties getProperties() {
		return prop;
	}

	public void setProperties(Properties prop) {
		TwitterAPIHandler.prop = prop;
	}

	public Twitter getTwitterInstance() {
		return twitterInstance;
	}

	public void setTwitterInstance(Twitter twitterInstance) {
		this.twitterInstance = twitterInstance;
	}

	// constructor
	public TwitterAPIHandler(String configFilePath) {
		try {
			initializeParameters(configFilePath);
			Twitter twitter = TwitterAPIConnector.getInstance()
					.connectToTwitterAPI();
			setTwitterInstance(twitter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeParameters(String configFilePath) {
		InputStream input = null;

		try {
			input = new FileInputStream(configFilePath);
			getProperties().load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public Status getTweet(String tweetId){

		Twitter twitter = TwitterAPIConnector.getInstance().connectToTwitterAPI();
		Long id = Long.parseLong(tweetId);
		
		Status status;
		
		try {
			status = twitter.showStatus(id);
		} catch (TwitterException e) {
			status = null;
		}

		return status;
	}

	
}
