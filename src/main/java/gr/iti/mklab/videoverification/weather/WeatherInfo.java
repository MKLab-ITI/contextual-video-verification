package gr.iti.mklab.videoverification.weather;

import java.math.BigDecimal;

import org.json.JSONObject;

import ch.rasc.darksky.DsClient;
import ch.rasc.darksky.model.DsBlock;
import ch.rasc.darksky.model.DsDataPoint;
import ch.rasc.darksky.model.DsResponse;
import ch.rasc.darksky.model.DsTimeMachineRequest;
import ch.rasc.darksky.model.DsUnit;
import gr.iti.mklab.utils.vUtils;

/*
 * cloudCover: The percentage of sky occluded by clouds, between 0 and 1, inclusive.
 * summary: A human-readable text summary of this data point. 
 * icon:  A machine-readable text summary of this data point, suitable for selecting an icon for display. 
 * 		  If defined, this property will have one of the following values: clear-day, clear-night, rain, 
 * 		  snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night. (Developers should 
 * 		  ensure that a sensible default is defined, as additional values, such as hail, thunderstorm, 
 * 		  or tornado, may be defined in the future.)
 * precipIntensity: The intensity (in inches of liquid water per hour) of precipitation occurring at the given time. 
 * 					This value is conditional on probability (that is, assuming any precipitation occurs at all) 
 * 					for minutely data points, and unconditional otherwise.
 */

public class WeatherInfo {
	
	 
	 public static String getWeather(String id, String location, 
			 long time, String mongo_host, 
			 String weather_api_key, String google_api_key) throws Exception{
		 
		JSONObject weatherObj = new JSONObject();
					GeoCoordinates coord = new GeoCoordinates();
					try {
						coord = vUtils.getUtils().testSimpleGeocode(location, google_api_key);						
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Cannot convert from location to coordinates");
						coord.exist = false;
					}					 					  
					
					// https://github.com/ralscha/darksky
					// https://api.darksky.net/forecast/ac1e66141ef796a6776108fb29df690c/37.8267,-122.4233
					// My API key ac1e66141ef796a6776108fb29df690c
					// mail olgapapa@iti.gr
					// pass naz7229....
					// response example https://darksky.net/dev/docs/time-machine
			if(coord.exist){
				 	  JSONObject dailyinfo = new JSONObject();
				      JSONObject hourlyinfo = new JSONObject();
				   	  hourlyinfo.put("summary", "" );
				      hourlyinfo.put("icon",  "");
				      hourlyinfo.put("wind_speed", "");
				      hourlyinfo.put("visibility",  "");
				      hourlyinfo.put("cloud_cover",  "");
				      hourlyinfo.put("data_exist",  false);
				      hourlyinfo.put("temperature", "");
				      weatherObj.put("hourly", hourlyinfo);
				      
				      dailyinfo.put("summary", "" );
					  dailyinfo.put("icon",  "");
					  dailyinfo.put("wind_speed", "");
					  dailyinfo.put("visibility",  "");
					  dailyinfo.put("cloud_cover",  "");
					  dailyinfo.put("data_exist",  false);
					  dailyinfo.put("max_temperature", "");
  			          dailyinfo.put("min_temperature", "");
				      weatherObj.put("daily", dailyinfo);
				
					try {
						DsClient client = new DsClient(weather_api_key);
						DsTimeMachineRequest request = DsTimeMachineRequest.builder()
						        .latitude(String.valueOf(coord.getLat()))
						        .longitude(String.valueOf(coord.getLon())) 
						        .excludeBlock(DsBlock.ALERTS, DsBlock.MINUTELY)//, DsBlock.HOURLY
						        .unit(DsUnit.SI)
						        .time(time)
						        .build();
						DsResponse response = client.sendTimeMachineRequest(request);
						
						long max = 0;
						long  min= 0;
						try {
							for (DsDataPoint dataPoint : response.hourly().data()) { 
								max = dataPoint.time();
								//System.out.println("time input " + time);
								//System.out.println("time " + dataPoint.time());
								//System.out.println("min " + min);
								//System.out.println("max " + max);
								if ((min <= time) && (time < max)){
									//System.out.println("Finf time");
									//System.out.println("into");
								    //System.out.println(dataPoint);  
									//System.out.println("time " + dataPoint.time());
								   //// System.out.println("summary " + dataPoint.summary());
								   // System.out.println("icon " + dataPoint.icon());
								  //  System.out.println("windSpeed " + dataPoint.windSpeed());
								  //  System.out.println("visibility " + dataPoint.visibility());
								  //  System.out.println("cloudCover  " + dataPoint.cloudCover());
								    // System.out.println("temper " + dataPoint.temperature());
								      hourlyinfo.put("summary",  dataPoint.summary() == null ? "" : dataPoint.summary());
								      hourlyinfo.put("icon", dataPoint.icon() == null ? "" : dataPoint.icon());
								      
								      BigDecimal km_windSpeed = dataPoint.windSpeed().multiply(BigDecimal.valueOf(3.6));
								    
								      hourlyinfo.put("wind_speed", dataPoint.windSpeed() == null ? "" : km_windSpeed);
								      hourlyinfo.put("beaufort", convert2beaufort(km_windSpeed));
								      hourlyinfo.put("visibility", dataPoint.visibility() == null ? "" : dataPoint.visibility());
								      hourlyinfo.put("cloud_cover", dataPoint.cloudCover() == null ? "" : dataPoint.cloudCover());
								      BigDecimal htemperature = dataPoint.temperature().setScale(0, BigDecimal.ROUND_HALF_UP);
								      hourlyinfo.put("temperature", dataPoint.temperature() == null ? "" : htemperature);								      
								      hourlyinfo.put("data_exist",  true);
								      weatherObj.put("hourly", hourlyinfo);							     
								      break;
								}else{
									min = max;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("No Weather Data for this location and Timestamp");
							
						     
						}						
						
						try {
							for (DsDataPoint dataPointFDaily : response.daily().data()) { 
									
							        dailyinfo.put("precipType", dataPointFDaily.precipType() == null ? "" : dataPointFDaily.precipType());
							        dailyinfo.put("summary", dataPointFDaily.summary() == null ? "" : dataPointFDaily.summary());
							        dailyinfo.put("icon", dataPointFDaily.icon() == null ? "" : dataPointFDaily.icon());
							        BigDecimal km_windSpeed = dataPointFDaily.windSpeed().multiply(BigDecimal.valueOf(3.6));
								    //BigDecimal km_visibility = dataPointFDaily.windSpeed().multiply(BigDecimal.valueOf(1.609344));
							        dailyinfo.put("wind_speed", dataPointFDaily.windSpeed() == null ? "" : km_windSpeed);
							        dailyinfo.put("beaufort", convert2beaufort(km_windSpeed));
							        dailyinfo.put("visibility", dataPointFDaily.visibility() == null ? "" : dataPointFDaily.visibility());
							        dailyinfo.put("cloud_cover", dataPointFDaily.cloudCover() == null ? "" : dataPointFDaily.cloudCover());
							       // System.out.println("F " + dataPointFDaily.temperatureMax().doubleValue());
							       // System.out.println("C " + farenheitToCelcius( dataPointFDaily.temperatureMax().doubleValue()));
							       // System.out.println("temp " + dataPointFDaily.temperatureMax());
							        BigDecimal dmaxtemperature = dataPointFDaily.temperatureMax().setScale(0, BigDecimal.ROUND_HALF_UP);
							       // System.out.println("temp round " + dmaxtemperature);
							        BigDecimal dmintemperature = dataPointFDaily.temperatureMin().setScale(0, BigDecimal.ROUND_HALF_UP);
							        dailyinfo.put("max_temperature", dataPointFDaily.temperatureMax() == null ? "" :  dmaxtemperature);
							        dailyinfo.put("min_temperature", dataPointFDaily.temperatureMin() == null ? "" : dmintemperature);
							        dailyinfo.put("data_exist",  true);
							        weatherObj.put("daily", dailyinfo);
							}
						} catch (Exception e) {
							e.printStackTrace();
							e.printStackTrace();
							System.out.println("No Weather Data for this Location and Timestamp");
							
							
						}
					} catch (Exception e) {
						System.out.println("Weather cannot be retrieved");
						weatherObj.put("message", "Weather cannot be retrieved - Invalid Timestamp");
						e.printStackTrace();
					}
			}else{
				weatherObj.put("message", "Location cannot be converted to coordinates");
			}
				
		return weatherObj.toString();
	}
	 
	 public static int  convert2beaufort(BigDecimal km_windSpeed){
		 int beaufort = 0;
		 
		 if (km_windSpeed.doubleValue() < 1 ){
			 beaufort = 0;
		 }else if ((km_windSpeed.doubleValue() < 6 )&& (km_windSpeed.doubleValue() >= 1)){
			 beaufort = 1;
		 }else if ((km_windSpeed.doubleValue() < 12 )&& (km_windSpeed.doubleValue() >= 6)){
			 beaufort = 2;
		 }else if ((km_windSpeed.doubleValue() < 20 )&& (km_windSpeed.doubleValue() >= 12)){
			 beaufort = 3;
		 }else if ((km_windSpeed.doubleValue() < 29 )&& (km_windSpeed.doubleValue() >= 20)){
			 beaufort = 4;
		 }else if ((km_windSpeed.doubleValue() < 39 )&& (km_windSpeed.doubleValue() >= 29)){
			 beaufort = 5;
		 }else if ((km_windSpeed.doubleValue() < 50 )&& (km_windSpeed.doubleValue() >= 39)){
			 beaufort = 6;
		 }else if ((km_windSpeed.doubleValue() < 62 )&& (km_windSpeed.doubleValue() >= 50)){
			 beaufort = 7;
		 }else if ((km_windSpeed.doubleValue() < 75 )&& (km_windSpeed.doubleValue() >= 62)){
			 beaufort = 8;
		 }else if ((km_windSpeed.doubleValue() < 89 )&& (km_windSpeed.doubleValue() >= 75)){
			 beaufort = 9;
		 }else if ((km_windSpeed.doubleValue() < 103 )&& (km_windSpeed.doubleValue() >= 89)){
			 beaufort = 10;
		 }else if ((km_windSpeed.doubleValue() < 118 )&& (km_windSpeed.doubleValue() >= 103)){
			 beaufort = 11;
		 }else if (km_windSpeed.doubleValue() >= 118){
			 beaufort = 12;
		 }	 
		 
		 return beaufort;
	 }
	
	
	
	 public static double farenheitToCelcius(double farenheit) {
	        return (farenheit - 32.0)/1.8;
	    }
	
	public static void main(String[] args) throws Exception {
		getWeather("1481192100", "thessaloniki", 1476273600, "localhost", "", "");
	}
	
	
	

}
