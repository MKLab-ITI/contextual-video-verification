package gr.iti.mklab.videoverification.twitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBList;

public class TweetVerificationInfo {
	
	public int numebOfFakes;
	public int numberOfReals;
	public int numberOfVerifiedTweets;
	public double mean_score;
	public String videoExist;
	public double median_score;
	public double st_dev_score;
	public double range;
	public BasicDBList hist = new BasicDBList();
	public BasicDBList tweetIds = new BasicDBList();
	public String max_tweet_timestamp;
	public String min_tweet_timestamp;
	public List<String> tweetsObj = new ArrayList<String>();
	
	public String toJSONString() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(this);
	}
	
}
