package gr.iti.mklab.videoverification.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.util.JSON;

public class TweetVerificationClient {
	
	public static String verifyTweet(String tweetId, String tweetVerServiceIP) {
		String verified = "";
		String output = "";
		  try {
			URL url = new URL("http://" + tweetVerServiceIP + "/verify?id=" + tweetId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(15000); //set timeout to 5 seconds
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			while ((output = br.readLine()) != null) {
					verified = output;					
			}			  
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();

		  } catch (IOException e) {
			  e.printStackTrace();
		  }	
		  return verified;
		}


}
