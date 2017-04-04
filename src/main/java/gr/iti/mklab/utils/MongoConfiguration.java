package gr.iti.mklab.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Mongodb configuration
 * Authentication is used - provide user and password
 * @author olgapapa
 *
 */

public class MongoConfiguration {

	    public static String MONGO_HOST; // mogno host
	    public static String USER; // username
	    public static String PWD; // user password 
	    public static String VIDEO_CONTEXT_DB; // contextual verification database
	    public static String VIDEO_CONTEXT_DB_FEATURES; // contextual verification database
	    public static String VIDEO_CONTEXT_DB_RESULTS;
	    public static String LOG_COLLECTION; // collection for storing log information 
	    public static String DB_YOUTUBE_COLLECTION; // youtube collection
	    public static String DB_TWITTER_COLLECTION; // twitter collection
	    public static String ADMIN_DB; // admin database (for authentication)
	    public static String DB_TWEETS_COLLECTION;  // tweet collection
	   
	    public static void load(String file) throws ConfigurationException {
	        PropertiesConfiguration conf = new PropertiesConfiguration(file);
	        MONGO_HOST = conf.getString("mongohostip");
	        USER = conf.getString("username");
	        PWD =  conf.getString("password");	 
	        ADMIN_DB = conf.getString("admin_db");
	        VIDEO_CONTEXT_DB =  conf.getString("video_contex_db");
	        LOG_COLLECTION =  conf.getString("log_collection");
	        DB_YOUTUBE_COLLECTION =  conf.getString("db_youtube_collection");
	        DB_TWITTER_COLLECTION =  conf.getString("db_twitter_collection");
	        DB_TWEETS_COLLECTION =  conf.getString("db_tweets_collection");
	        VIDEO_CONTEXT_DB_FEATURES = conf.getString("video_contex_db_features");
	        VIDEO_CONTEXT_DB_RESULTS = conf.getString("video_context_db_results");	        
	    }

	    public static void load(InputStream stream) throws ConfigurationException, IOException {
	        Properties conf = new Properties();
	        conf.load(stream);	
	        MONGO_HOST = conf.getProperty("mongohostip");
	        USER = conf.getProperty("username");
	        PWD = conf.getProperty("password");	
	        VIDEO_CONTEXT_DB = conf.getProperty("video_contex_db");	
	        ADMIN_DB = conf.getProperty("admin_db");
	        LOG_COLLECTION = conf.getProperty("log_collection");	
	        DB_YOUTUBE_COLLECTION = conf.getProperty("db_youtube_collection");	
	        DB_TWITTER_COLLECTION = conf.getProperty("db_twitter_collection");	
	        DB_TWEETS_COLLECTION = conf.getProperty("db_tweets_collection");	
	        VIDEO_CONTEXT_DB_FEATURES = conf.getProperty("video_contex_db_features");
	        VIDEO_CONTEXT_DB_RESULTS = conf.getProperty("video_context_db_results");	     
	    }
}

