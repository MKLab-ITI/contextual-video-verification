package classify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import test.extract.features.ItemFeatures;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class CommentBasedClassification {
	
	public static Instances createtestFeatures(String file) throws ParseException, IOException{
		
		 List<ItemFeatures> itemFeaturesTraining = new ArrayList<ItemFeatures>();
		 List<ItemFeatures> itemFeatures = new ArrayList<ItemFeatures>();
		 List<ItemFeaturesAnnotation> itemAnnotations = new ArrayList<ItemFeaturesAnnotation>();
		 List<String> labels = new ArrayList<String>();
		 
		try ( Stream<String> lines =  Files.lines(Paths.get(file))){
		       lines.forEach(line -> {		    							
							Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
									.create();	
								ItemFeatures item = gson.fromJson(line.toString(), ItemFeatures.class);
								itemFeatures.add(item);
								labels.add(item.getLabel());
		       });
		   };
		            
		   	 System.out.println("Size of training set " + itemFeatures.size());
			 Instances testingSet = null;
			 if (itemFeatures.size() > 0){
					 for (int ii = 0; ii < itemFeatures.size(); ii++) {
						    System.out.println("**i " + ii);
							ItemFeaturesAnnotation itemAnnot = new ItemFeaturesAnnotation();
							itemAnnot.setId(itemFeatures.get(ii).getId());
							itemAnnot.setReliability(labels.get(ii));
							itemAnnotations.add(itemAnnot);
							itemFeaturesTraining.add(itemFeatures.get(ii));
					 }
						 System.out.println("Create training set");
						 ItemClassifier ic = new ItemClassifier();
						 testingSet = ic.createTestingSet(itemFeaturesTraining, itemAnnotations);
						 System.out.println("Feature test " + testingSet.get(0));						 
			 }else{
				 System.out.println("No training data!");
			 }
		 return testingSet;
	}	
	
	
	public static double[] createhistogram(double[] zmat, int bins, int testsize) throws IOException{
		  
	   	  int[] result = new int[bins];
	   	  double[] result_norm = new double[bins];
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
	   
	   	 for (int k = 0; k < result.length;k++){
	   		result_norm[k] = result[k] / (double)  testsize;
	   	 }
	   	  return result_norm;
	   } 
	
	public static double[] classification(Instances testingSet) throws IOException{
	
			 
		 double[] hist = new double[10];
				 
		 if (testingSet != null ){
		
		   int sizeTest = testingSet.size();
			 System.out.println("TestSet Size *********** " + sizeTest);
			 Classifier cls = null; 									 
			 try {
				cls = (Classifier) weka.core.SerializationHelper.read("/models/ivc.model");
			} catch (Exception e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			}			 
			 double[] arrayF = new double[sizeTest];
			
			 for (int j=0; j<sizeTest; j++) {
					 	
						double[] probDistr = null;
						try {
							probDistr = cls.distributionForInstance(testingSet.instance(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
							arrayF[j] = probDistr[1];							
			 		}
				 	hist = createhistogram(arrayF, 10, sizeTest);
			
		 }else{
			System.out.println("Testing set Empty");
		 } 
		return hist;	 
	}
	
	public static void main(String[] args) throws Exception {

		//String file = "Text file containing the comment based features in json format of a video";
		String file = "D:/Reveal/ContextualVideoVerification/FVC_features_real_json.txt";
		Instances testingSet = createtestFeatures(file);
		
		double[] hist = classification(testingSet);
		// print
		/* for (int i=0; i<hist.length; i++){
			System.out.println("Histogram " + hist[i]);
		}*/
			
	}

}
