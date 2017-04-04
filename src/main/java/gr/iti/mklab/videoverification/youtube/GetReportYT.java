package gr.iti.mklab.videoverification.youtube;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
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

/**
 * Create YouTube report
 * @author olgapapa
 *
 */
public class GetReportYT {	
	
	  static String mongoDocId = "_id";
	  
		 public GetReportYT(){
		        try {
					MongoConfiguration.load(getClass().getResourceAsStream(VideoVerificationController.mongo_properties_file));
				} catch (ConfigurationException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		         
		 }	  

	 public static String getReporYT(String videoId, String fields, String mongo_host) throws MalformedURLException, JSONException{
		 String youtubereport = "";
		 
		   MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
	       MongoClient mongoClient = null;
		 
			try {
				  mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
			MongoCollection<Document> colYT = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION);
			
			try {
			    Thread.sleep(1500);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}			
			if (MongoHandler.getInstance().documentsExists(database, MongoConfiguration.DB_YOUTUBE_COLLECTION, videoId)){
				Document query = new Document(mongoDocId, videoId);
				while (colYT.find(query).first().getString("processing_status").equalsIgnoreCase("processing")){
					//System.out.println("wait for YouTube method to finish!");
				}				
					FindIterable<Document> findIterable = colYT.find(query);				
					Document queryResult = findIterable.first();
					youtubereport = queryResult.toJson();
			}else{
				 JSONObject ytResult = new JSONObject();
				 ytResult.put("message", "Cannot retrieve report for video " + videoId);
				 youtubereport = ytResult.toString();				 
			}
			mongoClient.close();		       
			return 	youtubereport;				 
		 }
}
