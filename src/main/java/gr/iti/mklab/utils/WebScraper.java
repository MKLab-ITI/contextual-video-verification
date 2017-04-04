package gr.iti.mklab.utils;

import gr.iti.mklab.utils.vUtils;
import gr.iti.mklab.videoverification.twitter.TweetVerificationInfo;
import gr.iti.mklab.videoverification.twitter.TweetVerificationClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.Set;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import gr.iti.mklab.twitterapi.TwitterAPIConnector;


/**
 * Class for web scraping
 * @author boididou
 * 08.07.2016
 * 
 * modified 16.12.2016
 * @author olgapapa
 * 
 * 1. Call tweet verification service - return fake/real label and the confidence score
 * 2. Extract number of fake and real tweets
 * 3. Calculate mean, median, std, range, histogram of the confidence scores
 * 
 */

public class WebScraper {
	
	public static WebDriver driver;
	static String dbTwitter = "TwitterVideo";
	static String dbTweets = "TwitterTweets";
	
	public static void openTestSite(String chromedriver, String videoId) {
		System.setProperty("webdriver.chrome.driver", chromedriver);
		driver = new ChromeDriver();
		driver.navigate().to("https://twitter.com/search?f=tweets&q=https://www.youtube.com/watch?v=" + videoId +"&src=typd");
	}
	public static void closeBrowser() {
		driver.close();
	}
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
			
	public static TweetVerificationInfo scrapeTweetsStoreOne2(String videoId, String chromedriver, MongoDatabase database) throws InterruptedException, IOException {
		System.out.println("****SCRAPING VIDEO ID " + videoId + "\n");
		
		TweetVerificationInfo tweetVerInfo = new TweetVerificationInfo();
		// initialize 
		tweetVerInfo.videoExist = "true";
		tweetVerInfo.mean_score = 0;
		tweetVerInfo.numberOfReals = 0;
		tweetVerInfo.numberOfVerifiedTweets = 0;
		tweetVerInfo.numebOfFakes = 0;
		tweetVerInfo.median_score = 0;
		tweetVerInfo.range = 0;
		tweetVerInfo.st_dev_score = 0;
		tweetVerInfo.hist = null;
		tweetVerInfo.tweetIds = null;
				
				openTestSite(chromedriver, videoId);
			
				Set<String> ids = new HashSet<String>();
				List<WebElement> elements = new ArrayList<WebElement>();
				List<WebElement> elementsNew = new ArrayList<WebElement>();
								
				try {
					int i=0;				
					while (i<1) {					
						WebElement firstElement = driver.findElement(By.tagName("a"));
						elements = driver.findElements(By.cssSelector("#stream-items-id li div.original-tweet"));					
						firstElement.sendKeys(Keys.END);					
						elementsNew = driver.findElements(By.cssSelector("#stream-items-id li div.original-tweet"));		
						int j=0;
						while (elements.size()==elementsNew.size() && j<7) {					
							firstElement.sendKeys(Keys.END);
							Thread.sleep(2000);
							j++;											
							elementsNew = driver.findElements(By.cssSelector("#stream-items-id li div.original-tweet"));
							for (WebElement elem:elementsNew) {
								ids.add(elem.getAttribute("data-item-id"));							
							}
							i++;										
						}//end inner while					
						if (elements.size()==elementsNew.size()) {
							break;
						}			
					}//end outer while
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				if (!ids.isEmpty()){
					tweetVerInfo = searchTweeter(videoId, ids, database);
				}else{
					System.out.println("NO tweets found ***");					
					tweetVerInfo.numberOfVerifiedTweets = 0;
				}
				System.out.println("END scrapping ***********************************\n");	
				closeBrowser();			
		return tweetVerInfo;
	}
	
	public static TweetVerificationInfo searchTweeter(String videoId, Set<String> ids, MongoDatabase database) throws IOException, InterruptedException{
		
		String predicted_value = "predicted_value";
		String confidence_value = "confidence_value"; 
		String fake = "fake";
		String mongoDocId = "_id";
		
		TweetVerificationInfo tweetVerInfo = new TweetVerificationInfo();
		List<String> is_verified = new ArrayList<String>();		
		String tweetVerOutput = "";		
		List<String> tweetTimestamps = new ArrayList<String>();
		List<Double> confidence_scores = new ArrayList<Double>();
		int cnt_fake = 0;
		int cnt_real = 0;
		MongoCollection<Document> colTweets = database.getCollection(dbTweets);
		Bson updateOperationDocument = null;
		int numCalls  = 0;
		
		BasicDBList tweetsids = new BasicDBList();
		for (String currId : ids) {
			System.out.println("Tweet ID :: " + currId);
			String tweetVerified = "";
			tweetsids.add(currId);
			 double confScore = 0;
			if (MongoHandler.getInstance().documentsExists(database, dbTweets, currId)){
				FindIterable<Document> iterable = database.getCollection(dbTweets).find(new Document(mongoDocId, currId));
				tweetVerified = iterable.first().getString(predicted_value);
				
				confScore =  iterable.first().getDouble(confidence_value);
				if (tweetVerified.equalsIgnoreCase(fake)){
					confScore = 1 - confScore;
					cnt_fake = cnt_fake + 1;
				}else{
					cnt_real = cnt_real + 1;
				}				
				confidence_scores.add(confScore);
			}else{				
				try {
					tweetVerOutput =  TweetVerificationClient.verifyTweet(currId, Configuration.TWEET_VERIFICATION_SERVICE_URL);
					JSONObject jsonObject = new JSONObject(tweetVerOutput);
					tweetVerified = jsonObject.getString(predicted_value);
					is_verified.add(tweetVerified);		
					if (tweetVerified.equalsIgnoreCase(fake)){
						confScore = 1 - confScore;
						cnt_fake = cnt_fake + 1;
					}else{
						cnt_real = cnt_real + 1;
					}
					confScore = jsonObject.getDouble(confidence_value);
					if (tweetVerified.equalsIgnoreCase(fake))
						confScore = 1 - confScore;
					
					confidence_scores.add(confScore);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (numCalls<179) {
					 try {
						JSONObject json = TwitterAPIConnector.getInstance().getSingleTweetJSON(currId);   
						 tweetTimestamps.add(json.getString("created_at"));		  
						 json.put(fake, tweetVerified);
						 json.put(predicted_value, tweetVerified);
						 json.put(confidence_value, confScore);
						 Document doc = new Document(mongoDocId, currId);
						  String jsonString = doc.toJson();
						  doc = Document.parse(jsonString);			
						  colTweets.insertOne(doc);
						 Object o = JSON.parse(json.toString());
						 DBObject dbObj = (DBObject) o;     		  
						 updateOperationDocument = new Document("$set", dbObj);
						 colTweets.updateOne(doc, updateOperationDocument);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}else {
					Thread.sleep(15 * 60 * 1000);
					Thread.sleep(1000);
					numCalls = 0;
				}
			}		
		}
		tweetVerInfo.tweetIds = tweetsids;
			/*
			 * Calculate aggregate statistics
			 * mean confidence score
			 * median
			 * std
			 * range
			 * histogram 
			 * 
			 */
				if (!is_verified.isEmpty()){
				
					tweetVerInfo.videoExist = "true";
					tweetVerInfo.numberOfReals = cnt_real;
					tweetVerInfo.numebOfFakes = cnt_fake;
					tweetVerInfo.numberOfVerifiedTweets = cnt_real + cnt_fake;
					long[] unixArray = new long[tweetTimestamps.size()];
					int unix_cnt = 0;
					for (String tweetTime : tweetTimestamps) {
		  			    try
		  			    {
		  			    	unixArray[unix_cnt] = dateFormat.parse(tweetTime).getTime();  
		  			        //unixtime=unixtime/1000;
		  			    } 
		  			    catch (ParseException e) 
		  			    {
		  			        e.printStackTrace();
		  			    }
		  			   unix_cnt = unix_cnt+1;
					}
					
					int[] indices = vUtils.insertionSort(unixArray); 
					int max_ind = indices[indices.length-1];
					int min_ind = indices[0];
					tweetVerInfo.max_tweet_timestamp = tweetTimestamps.get(max_ind);
					tweetVerInfo.min_tweet_timestamp = tweetTimestamps.get(min_ind);
									
					if (tweetVerInfo.numberOfVerifiedTweets > 9){
							OptionalDouble average = confidence_scores
						            .stream()
						            .mapToDouble(a -> a)
						            .average();
							tweetVerInfo.mean_score = average.getAsDouble();
							double[] array = new double[confidence_scores.size()];
							for (int l = 0; l< array.length; l++) {
								 array[l] = confidence_scores.get(l);								
							}
							tweetVerInfo.median_score = vUtils.getMedian(array);
							tweetVerInfo.st_dev_score = vUtils.getStdDev(array);
							tweetVerInfo.range = vUtils.getUtils().range(confidence_scores);
							tweetVerInfo.hist = vUtils.getUtils().createhistogram(array, 10);			
					}
				}else{
					tweetVerInfo.videoExist = "exist";
					System.out.println("Tweet already exist in db");
				}		
		return tweetVerInfo;
	}
}