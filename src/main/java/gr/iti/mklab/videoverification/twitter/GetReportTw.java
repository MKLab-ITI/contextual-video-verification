package gr.iti.mklab.videoverification.twitter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import gr.iti.mklab.VideoVerificationController;
import gr.iti.mklab.utils.MongoConfiguration;
import gr.iti.mklab.utils.MongoHandler;

public class GetReportTw {
	

	  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	  private static SimpleDateFormat dateFormatnew = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss a Z");
	  static String processing_status = "processing_status";
	  static String mongoDocId = "_id";
	  
	  public GetReportTw(){
	        try {
				MongoConfiguration.load(getClass().getResourceAsStream(VideoVerificationController.mongo_properties_file));
			} catch (ConfigurationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		         
	 }
	
	
public static String getReportTw(String videoId, String fields, String mongo_host) throws MalformedURLException, JSONException{
		
		String twitterResult = null;
		JSONObject obj = new JSONObject();
		JSONObject test = new JSONObject();
		
		MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
	    MongoClient mongoClient = null;
		 
		try {
			mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document queryResult = null;
		MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
		MongoCollection<Document> colTW = database.getCollection(MongoConfiguration.DB_TWITTER_COLLECTION);
		MongoCollection<Document> colTweet = database.getCollection(MongoConfiguration.DB_TWEETS_COLLECTION);
		MongoCollection<Document> colYT = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION);
		
		try {
		    Thread.sleep(1500);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		if (MongoHandler.getInstance().documentsExists(database, MongoConfiguration.DB_TWITTER_COLLECTION, videoId)){	
			System.out.println("tw report " + videoId + " " + MongoHandler.getInstance().documentsExists(database, MongoConfiguration.DB_TWITTER_COLLECTION, videoId));
			/*
			 * Twitter report information	
			 */				
				Document query = new Document(mongoDocId, videoId);
				FindIterable<Document> findIterable = colTW.find(query);
				/*
				 * Check if message - the video does not exist
				 */					
			while (colTW.find(query).first().getString("processing_status").equalsIgnoreCase("processing")){
				//System.out.println("wait for Twitter method to finish!");
			}
		
				if (findIterable.first().containsKey("message")){
					 queryResult = findIterable.first();
					 twitterResult = queryResult.toJson();
				}else{	
					
					if (MongoHandler.getInstance().documentsExists(database, MongoConfiguration.DB_YOUTUBE_COLLECTION, videoId)){
						/*
						 * For YouTube report retrieve the upload time of the video
						 */
						while (colYT.find(query).first().getString("processing_status").equalsIgnoreCase("processing")){
							//System.out.println("wait for YouTube method to finish!");
						}
						
							Document Qupload_time = new Document(mongoDocId, videoId);		
							dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//Specify your timezone 
			  				String uploadTime = colYT.find(Qupload_time).first().getString("video_upload_time");
			  				//System.out.println("**** video upload time " + uploadTime);
			  			    try
			  			    {
			  			    	test.put("upload_time", dateFormatnew.parse(uploadTime).getTime());	  			      
			  			    } 
			  			    catch (ParseException e) 
			  			    {
			  			        e.printStackTrace();
			  			    }	
					}else{
						test.put("upload_time", 0);
					}
					
					/*
					 * Store aggregate statistics
					 * 
					 */
					Document VideoDoc = findIterable.first();						
					obj.put("number_of_fake_tweets", VideoDoc.getInteger("number_of_fake_tweets"));
					obj.put("number_of_real_tweets", VideoDoc.getInteger("number_of_real_tweets"));
					obj.put("number_of_tweets", VideoDoc.getInteger("number_of_tweets"));
					obj.put("mean_confidence_value", VideoDoc.getDouble("mean_confidence_value"));
					obj.put("std_value", VideoDoc.getDouble("std_value"));
					obj.put("range", VideoDoc.getDouble("range"));
					obj.put("median_value", VideoDoc.getDouble("median_value"));
					obj.put("max_tweet_timestamp", VideoDoc.getString("max_tweet_timestamp")  == null ? "" : VideoDoc.getString("max_tweet_timestamp"));
					obj.put("min_tweet_timestamp", VideoDoc.getString("min_tweet_timestamp") == null ? "" : VideoDoc.getString("min_tweet_timestamp"));
					obj.put("hist", VideoDoc.get("hist") == null ? "" : VideoDoc.get("hist"));
					test.put("aggregate_stats", obj);
					
				  JSONArray jsonArray = new JSONArray();
				  if (VideoDoc.getInteger("number_of_tweets") > 0){						
						Object tweetIds = VideoDoc.get("tweetIds").toString();
						StringTokenizer st2 = new StringTokenizer(tweetIds.toString().replaceAll("[\\[|\\] ]", "").trim(), ",");						  
						while (st2.hasMoreElements()) {
							Object twID = st2.nextElement();
							Document tweetquery = new Document(mongoDocId, twID);
							FindIterable<Document> findIterableTweets = colTweet.find(tweetquery);							
							JSONObject obj2 = new JSONObject(findIterableTweets.first().toJson());
							jsonArray.put(obj2);										    			
						}
					}
					test.put("tweets", jsonArray);
					twitterResult = test.toString();						
				}		
		}else{
			 JSONObject twResult = new JSONObject();
			 twResult.put("message", "Cannot retrieve report for video " + videoId);
			 twitterResult = twResult.toString();
		}
		mongoClient.close();
		return 	twitterResult;		
	}

}
