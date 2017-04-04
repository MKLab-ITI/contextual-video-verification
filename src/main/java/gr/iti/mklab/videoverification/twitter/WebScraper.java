package gr.iti.mklab.videoverification.twitter;

import gr.iti.mklab.utils.Configuration;
import gr.iti.mklab.utils.MongoConfiguration;
import gr.iti.mklab.utils.MongoHandler;
import gr.iti.mklab.utils.vUtils;
import gr.iti.mklab.videoverification.youtube.VideoVerifier;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.google.api.services.youtube.model.Video;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import gr.iti.mklab.VideoVerificationController;
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
	
	private static String processing_status = "processing_status";
	private static String mongoDocId = "_id";
	
	
	 public WebScraper(){
	        try {
				Configuration.load(getClass().getResourceAsStream(VideoVerificationController.properties_file));
				MongoConfiguration.load(getClass().getResourceAsStream(VideoVerificationController.mongo_properties_file));
			} catch (ConfigurationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
	 }
	
/*	public static void openTestSite(String chromedriver, String videoId) {
		System.setProperty("webdriver.chrome.driver", chromedriver);
		System.setProperty("webdriver.chrome.logfile", "/logs/driverchromedriver.log");
		System.out.println("chromedriver " + chromedriver);
		//driver = new ChromeDriver();
		//driver.navigate().to("https://twitter.com/search?f=tweets&q=https://www.youtube.com/watch?v=" + videoId +"&src=typd");	  
	}
	public static void closeBrowser() {
		//driver.close();
	}*/
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
	
	public static TweetVerificationInfo updateScrapeTweetsStoreOne2(String videoId) throws InterruptedException, IOException {
		System.out.println("****update SCRAPING VIDEO ID " + videoId + "\n");
		Bson filter = null;
		Bson newValue = null;
		Bson updateOperationDocument = null;
			
		MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
		MongoClient mongoClient = null;
		try {
			//mongoClient = new MongoClient(MongoConfiguration.MONGO_HOST, 27017);
			 mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
		MongoCollection<Document> colTW = database.getCollection(MongoConfiguration.DB_TWITTER_COLLECTION);
				
		 filter = new Document(mongoDocId, videoId);
		 newValue = new Document(processing_status, "processing");
		 updateOperationDocument = new Document("$set", newValue);
		 colTW.updateOne(filter, updateOperationDocument);
		 
			// retrieve the video
			Video video = VideoVerifier.retrieveVideoById(videoId);

			if (video.isEmpty()) {
				   colTW.updateOne(filter, new Document("$set", new Document("message", "This video cannot be found!")));
				   newValue = new Document(processing_status, "done");
				   updateOperationDocument = new Document("$set", newValue);
				   colTW.updateOne(filter, updateOperationDocument);
			}else{
				 
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
					WebDriver driver;
					System.setProperty("webdriver.chrome.driver", Configuration.CHROME_DRIVER);
					//System.setProperty("webdriver.chrome.logfile", "D:/Reveal/Java_workspace_Reveal/VideoVerificationAsync/driverchromedriver.log");
					System.out.println("chromedriver path" + Configuration.CHROME_DRIVER);
					driver = new ChromeDriver();
					driver.navigate().to("https://twitter.com/search?f=tweets&q=https://www.youtube.com/watch?v=" + videoId +"&src=typd");
							//openTestSite(chromedriver, videoId);
							//System.setProperty("webdriver.chrome.driver", chromedriver);
							//System.out.println("chromedriver " + chromedriver);
							//driver = new ChromeDriver();
							//driver.navigate().to("https://twitter.com/search?f=tweets&q=https://www.youtube.com/watch?v=" +videoId+"&src=typd");
							
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
										//System.out.println("in while");
										firstElement.sendKeys(Keys.END);
										Thread.sleep(2000);
										j++;						
													
										elementsNew = driver.findElements(By.cssSelector("#stream-items-id li div.original-tweet"));
										//System.out.println("Number of elements - number of tweets maybe " + elementsNew.size());
										for (WebElement elem:elementsNew) {
											//System.out.println("scraping id " + elem.getAttribute("data-item-id"));
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
							driver.close();
							//driver.quit();
							//closeBrowser();	
						
							if (!ids.isEmpty()){
								tweetVerInfo = searchTweeter(videoId, ids, database);
								
							}else{
								System.out.println("NO tweets found ***");					
								tweetVerInfo.numberOfVerifiedTweets = 0;
								   colTW.updateOne(filter, new Document("$set", new Document("number_of_real_tweets", tweetVerInfo.numberOfReals)));
								   colTW.updateOne(filter, new Document("$set", new Document("number_of_fake_tweets", tweetVerInfo.numebOfFakes)));
								   colTW.updateOne(filter, new Document("$set", new Document("number_of_tweets", tweetVerInfo.numberOfVerifiedTweets)));
								   colTW.updateOne(filter, new Document("$set", new Document("mean_confidence_value", tweetVerInfo.mean_score)));
								   colTW.updateOne(filter, new Document("$set", new Document("std_value", tweetVerInfo.st_dev_score)));
								   colTW.updateOne(filter, new Document("$set", new Document("median_value", tweetVerInfo.median_score)));
								   colTW.updateOne(filter, new Document("$set", new Document("range", tweetVerInfo.range)));
								   colTW.updateOne(filter, new Document("$set", new Document("hist", tweetVerInfo.hist)));
								   colTW.updateOne(filter, new Document("$set", new Document("tweetIds", tweetVerInfo.tweetIds)));
								   colTW.updateOne(filter, new Document("$set", new Document("max_tweet_timestamp", tweetVerInfo.max_tweet_timestamp)));
								   colTW.updateOne(filter, new Document("$set", new Document("min_tweet_timestamp", tweetVerInfo.min_tweet_timestamp)));
								   newValue = new Document(processing_status, "done");
								   updateOperationDocument = new Document("$set", newValue);
								   colTW.updateOne(filter, updateOperationDocument);
							}
							System.out.println("END scrapping ***********************************\n");	
			}	
					mongoClient.close();
		 
		return null;		
	}
			
	public static TweetVerificationInfo scrapeTweetsStoreOne2(String videoId) throws InterruptedException, IOException {
		System.out.println("****SCRAPING VIDEO ID " + videoId + "\n");
		
		MongoClient mongoClient = null;
		MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
		try {
			//mongoClient = new MongoClient(MongoConfiguration.MONGO_HOST, 27017);
			 mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Bson filter = null;
		Bson newValue = null;
		Bson updateOperationDocument = null;
		
		MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
		MongoCollection<Document> colTW = database.getCollection(MongoConfiguration.DB_TWITTER_COLLECTION);
		
		Document d = new Document(mongoDocId, videoId);		
		 filter = new Document(mongoDocId, videoId);
		 d.append(processing_status, "processing");		        	
		 colTW.insertOne(d);
		 TweetVerificationInfo tweetVerInfo = new TweetVerificationInfo();
		 
			// retrieve the video
			Video video = VideoVerifier.retrieveVideoById(videoId);

			if (video.isEmpty()) {
				   colTW.updateOne(filter, new Document("$set", new Document("message", "This video cannot be found!")));
				   newValue = new Document(processing_status, "done");
				   updateOperationDocument = new Document("$set", newValue);
				   colTW.updateOne(filter, updateOperationDocument);
			}else{
				
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
			
			WebDriver driver;
			System.setProperty("webdriver.chrome.driver", Configuration.CHROME_DRIVER);
			//System.setProperty("webdriver.chrome.logfile", "D:/Reveal/Java_workspace_Reveal/VideoVerificationAsync/driverchromedriver.log");
			System.out.println("chromedriver path " + Configuration.CHROME_DRIVER);
			driver = new ChromeDriver();
			driver.navigate().to("https://twitter.com/search?f=tweets&q=https://www.youtube.com/watch?v=" + videoId +"&src=typd");
				
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
								//System.out.println("in while");
								firstElement.sendKeys(Keys.END);
								Thread.sleep(2000);
								j++;						
											
								elementsNew = driver.findElements(By.cssSelector("#stream-items-id li div.original-tweet"));
								//System.out.println("Number of elements - number of tweets maybe " + elementsNew.size());
								for (WebElement elem:elementsNew) {
									//System.out.println("scraping id " + elem.getAttribute("data-item-id"));
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
					driver.close();
					//driver.quit();
				
					if (!ids.isEmpty()){
						tweetVerInfo = searchTweeter(videoId, ids, database);
						
					}else{
						System.out.println("NO tweets found ***");					
						tweetVerInfo.numberOfVerifiedTweets = 0;
						  colTW.updateOne(filter, new Document("$set", new Document("number_of_real_tweets", tweetVerInfo.numberOfReals)));
						   colTW.updateOne(filter, new Document("$set", new Document("number_of_fake_tweets", tweetVerInfo.numebOfFakes)));
						   colTW.updateOne(filter, new Document("$set", new Document("number_of_tweets", tweetVerInfo.numberOfVerifiedTweets)));
						   colTW.updateOne(filter, new Document("$set", new Document("mean_confidence_value", tweetVerInfo.mean_score)));
						   colTW.updateOne(filter, new Document("$set", new Document("std_value", tweetVerInfo.st_dev_score)));
						   colTW.updateOne(filter, new Document("$set", new Document("median_value", tweetVerInfo.median_score)));
						   colTW.updateOne(filter, new Document("$set", new Document("range", tweetVerInfo.range)));
						   colTW.updateOne(filter, new Document("$set", new Document("hist", tweetVerInfo.hist)));
						   colTW.updateOne(filter, new Document("$set", new Document("tweetIds", tweetVerInfo.tweetIds)));
						   colTW.updateOne(filter, new Document("$set", new Document("max_tweet_timestamp", tweetVerInfo.max_tweet_timestamp)));
						   colTW.updateOne(filter, new Document("$set", new Document("min_tweet_timestamp", tweetVerInfo.min_tweet_timestamp)));
						   newValue = new Document(processing_status, "done");
						   updateOperationDocument = new Document("$set", newValue);
						   colTW.updateOne(filter, updateOperationDocument);
					}
					System.out.println("END scrapping ***********************************\n");	
			}
			//	closeBrowser();		
				mongoClient.close();
		return tweetVerInfo;
	}
	
	public static TweetVerificationInfo searchTweeter(String videoId, Set<String> ids, MongoDatabase database) throws IOException, InterruptedException{
		System.out.println("*****Aggregate Statistics");
		String predicted_value = "predicted_value";
		String confidence_value = "confidence_value"; 
		String fake = "fake";
		String mongoDocId = "_id";
		Bson filter = null;
		Bson newValue = null;
		Bson updateOperationDocument = null;
		
		TweetVerificationInfo tweetVerInfo = new TweetVerificationInfo();
		List<String> is_verified = new ArrayList<String>();		
		String tweetVerOutput = "";		
		List<String> tweetTimestamps = new ArrayList<String>();
		List<Double> confidence_scores = new ArrayList<Double>();
		int cnt_fake = 0;
		int cnt_real = 0;
		MongoCollection<Document> colTW = database.getCollection(MongoConfiguration.DB_TWITTER_COLLECTION);
		MongoCollection<Document> colTweets = database.getCollection(MongoConfiguration.DB_TWEETS_COLLECTION);
		
		int numCalls  = 0;
		
		BasicDBList tweetsids = new BasicDBList();
		for (String currId : ids) {
			System.out.println("Tweet ID :: " + currId);
			String tweetVerified = "";
			tweetsids.add(currId);
			 double confScore = 0;
			if (MongoHandler.getInstance().documentsExists(database, MongoConfiguration.DB_TWEETS_COLLECTION, currId)){
				FindIterable<Document> iterable = database.getCollection(MongoConfiguration.DB_TWEETS_COLLECTION).find(new Document(mongoDocId, currId));
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
						// System.out.println("twitter API finished");
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
		 		filter = new Document(mongoDocId, videoId);	
				if (!is_verified.isEmpty()){
				
					tweetVerInfo.videoExist = "true";
					tweetVerInfo.numberOfReals = cnt_real;
					tweetVerInfo.numebOfFakes = cnt_fake;
					tweetVerInfo.numberOfVerifiedTweets = cnt_real + cnt_fake;
					long[] unixArray = new long[tweetTimestamps.size()];
					int unix_cnt = 0;
					for (String tweetTime : tweetTimestamps) {
						//System.out.println("****tweetTime " + tweetTime);
						//dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));//Specify your timezone 
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
					
					 								  
					   colTW.updateOne(filter, new Document("$set", new Document("number_of_real_tweets", tweetVerInfo.numberOfReals)));
					   colTW.updateOne(filter, new Document("$set", new Document("number_of_fake_tweets", tweetVerInfo.numebOfFakes)));
					   colTW.updateOne(filter, new Document("$set", new Document("number_of_tweets", tweetVerInfo.numberOfVerifiedTweets)));
					   colTW.updateOne(filter, new Document("$set", new Document("mean_confidence_value", tweetVerInfo.mean_score)));
					   colTW.updateOne(filter, new Document("$set", new Document("std_value", tweetVerInfo.st_dev_score)));
					   colTW.updateOne(filter, new Document("$set", new Document("median_value", tweetVerInfo.median_score)));
					   colTW.updateOne(filter, new Document("$set", new Document("range", tweetVerInfo.range)));
					   colTW.updateOne(filter, new Document("$set", new Document("hist", tweetVerInfo.hist)));
					   colTW.updateOne(filter, new Document("$set", new Document("tweetIds", tweetVerInfo.tweetIds)));
					   colTW.updateOne(filter, new Document("$set", new Document("max_tweet_timestamp", tweetVerInfo.max_tweet_timestamp)));
					   colTW.updateOne(filter, new Document("$set", new Document("min_tweet_timestamp", tweetVerInfo.min_tweet_timestamp)));
					/*   newValue = new Document(processing_status, "done");
					   updateOperationDocument = new Document("$set", newValue);
					   colTW.updateOne(filter, updateOperationDocument);*/
				}else{
					tweetVerInfo.videoExist = "exist";
					System.out.println("Tweet already exist in db");
				}	
				  /* filter = new Document(mongoDocId, videoId);									  
				   colTW.updateOne(filter, new Document("$set", new Document("number_of_real_tweets", tweetVerInfo.numberOfReals)));
				   colTW.updateOne(filter, new Document("$set", new Document("number_of_fake_tweets", tweetVerInfo.numebOfFakes)));
				   colTW.updateOne(filter, new Document("$set", new Document("number_of_tweets", tweetVerInfo.numberOfVerifiedTweets)));
				   colTW.updateOne(filter, new Document("$set", new Document("mean_confidence_value", tweetVerInfo.mean_score)));
				   colTW.updateOne(filter, new Document("$set", new Document("std_value", tweetVerInfo.st_dev_score)));
				   colTW.updateOne(filter, new Document("$set", new Document("median_value", tweetVerInfo.median_score)));
				   colTW.updateOne(filter, new Document("$set", new Document("range", tweetVerInfo.range)));
				   colTW.updateOne(filter, new Document("$set", new Document("hist", tweetVerInfo.hist)));
				   colTW.updateOne(filter, new Document("$set", new Document("tweetIds", tweetVerInfo.tweetIds)));
				   colTW.updateOne(filter, new Document("$set", new Document("max_tweet_timestamp", tweetVerInfo.max_tweet_timestamp)));
				   colTW.updateOne(filter, new Document("$set", new Document("min_tweet_timestamp", tweetVerInfo.min_tweet_timestamp)));
				   */
				   newValue = new Document(processing_status, "done");
				   updateOperationDocument = new Document("$set", newValue);
				   colTW.updateOne(filter, updateOperationDocument);
		return tweetVerInfo;
	}
	
	
	/*public static void main(String[] args) throws InterruptedException {
		WebScraper webScraper = new WebScraper();
		webScraper.openTestSite();
		
		List<String> ids = webScraper.getTweetIds();
			
		webScraper.closeBrowser();
		
		System.out.println(ids.size());
	}*/
}