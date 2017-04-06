package test.extract.features;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.process.PTBTokenizer;
import me.champeau.ld.UberLanguageDetector;

/**
 * Provides the text processing procedure with the necessary functions.
 * @author Boididou Christina 
 * @date 10.07.14
 */
public class TextProcessing {

	private static TextProcessing mInstance = new TextProcessing();

	public static TextProcessing getInstance() {
		
		
		if (mInstance == null) {
			
			mInstance = new TextProcessing();
		}
		return mInstance;

	}

	/**
	 * Tokenizes the given string by the space character
	 * 
	 * @param str
	 *            String to be tokenized
	 * @return String[] the tokens found
	 */
	public String[] tokenizeText(String str) {

		//lowercase the text
		str = str.toLowerCase();
		
		// replace spanish characters with the corresponding simple ones
		str = removeSpanishAccent(str);
		
		// replace all the useless chars inside the text
		str = str.replaceAll(",", " ");
		str = str.replaceAll("$", " ");
		str = str.replaceAll("&quot;", " ");
		str = str.replaceAll("&gt;", " ");
		str = str.replaceAll("&lt;", " ");
		str = str.replaceAll("&amp", " ");
		str = str.replaceAll("http://[^ ]+ ", " ");
		str = str.replaceAll("https://[^ ]+ ", " ");
		str = str.replaceAll("-", " ");
		str = str.replaceAll("/", " ");
		str = str.replaceAll("=", " ");
		str = str.replaceAll("\\!", " ");
		str = str.replaceAll("\\.+", " ");
		str = str.replaceAll("\\[", " ");
		str = str.replaceAll("\\]", " ");
		str = str.replaceAll("\"", " ");
		str = str.replaceAll(";", " ");
		

		str = str.trim();
		str = str.replaceAll("\\s+", " ");
		
		StringReader sr = new StringReader(str);
		PTBTokenizer tkzr = PTBTokenizer.newPTBTokenizer(sr);
		List toks = tkzr.tokenize();

		String[] tokens = new String[toks.size()];
		for (int i=0; i<toks.size(); i++) {
			tokens[i] = toks.get(i).toString();
		}
	/*	System.out.println("**** Tokens");
		for (String tk : tokens){
			System.out.println(tk);
		}*/
			
		/*System.out.println("TOKENS");
		for (String token:tokens) {
			System.out.println(token);
		}*/
		
		return tokens;
	}
	
	/**
	 * Auxiliary function that removes the spanish accent from a spanish text
	 * @param word String the text from which the accent is removed
	 * @return String the transformed text
	 */
	public String removeSpanishAccent(String text) {
		text = text.replaceAll("Γ |Γ΅|ΓΆ|Γ¤", "a");
		text = text.replaceAll("Γ²|Γ³|Γ΄|Γ¶", "o");
		text = text.replaceAll("Γ¨|Γ©|Γ�|Γ«", "e");
		text = text.replaceAll("ΓΉ|ΓΊ|Γ»|ΓΌ", "u");
		text = text.replaceAll("Γ¬|Γ­|Γ®|Γ―", "i");

		return text;
	}
	
	/**
	 * Removes the useless characters from a string (apart from the dot(.))
	 * It is used specifically for pre-processing the text before finding the urls
	 * @param str String that is processed
	 * @return String the result of processing
	 */
	public String eraseCharacters(String str){
		
		System.out.println("before " + str);
		
		str = str.replaceAll(",", " "); // Clear commas
		str = str.replaceAll("$", " "); // Clear $'s (optional)
		//str = str.replaceAll("@", " ");
		str = str.replaceAll("-", " ");
		str = str.replaceAll("!", " ");
		str = str.replaceAll("=", " ");
		//str = str.replaceAll("#"," ");
		
		//quotation marks
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("Β»", " ");
		str = str.replaceAll("Β«", " ");
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("β€™", " ");
		str = str.replaceAll("'", " ");
		str = str.replaceAll("\"", " ");
		
		//brackets
		str = str.replaceAll("\\[", " ");
		str = str.replaceAll("\\]", " ");
		
		//greater than - lower than
		str = str.replaceAll("&gt;", " ");
		str = str.replaceAll("&lt;", " ");
		
		str = str.replaceAll("\\s+", " ");
		
		//System.out.println("after " + str);
		return str;
	}

	/**
	 * Removes the useless characters from a string (including dot(.))
	 * It is used specifically for pre-processing the text before finding the urls
	 * @param str String that is processed
	 * @return String the result of processing
	 */
	public String eraseAllCharacters(String str){
		
		System.out.println("before " + str);
		
		str = str.replaceAll("\\.", " "); 
		str = str.replaceAll(",", " "); // Clear commas
		str = str.replaceAll("$", " "); // Clear $'s (optional)
		str = str.replaceAll("@", " ");
		str = str.replaceAll("-", " ");
		str = str.replaceAll("!", " ");
		str = str.replaceAll("=", " ");
		str = str.replaceAll("#"," ");
		
		//quotation marks
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("Β»", " ");
		str = str.replaceAll("Β«", " ");
		str = str.replaceAll("β€�", " ");
		str = str.replaceAll("β€™", " ");
		str = str.replaceAll("'", " ");
		str = str.replaceAll("\"", " ");
		
		//brackets
		str = str.replaceAll("\\[", " ");
		str = str.replaceAll("\\]", " ");
		
		//greater than - lower than
		str = str.replaceAll("&gt;", " ");
		str = str.replaceAll("&lt;", " ");
		
		str = str.trim();
		System.out.println("after " + str);
		return str;
	}
	
	/**
	 * Returns the language detected for the given String
	 * @param str String for which the language is detected
	 * @return String the language detected
	 */
	public String getLanguage(String str){
		String language = null;
		if (!str.trim().isEmpty()){
			UberLanguageDetector detector = UberLanguageDetector.getInstance();
			//System.out.println("string "+str);
			language = detector.detectLang(str);
			//System.out.println("language "+detector.scoreLanguages(str));			
		}
		if (language == null){
			language = "nolang";
		}
		return language;
	}
	
	public HashMap<String,Integer> calculateFrequencyInText(String[] words){
		
		HashMap<String, Integer> map = new HashMap<String,Integer>();
		    for (String w : words) {
		        Integer n = map.get(w);
		        n = (n == null) ? 1 : ++n;
		        map.put(w, n);
		    }
		return map;
	}
	
}
