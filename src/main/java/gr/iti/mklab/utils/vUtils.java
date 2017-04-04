package gr.iti.mklab.utils;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.PTBTokenizer;
import gr.iti.mklab.videoverification.weather.GeoCoordinates;

/**
 * Utils
 * @author olgapapa
 *
 */

public class vUtils {

	static String rootGeonamesDir = "";
	static String citiesFile = "";
	static String countryInfoFile = "";
	static String adminNamesFile = "";

	private static vUtils sInstance = new vUtils();

	public static vUtils getUtils() {

		if (sInstance == null) {
			sInstance = new vUtils();
		}

		return sInstance;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public Set<String> getLocationMentions(String text, CRFClassifier<CoreLabel> classifier) {

		Set<String> locations = new HashSet<String>();

		// our model
		//String serializedClassifier = VideoVerifier.getProperties().getProperty("path_of_classifier_for_location");
		// our classifier
		//CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

		// classify text
		List<List<CoreLabel>> out = classifier.classify(text);
		for (List<CoreLabel> sentence : out) {
			for (CoreLabel word : sentence) {
				// find the entities tagged as LOCATION
				if (word.get(AnswerAnnotation.class).equals("LOCATION")) {
					// System.out.println(word.word());
					locations.add(word.word());
				}
			}
		}

		return locations;
	}

	/**
	 * Removes the useless characters from a string (including dot(.)) It is
	 * used specifically for pre-processing the text before finding the
	 * mentioned locations
	 * 
	 * @param str
	 *            String that is processed
	 * @return String the result of processing
	 */
	public String eraseAllCharacters(String str) {

		System.out.println("before " + str);

		str = str.replaceAll("\\.", " ");
		str = str.replaceAll(",", " "); // Clear commas
		str = str.replaceAll("$", " "); // Clear $'s (optional)
		str = str.replaceAll("@", " ");
		str = str.replaceAll("-", " ");
		str = str.replaceAll("!", " ");
		str = str.replaceAll("=", " ");
		str = str.replaceAll("#", " ");

		// quotation marks
		str = str.replaceAll("”", " ");
		str = str.replaceAll("“", " ");
		str = str.replaceAll("»", " ");
		str = str.replaceAll("«", " ");
		str = str.replaceAll("‘", " ");
		str = str.replaceAll("’", " ");
		str = str.replaceAll("'", " ");
		str = str.replaceAll("\"", " ");

		// brackets
		str = str.replaceAll("\\[", " ");
		str = str.replaceAll("\\]", " ");

		// greater than - lower than
		str = str.replaceAll("&gt;", " ");
		str = str.replaceAll("&lt;", " ");

		str = str.trim();
		str = str.replaceAll("\\s+", " ");

		System.out.println("after " + str);
		return str;
	}

	public boolean isAVerificationComment(String comment, String[] verifWords) {

		
		StringReader sr = new StringReader(comment);
		PTBTokenizer tkzr = PTBTokenizer.newPTBTokenizer(sr);
		List toks = tkzr.tokenize();
		//System.out.println("tokens: " + toks);
		//transform the String[] array to a hashset
		HashSet<String> wordsSet = new HashSet<String>(Arrays.asList(verifWords)); 		
		for (int i=0;i<toks.size();i++) {
			if (wordsSet.contains(toks.get(i).toString().toLowerCase())) {
				return true;
			}
		}		
		return false;					
	}
	
	public static int[] insertionSort(long[] arr){
	    int[] indices = new int[arr.length];
	        indices[0] = 0;
	        for(int i=1;i<arr.length;i++){
	            int j=i;
	            for(;j>=1 && arr[j]<arr[j-1];j--){
	                    long temp = arr[j];
	                    arr[j] = arr[j-1];
	                    indices[j]=indices[j-1];
	                    arr[j-1] = temp;
	            }
	            indices[j]=i;
	        }
	        return indices;//indices of sorted elements
	 }
	
	 public BasicDBList createhistogram(double[] zmat, int bins) throws IOException{
	  
	   	  int[] result = new int[bins];
	   	  double binSize = 1f/bins;
	   	  //System.out.println("binSize " + binSize);
	   	  for (double d : zmat) {
	   		//System.out.println("d " + d);
	   	    int bin = (int) (d / binSize);
	   	   // System.out.println("bin " + bin);
	   	    if (bin < 0) { /* this data is smaller than min */ System.out.println("this data point is smaller than min " + d);}
	   	    else if (bin >= bins) { /* this data point is bigger than max */
	   	    	System.out.println("this data point is bigger than max");	   	    
	   	    	result[bin-1] += 1;    	
	   	    	}
	   	    else {
	   	      result[bin] += 1;
	   	    }
	   	  }
	   	 BasicDBList asList = new BasicDBList();
	   	 for (int k = 0; k < result.length;k++){
	   		asList.add(result[k]);
	   	 }
	   	  return asList;
	   } 
	
	// the array double[] m MUST BE SORTED
	 public static double median(double[] m) {
		 int middle = m.length/2;
	     if (m.length%2 == 1) {
	          return m[middle];
	     } else {
	    	 return (m[middle-1] + m[middle]) / 2.0;
	     }
	 }
	 
	//by creating a copy array and sorting it, this function can take any data.
	    public static double getMedian(double[] data) {
	        double[] copy = Arrays.copyOf(data, data.length);
	        Arrays.sort(copy);
	        return (copy.length % 2 != 0) ? copy[copy.length / 2] : (copy[copy.length / 2] + copy[(copy.length / 2) - 1]) / 2;
	    }
	 
	  //std dev function for good measure
     public double getStandardDeviation(double[] data) {
         final double mean = getMean(data);
         double sum = 0;
         for (int index = 0; index != data.length; ++index) {
             sum += Math.pow(Math.abs(mean - data[index]), 2);
         }
         return Math.sqrt(sum / data.length);
     }
   
    public double range(final List<Double> list) {
    	    if (list.isEmpty()) {
    	        return 0;
    	    } else {
    	        return (Collections.max(list) - Collections.min(list));
    	    }
    	}
     
     public static double getMean(double[] data)
     {
     	int size = data.length;
         double sum = 0.0;
         for(double a : data)
             sum += a;
         return sum/size;
     }

     public static double getStdDev(double[] data)
     {
         return Math.sqrt(getVariance(data));
     }
     
     public static double getVariance(double[] data)
     {
     	int size = data.length;
         double mean = getMean(data);
         double temp = 0;
         for(double a :data)
             temp += (mean-a)*(mean-a);
         return temp/(size -1);
     }
     
     public long getUnixtimestamp (String time, SimpleDateFormat dateFormat){
    	 long unixtimestamp = 0;
    	 dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));//Specify your timezone 
    	 try
		    {
	    		 unixtimestamp = dateFormat.parse(time).getTime();  
			        //unixtime=unixtime/1000;
		    } 
		    catch (ParseException e) 
		    {
		        e.printStackTrace();
		    }
    	 return unixtimestamp;
     }
     
     public boolean isCollectionExists(DB db, String collectionName) 
	  {
	      DBCollection table = db.getCollection(collectionName);
	      return (table.count()>0)?true:false;
	  }
     
     public GeoCoordinates testSimpleGeocode(String location, String google_api_key) throws Exception {
		 GeoCoordinates coord = new GeoCoordinates();
		 GeoApiContext context = new GeoApiContext().setApiKey(google_api_key);
		 GeocodingResult[] results =  GeocodingApi.geocode(context,
				 location).await();
		if (results.length > 0){
			 coord.setLat(results[0].geometry.location.lat);
			 coord.setLon(results[0].geometry.location.lng);
			 coord.setExist(true);
		 }else{
			 coord.setExist(false);
		 }
			 
	 return coord;
}
     public String DurationFormat(String dur){
    	String dur_temp = "", dur_final = "";
 		String day = "";
 		String hours = "";
 		String minutes = "";
 		String seconds = "";
 		dur_temp = dur.replaceAll("\\d","");
 		System.out.println("case " + dur_temp);
 		if (dur_temp.contains("PDT")){
 			switch (dur_temp) {
 				case "PDTHMS" :
 					day = between(dur, "P", "D");
 					hours = between(dur, "T", "H");
 					minutes = between(dur, "H", "M");
 					seconds = between(dur, "M", "S");
 					if (hours.length() != 2) hours = "0" + hours;
 					if (minutes.length() != 2) minutes = "0" + minutes;
 					if (seconds.length() != 2) seconds = "0" + seconds;
 					dur_final = day + " day(s) " + hours + ":" + minutes + ":" + seconds;
 					break;
 				case "PDTMS" :
 					day = between(dur, "P", "D");
 					minutes = between(dur, "T", "M");					
 					seconds = between(dur, "M", "S");
 					if (minutes.length() != 2) minutes = "0" + minutes;
 					if (seconds.length() != 2) seconds = "0" + seconds;				
 					dur_final = day + " day(s) " + "00:" + minutes + ":" + seconds;					
 					break;
 				case "PDTS" :	
 					day = between(dur, "P", "D");
 					seconds = between(dur, "T", "S");
 					if (seconds.length() != 2) seconds = "0" + seconds;
 					dur_final = day + " day(s) " + "00:" +  seconds;
 					break;
 				default:
 					System.out.println("Error Format");
 					dur_final = dur;								
 			}
 		}

 		if (dur_temp.contains("PT")){
 			switch (dur_temp) {
 			case "PTHMS" :
 				hours = between(dur, "T", "H");
 				minutes = between(dur, "H", "M");
 				seconds = between(dur, "M", "S");
 				if (hours.length() != 2) hours = "0" + hours;
 				if (minutes.length() != 2) minutes = "0" + minutes;
 				if (seconds.length() != 2) seconds = "0" + seconds;
 				dur_final = hours + ":" + minutes + ":" + seconds;
 				break;
 			case "PTMS" :
 				minutes = between(dur, "T", "M");					
 				seconds = between(dur, "M", "S");
 				if (minutes.length() != 2) minutes = "0" + minutes;
 				if (seconds.length() != 2) seconds = "0" + seconds;				
 				dur_final = "00:" + minutes + ":" + seconds;					
 				break;
 			case "PTS" :					
 				seconds = between(dur, "T", "S");
 				if (seconds.length() != 2) seconds = "0" + seconds;
 				dur_final = "00:" +  seconds;
 				break;
 			default:
 				System.out.println("Error Format");
 				dur_final = dur;								
 			}
 		}	
 		return dur_final;
     }
     static String between(String value, String a, String b) {
         // Return a substring between the two strings.
         int posA = value.indexOf(a);
         if (posA == -1) {
             return "";
         }
         int posB = value.lastIndexOf(b);
         if (posB == -1) {
             return "";
         }
         int adjustedPosA = posA + a.length();
         if (adjustedPosA >= posB) {
             return "";
         }
         return value.substring(adjustedPosA, posB);
     }
     
     /**
      * Get a diff between two dates
      * @param date1 the oldest date
      * @param date2 the newest date
      * @param timeUnit the unit in which you want the diff
      * @return the diff value, in the provided unit
      */
     public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
         long diffInMillies = date2.getTime() - date1.getTime();
         return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
     }

	public static void main(String[] args) {
		
	}

}
