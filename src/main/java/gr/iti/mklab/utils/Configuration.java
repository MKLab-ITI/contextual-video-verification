package gr.iti.mklab.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Configuration properties file
 * @author olgapapa
 *
 */

public class Configuration {
	 	public static String API_KEY;
	    public static String VERIFICATION_WORDS; //verification_words
	    public static String PATH_OF_CLASSIFIER_FOR_LOCATION; //path_of_classifier_for_location
	    public static String PATH_OF_LEXICALIZED_PARSER; //path_of_lexicalized_parser
	    public static String CHROME_DRIVER; //chromedriver
	    public static String MONGO_HOST; // mogno host
	    public static String WEATHER_API_KEY; // https://darksky.net/ olgapapa@iti.gr 
	    public static String GOOGLE_GEO_API_KEY;
	    public static String REPORT_BASE_URL;
	    public static String TWEET_VERIFICATION_SERVICE_URL;

	    public static void load(String file) throws ConfigurationException {
	        PropertiesConfiguration conf = new PropertiesConfiguration(file);
	        API_KEY = conf.getString("apikey");
	        VERIFICATION_WORDS=conf.getString("verification_words");
	        PATH_OF_CLASSIFIER_FOR_LOCATION=conf.getString("path_of_classifier_for_location");
	        PATH_OF_LEXICALIZED_PARSER=conf.getString("path_of_lexicalized_parser");
	        CHROME_DRIVER=conf.getString("chromedriver");
	        REPORT_BASE_URL = conf.getString("report_base_url");
	        WEATHER_API_KEY =  conf.getString("weather_api_key");
	        GOOGLE_GEO_API_KEY = conf.getString("google_geo_api_key");
	        TWEET_VERIFICATION_SERVICE_URL = conf.getString("tweet_verification_service_url");
	    }

	    public static void load(InputStream stream) throws ConfigurationException, IOException {
	        Properties conf = new Properties();
	        conf.load(stream);
	       
	        API_KEY = conf.getProperty("apikey");
	        VERIFICATION_WORDS = conf.getProperty("verification_words");
	        PATH_OF_CLASSIFIER_FOR_LOCATION = conf.getProperty("path_of_classifier_for_location");
	        PATH_OF_LEXICALIZED_PARSER = conf.getProperty("path_of_lexicalized_parser");
	        CHROME_DRIVER=conf.getProperty("chromedriver");
	        REPORT_BASE_URL = conf.getProperty("report_base_url");
	        WEATHER_API_KEY = conf.getProperty("weather_api_key");
	        GOOGLE_GEO_API_KEY = conf.getProperty("google_geo_api_key");
	        TWEET_VERIFICATION_SERVICE_URL = conf.getProperty("tweet_verification_service_url");

	     
	    }

}
