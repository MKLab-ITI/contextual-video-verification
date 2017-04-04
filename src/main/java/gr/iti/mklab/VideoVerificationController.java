package gr.iti.mklab;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import gr.iti.mklab.services.*;
import gr.iti.mklab.utils.Configuration;
import gr.iti.mklab.utils.MongoConfiguration;
import gr.iti.mklab.utils.MongoHandler;
import gr.iti.mklab.utils.vUtils;
import gr.iti.mklab.videoverification.twitter.GetReportTw;
import gr.iti.mklab.videoverification.weather.WeatherInfo;
import gr.iti.mklab.videoverification.youtube.GetReportYT;


/**
 * Resourses: 
 * 
 * verify_video: trigger the verification methods (YouTube search, Twitter search)
 * @param id 
 * 		( YouTube video id)
 * @return 	json format message defining the status of the call
 * 
 * get_ytverification: YouTube response
 * @param id
 * 			( YouTube video id)
 * @return json format response of YouTube
 * 
 * get_twverification: Twitter response
 * @param id
 * 			( YouTube video id)
 * @return	json format response of YouTube
 * 
 * weather: get weather information for specific time 
 * @param location 
 * 			location of the event (country, city, street)
 * 		  time
 * 			timestamp of the time that the event took place
 * @return json format of weather information (temperature etc.)
 * 
 * @author olgapapa
 *
 */


@CrossOrigin(maxAge = 3600)
@EnableWebMvc
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@Controller
public class VideoVerificationController {
	
	/*
	 * Define the properties files
	 */
	
	public static String properties_file = "/remote.properties";
	public static String mongo_properties_file = "/mongoRemote.properties";

	@Autowired
	private VideoVerificationService videoVerificationService;
	

   public VideoVerificationController() {
			   try {
				   Configuration.load(getClass().getResourceAsStream(properties_file));	      
			       MongoConfiguration.load(getClass().getResourceAsStream(mongo_properties_file));
			   } catch (ConfigurationException | IOException e) {
					e.printStackTrace();
					System.out.println("Cannot load configuration files");
				}
	    }	  
	
	@RequestMapping(value =  "/verify_video", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String doSomeWork(@RequestParam(value = "id") String id, 
			@RequestParam(value = "fields", required = false) String fields,
			HttpServletRequest request)  {

		
		String output = "";
		boolean videoExist = false;
		JSONObject statusResult = new JSONObject();
		
    	// Create an instance of SimpleDateFormat used for formatting the string representation of date (day/month/year)
		 DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		// Get the date today using Calendar object.
		 Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string representation of a date with the defined format.
		String reportDate = df.format(today);
          
        System.out.println("** verify_video " + request.getRemoteAddr() + " " + id + " " + reportDate + "\n");		
		
		/*
		 *  * open mongo db with auth
		 */
       MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
       MongoClient mongoClient = null;
    		try { 
				  mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
			} catch (Exception e) {
				e.printStackTrace();
			}	
			
			MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
			MongoCollection<Document> colLog = database.getCollection(MongoConfiguration.LOG_COLLECTION);
			MongoCollection<Document> colTW = database.getCollection(MongoConfiguration.DB_TWITTER_COLLECTION);
			MongoCollection<Document> colYT = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION);
					
			if (MongoHandler.getInstance().documentsExists(database, MongoConfiguration.LOG_COLLECTION, id)){
				videoExist = true;
				System.out.println("Video " + id + " is already processed!");				
				//output = "Video https://www.youtube.com/watch?v=" + id + " is processed!";				
				Document first_time_processed = new Document("_id", id);		
				String videoURLExistCheck = colYT.find(first_time_processed).first().getString("message");
				if (videoURLExistCheck != null){
					output = "THIS_VIDEO_CANNOT_BE_FOUND";
					System.out.println("This video cannot be found!");
					statusResult.put("status", output);
					statusResult.put("YouTube response", "");
					statusResult.put("Twitter response", "");					
				}else{						
					System.out.println("Check_for_new_information!");
						boolean yt_process = colYT.find(first_time_processed).first().getString("processing_status").equalsIgnoreCase("processing");
						boolean tw_process = colTW.find(first_time_processed).first().getString("processing_status").equalsIgnoreCase("processing");
						
						if (yt_process && tw_process){		
							output = "Processing";
							System.out.println("Processing!");
							statusResult.put("status", output);
							statusResult.put("Twitter response", "");
							statusResult.put("YouTube response", "");
						}else if (tw_process){
							output = "YOUTUBE_COMPLETED_TWITTER_PROCESSING";
							statusResult.put("status", output);
							statusResult.put("YouTube response", "http://" + Configuration.REPORT_BASE_URL + "/get_ytverification?id=" + id);
							statusResult.put("Twitter response", "");
						}else if (yt_process){
							output = "TWITTER_COMPLETED_YOUTUBE_PROCESSING";
							statusResult.put("status", output);
							statusResult.put("YouTube response", "");
							statusResult.put("Twitter response", "http://" + Configuration.REPORT_BASE_URL + "/get_twverification?id=" + id);
						}else{								
								String uploadTime = colLog.find(first_time_processed).first().getString("verification_report_date");
								long time_difference = 0;
								try {
									time_difference = vUtils.getUtils().getDateDiff(df.parse(uploadTime), df.parse(reportDate),  TimeUnit.MINUTES);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								// if video is sent after a day - process it again - check for new information
								if (time_difference > 1440){
									System.out.println("Above_threshold_reprocess!");
									output = "VIDEO_PROCESSED_PREVIOUSLY_REPROCESS_FOR_NEW_INFORMATION_IF_EXITS";
									Bson newValue = new Document("verification_report_date", reportDate);
									Bson updateOperationDocument = new Document("$set", newValue);
									colLog.updateOne(first_time_processed, updateOperationDocument);
									statusResult.put("status", output);
									statusResult.put("YouTube response", "");
									statusResult.put("Twitter response", "");
									videoVerificationService.createReport(id, fields, videoExist);
								}else{
									output = "VIDEO_PROCESSING_DONE";
									statusResult.put("status", output);
									statusResult.put("YouTube response", "http://" + Configuration.REPORT_BASE_URL + "/get_ytverification?id=" + id);
									statusResult.put("Twitter response", "http://" + Configuration.REPORT_BASE_URL + "/get_twverification?id=" + id);
								}
						}
				}
			}else{
				System.out.println("New video");
				Document d = new Document("_id", id);
				d.append("verification_report_date", reportDate);
				colLog.insertOne(d);
				output = "YOUR_REQUEST_HAS_BEEN_QUEUED";				
				statusResult.put("status", output);
				statusResult.put("YouTube response", "");
				statusResult.put("Twitter response", "");
				videoVerificationService.createReport(id, fields, videoExist);
			}			
			mongoClient.close();
			System.out.println("End verify video" + id + " - " + output);
		return statusResult.toString();
	}
	 		  
		  /*
			 * Get YouTube search results  
			 */
			
			@RequestMapping(value = "/get_ytverification", method = RequestMethod.GET, produces = {"application/json; charset=UTF-8"})//MediaType.APPLICATION_JSON_VALUE)
			@ResponseBody
			public String getYTVerificationResult(@RequestParam(value = "id") String id, 
					@RequestParam(value = "fields", required = false) String fields, 
					 HttpServletRequest request) throws Exception {
				String result_string;
								
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
					Date today = Calendar.getInstance().getTime();        
					String reportDate = df.format(today);			        
			        System.out.println("** ytverification " + request.getRemoteAddr() + " " + id + " " + reportDate + "\n");	
			        result_string = GetReportYT.getReporYT(id, fields, Configuration.MONGO_HOST);
		       return result_string;
			}
			
				
			/*
			 * Get Twitter search results 
			 */
			
			@RequestMapping(value = "/get_twverification", method = RequestMethod.GET, produces = {"application/json; charset=UTF-8"})
			@ResponseBody
			public String getTwVerificationResult(@RequestParam(value = "id") String id, @RequestParam(value = "fields", required = false) String fields,  HttpServletRequest request) throws Exception {
				String result;
				
					    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
						    Date today = Calendar.getInstance().getTime();        
							String reportDate = df.format(today);
							System.out.println("** twverification " + request.getRemoteAddr() + " " + id + " " + reportDate + "\n");	
					        result = GetReportTw.getReportTw(id, fields, Configuration.MONGO_HOST);	
				return result;
			}	
			
			
			@RequestMapping(value = "/weather", method = RequestMethod.GET, produces = {"application/json; charset=UTF-8"} )
			@ResponseBody
			public String requestWeather(@RequestParam(value = "id", required = false) String id,
											@RequestParam(value = "location") String location, 
											@RequestParam(value = "time") long time,
								HttpServletRequest request) throws Exception {
				System.out.println("** Weather report ");
				String weatherResult = "";
				weatherResult = WeatherInfo.getWeather(id, location, time, 
									Configuration.MONGO_HOST, Configuration.WEATHER_API_KEY, 
									Configuration.GOOGLE_GEO_API_KEY);
				return weatherResult;
			}
			
			@RequestMapping(value = "/", method = RequestMethod.GET,  produces = {"application/json; charset=UTF-8"})
			@ResponseBody
			public String testIfServiceIsOnline() throws Exception {
				System.out.println("Context Aggregation and Analysis Online!");
				JSONObject statusResult = new JSONObject();
				statusResult.put("title", "Context_Aggregation_and_Analysis_Online");
				statusResult.put("resources", "verify_video, get_ytverification, get_twverification, weather");
				statusResult.put("contact", "Olga Papadopoulou");
				statusResult.put("mail", "olgapapa@iti.gr");
				
				return statusResult.toString();
			}
			
}
