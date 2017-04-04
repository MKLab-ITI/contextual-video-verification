package gr.iti.mklab.videoverification.youtube;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Standford Named Entity Demo
 * 
 * @author Ganesh
 */
public class StanfordNER {
	/**
	 * identify Name,organization location etc entities and return Map<List>
	 * 
	 * @param text
	 *            -- data
	 * @param model
	 *            - Stanford model names out of the three models
	 * @return
	 */
	public static LinkedHashMap<String, LinkedHashSet<String>> identifyNER(
			String text, String model) {
		LinkedHashMap<String, LinkedHashSet<String>> map = new<String, LinkedHashSet<String>> LinkedHashMap();
		String serializedClassifier = model;
		System.out.println(serializedClassifier);
		CRFClassifier<CoreLabel> classifier = CRFClassifier
				.getClassifierNoExceptions(serializedClassifier);
		List<List<CoreLabel>> classify = classifier.classify(text);
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {

				String word = coreLabel.word();
				String category = coreLabel
						.get(CoreAnnotations.AnswerAnnotation.class);
				if (!"O".equals(category)) {
					if (map.containsKey(category)) {
						// key is already their just insert in arraylist
						map.get(category).add(word);
					} else {
						LinkedHashSet<String> temp = new LinkedHashSet<String>();
						temp.add(word);
						map.put(category, temp);
					}
					System.out.println(word + ":" + category);
				}

			}

		}
		return map;
	}

	public static void main(String args[]) {
		String content = "Sachin Ramesh Tendulkar (Listeni/ˌsətʃɪn tɛnˈduːlkər/; Marathi: "
				+ " सचिन रमेश तेंडुलकर; born 24 April 1973) is an Indian former cricketer widely "
				+ " acknowledged as the greatest batsman of the modern generation, popularly holds the title \"God of Cricket\" among his fans [2] He is also acknowledged as the greatest cricketer of all time.[6][7][8][9] He took up cricket at the age of eleven, made his Test debut against Pakistan at the age of sixteen, and went on to represent Mumbai domestically and India internationally for close to twenty-four years. He is the only player to have scored one hundred international centuries, the first batsman to score a Double Century in a One Day International, and the only player to complete more than 30,000 runs in international cricket.[10] In October 2013, he became the 16th player and first Indian to aggregate "
				+ " 50,000 runs in all recognized cricket "
				+ " First-class, List A and Twenty20 combined)";

		System.out.println(identifyNER(content,
				"C:/Users/boididou/Downloads/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz")
				.toString());
	}

}
