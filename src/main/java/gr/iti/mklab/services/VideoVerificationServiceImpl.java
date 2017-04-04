package gr.iti.mklab.services;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import gr.iti.mklab.videoverification.youtube.VideoVerifier;
import gr.iti.mklab.videoverification.twitter.WebScraper;

/**
 * Implements VideoVerificationService
 * Create threads in order to execute YouTube and Twitter search simultaneously.
 * 
 * @author olgapapa
 *
 */

@Service
public class VideoVerificationServiceImpl implements VideoVerificationService {

	 // Suppress MongoDB logging
    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);
    static {
        root.setLevel(Level.WARN);
    }	
	
	@Override
	@Async("workExecutor")
	public void createReport(String id, String fields, boolean videoExist) {	
		
	
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
		if (videoExist){
			System.out.println("Existing video video!");
			YoutubeInfoUptResultsThread ytresults = new YoutubeInfoUptResultsThread(id, fields);
	        Future ytFuture = threadpool.submit(ytresults);
	        
	        TwitterInfoUptResultsThread twresults = new TwitterInfoUptResultsThread(id);
	        Future twFuture = threadpool.submit(twresults);
		}else{
			System.out.println("New video!");
			
			YoutubeInfoResultsThread ytresults = new YoutubeInfoResultsThread(id, fields);
	        Future ytFuture = threadpool.submit(ytresults);
	        
	        TwitterInfoResultsThread twresults = new TwitterInfoResultsThread(id);
	        Future twFuture = threadpool.submit(twresults);
		}
	}	
	
	public static class YoutubeInfoResultsThread implements Runnable {
		  String videoId ="";
		  String fields = "";
		  public YoutubeInfoResultsThread(String videoId, String fields){
			  this.videoId =videoId;	
			  this.fields = fields;			
		  }	 
		  
			@Override
			public void run() {
			  try {
	               Calculation();
	          } catch (IOException e) {
	              e.printStackTrace();
	          }	          
			}
			public void Calculation() throws IOException {
				try {
					VideoVerifier.verifyVideo(videoId, fields);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				 
			}	
	  }
	
	public static class YoutubeInfoUptResultsThread implements Runnable {
		  String videoId ="";
		  String fields = "";
		  public YoutubeInfoUptResultsThread(String videoId, String fields){
			  this.videoId =videoId;	
			  this.fields = fields;
		  }	 
		  
			@Override
			public void run() {
			  try {
	               Calculation();
	          } catch (IOException e) {
	              e.printStackTrace();
	          }	          
			}
			public void Calculation() throws IOException {
				try {
					VideoVerifier.updateVerifyVideo(videoId, fields);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				 
			}	
	  }
	
	  public static class TwitterInfoResultsThread implements Runnable{
		  String videoId ="";
		 
		 public TwitterInfoResultsThread(String videoId){
			  this.videoId =videoId;
		  }	  

			@Override
			public void run(){
			  try {
	              twCalculation();
	          } catch (IOException e) {
	              e.printStackTrace();
	          } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	          
			}
			public void twCalculation() throws IOException, InterruptedException {
				WebScraper.scrapeTweetsStoreOne2(videoId);				
			}		
		  
	  }
	  
	  public static class TwitterInfoUptResultsThread implements Runnable{
		  String videoId ="";		
		 public TwitterInfoUptResultsThread(String videoId){
			  this.videoId =videoId;
		  }	  

			@Override
			public void run(){
			  try {
	              twCalculation();
	          } catch (IOException e) {
	              e.printStackTrace();
	          } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	          
			}
			public void twCalculation() throws IOException, InterruptedException {
				WebScraper.updateScrapeTweetsStoreOne2(videoId);				
			}		
		  
	  }

	}
