package classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import test.extract.features.ItemFeatures;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class ItemClassifier {
	
	private Instances isTrainingSet, isTestingSet;
	private ArrayList<Attribute> fvAttributes = new ArrayList<Attribute>();

	
	//constructor
	public ItemClassifier() {
		setFvAttributes(declareAttributes());
	}
	
	
	public ArrayList<Attribute> getFvAttributes() {
		return fvAttributes;
	}
	
	public void setFvAttributes(ArrayList<Attribute> list) {
		this.fvAttributes = list;
	}
	
	public Instances getIsTrainingSet() {
		return isTrainingSet;
	}

	public void setIsTrainingSet(Instances isTrainingSet) {
		this.isTrainingSet = isTrainingSet;
	}
	
	

	private ArrayList<Attribute> declareAttributes() {
		// declare numeric attributes

		Attribute ItemLength = new Attribute("item_length");
		Attribute numWords = new Attribute("num_words");
		Attribute numQuestionMark = new Attribute("num_questionmark");
		Attribute numExclamationMark = new Attribute("num_exclamationmark");
		Attribute numUppercaseChars = new Attribute("num_uppercasechars");
		Attribute numPosSentiWords = new Attribute("num_pos_sentiment_words");
		Attribute numNegSentiWords = new Attribute("num_neg_sentiment_words");	
		Attribute numSlangs = new Attribute("num_slangs");		
		Attribute readability = new Attribute("readability");
		
		// declare nominal attributes
				List<String> fvnominal1 = new ArrayList<String>(2);
				fvnominal1.add("true");
				fvnominal1.add("false");
				Attribute containsQuestionMark = new Attribute("contains_question_mark",
						fvnominal1);
		
				List<String> fvnominal2 = new ArrayList<String>(2);
				fvnominal2.add("true");
				fvnominal2.add("false");
				Attribute containsExclamationMark = new Attribute(
						"contains_exclamation_mark", fvnominal2);
			
				List<String> fvnominal3 = new ArrayList<String>(2);
				fvnominal3.add("true");
				fvnominal3.add("false");
				Attribute containsHappyEmo = new Attribute("contains_happy_emo",
						fvnominal3);
				// Attribute containsHappyEmo = new Attribute("containsHappyEmo");

				List<String> fvnominal4 = new ArrayList<String>(2);
				fvnominal4.add("true");
				fvnominal4.add("false");
				Attribute containsSadEmo = new Attribute("contains_sad_emo", fvnominal4);
				// Attribute containsSadEmo = new Attribute("containsSadEmo");

				List<String> fvnominal5 = new ArrayList<String>(2);
				fvnominal5.add("true");
				fvnominal5.add("false");
				Attribute containsFirstOrderPron = new Attribute(
						"contains_first_order_pron", fvnominal5);
				// Attribute containsFirstOrderPron = new
				// Attribute("containsFirstOrderPron");

				List<String> fvnominal6 = new ArrayList<String>(2);
				fvnominal6.add("true");
				fvnominal6.add("false");
				Attribute containsSecondOrderPron = new Attribute(
						"contains_second_order_pron", fvnominal6);
				// Attribute containsSecondOrderPron = new
				// Attribute("containsSecondOrderPron");

				List<String> fvnominal7 = new ArrayList<String>(2);
				fvnominal7.add("true");
				fvnominal7.add("false");
				Attribute containsThirdOrderPron = new Attribute(
						"contains_third_order_pron", fvnominal7);
				// Attribute containsThirdOrderPron = new
				// Attribute("containsThirdOrderPron");

				List<String> fvnominal8 = new ArrayList<String>(2);
				fvnominal8.add("true");
				fvnominal8.add("false");
				Attribute hasColon = new Attribute("has_colon", fvnominal8);
				// Attribute hasColon = new Attribute("hasColon");

				List<String> fvnominal9 = new ArrayList<String>(2);
				fvnominal9.add("true");
				fvnominal9.add("false");
				Attribute hasPlease = new Attribute("has_please", fvnominal9);
				// Attribute hasPlease = new Attribute("hasPlease");

				List<String> fvnominal11 = null;
				Attribute id = new Attribute("id", fvnominal11);

				List<String> fvClass = new ArrayList<String>(2);
				fvClass.add("real");
				fvClass.add("fake");
				Attribute ClassAttribute = new Attribute("theClass", fvClass);
	
		ArrayList<Attribute> fvAttributes = new ArrayList<Attribute>();
		
		fvAttributes.add(id);

		fvAttributes.add(ItemLength);
		fvAttributes.add(numWords);
		fvAttributes.add(containsQuestionMark);
		fvAttributes.add(containsExclamationMark);
		fvAttributes.add(containsHappyEmo);
		fvAttributes.add(containsSadEmo);
		fvAttributes.add(containsFirstOrderPron);
		fvAttributes.add(containsSecondOrderPron);
		fvAttributes.add(containsThirdOrderPron);
		fvAttributes.add(numUppercaseChars);
		fvAttributes.add(numPosSentiWords);
		fvAttributes.add(numNegSentiWords);
		fvAttributes.add(numSlangs); // new
		fvAttributes.add(hasColon); // new
		fvAttributes.add(hasPlease); // new
		fvAttributes.add(numQuestionMark);
		fvAttributes.add(numExclamationMark);
		fvAttributes.add(readability); // new
		fvAttributes.add(ClassAttribute);

		return fvAttributes;
	}

	public Instance createInstance(ItemFeatures listItemFeatures) {

		Instance iExample = new DenseInstance(fvAttributes.size());
		String id = listItemFeatures.getId().replaceAll("[^\\d.]", "");
		System.out.println("id " + id);
		iExample.setValue((Attribute) fvAttributes.get(0), id);

		iExample.setValue((Attribute) fvAttributes.get(1),
				listItemFeatures.getItemLength());
		System.out.println("getItemLength " + listItemFeatures.getItemLength());
		iExample.setValue((Attribute) fvAttributes.get(2),
				listItemFeatures.getNumWords());
		System.out.println("getNumWords " + listItemFeatures.getNumWords());
		iExample.setValue((Attribute) fvAttributes.get(3),
				String.valueOf(listItemFeatures.getContainsQuestionMark()));

		iExample.setValue((Attribute) fvAttributes.get(4),
				String.valueOf(listItemFeatures.getContainsExclamationMark()));
		iExample.setValue((Attribute) fvAttributes.get(5),
				String.valueOf(listItemFeatures.getContainsHappyEmo()));
		System.out.println("getContainsHappyEmo " + listItemFeatures.getContainsHappyEmo());
		iExample.setValue((Attribute) fvAttributes.get(6),
				String.valueOf(listItemFeatures.getContainsSadEmo()));

		if (listItemFeatures.getContainsFirstOrderPron() != null) {
			iExample.setValue((Attribute) fvAttributes.get(7), String
					.valueOf(listItemFeatures.getContainsFirstOrderPron()));
		}
		if (listItemFeatures.getContainsSecondOrderPron() != null) {
			iExample.setValue((Attribute) fvAttributes.get(8), String
					.valueOf(listItemFeatures.getContainsSecondOrderPron()));
		}
		if (listItemFeatures.getContainsThirdOrderPron() != null) {
			iExample.setValue((Attribute) fvAttributes.get(9), String
					.valueOf(listItemFeatures.getContainsThirdOrderPron()));
		}

		iExample.setValue((Attribute) fvAttributes.get(10),
				listItemFeatures.getNumUppercaseChars());

		if (listItemFeatures.getNumPosSentiWords() != null) {
			iExample.setValue((Attribute) fvAttributes.get(11),
					listItemFeatures.getNumPosSentiWords());
		}
		if (listItemFeatures.getNumNegSentiWords() != null) {
			iExample.setValue((Attribute) fvAttributes.get(12),
					listItemFeatures.getNumNegSentiWords());
		}
	
		if (listItemFeatures.getNumSlangs() != null) {
			iExample.setValue((Attribute) fvAttributes.get(13),
					listItemFeatures.getNumSlangs());
		}
		iExample.setValue((Attribute) fvAttributes.get(14),
				String.valueOf(listItemFeatures.getHasColon()));
		iExample.setValue((Attribute) fvAttributes.get(15),
				String.valueOf(listItemFeatures.getHasPlease()));

		iExample.setValue((Attribute) fvAttributes.get(16),
				listItemFeatures.getNumQuestionMark());
		iExample.setValue((Attribute) fvAttributes.get(17),
				listItemFeatures.getNumExclamationMark());

		if (listItemFeatures.getReadability() != null) {
			iExample.setValue((Attribute) fvAttributes.get(18),
					listItemFeatures.getReadability());
		}
		return iExample;
	}
	
	
	public Instances createTestingSet(List<ItemFeatures> listTest, List<ItemFeaturesAnnotation> itemFeaturesAnnot) {
		
		// Create an empty training set
		Instances isTestSet = new Instances("Rel", fvAttributes,
				listTest.size());
		// Set class index
		isTestSet.setClassIndex(fvAttributes.size() - 1);

		//save the <id,label> pair to a map
		HashMap<String,String> map = new HashMap<String, String>();
		
		for (int j = 0; j < itemFeaturesAnnot.size(); j++) {
			System.out.println("itemFeaturesAnnot.get(j).getId() " + itemFeaturesAnnot.get(j).getId());
			map.put(itemFeaturesAnnot.get(j).getId(), itemFeaturesAnnot.get(j).getReliability());
		}
		
		//iterate through list of ItemFeatures
		for (int i = 0; i < listTest.size(); i++) {
			//create an Instance
			System.out.println("listTest " + listTest.get(i).getContainsExclamationMark());
			Instance iExample = createInstance(listTest.get(i));
			System.out.println("iExample " + iExample);
			//find the reliability value of this feature from the map and put it to the Instance object just created
			System.out.println("value " + (fvAttributes.size() - 1));
			System.out.println(map.get(listTest.get(i).getId()));
			iExample.setValue((Attribute) fvAttributes.get(fvAttributes.size() - 1), map.get(listTest.get(i).getId()) );
			//add the complete Instance to the Instances object
			isTestSet.add(iExample);
		}		
		
		return isTestSet;

	}	
	
}

