package test.extract.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;
import weka.core.Instances;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;

/**
 * 
 * @author olgapapa
 *
 */

public class ItemFeaturesExtractorJSON {
	
	
	static String[] tokens;
	static String itemTitle;
	public static Instances isTrainingSet;
	public static LexicalizedParser lp = LexicalizedParser
			.loadModel(Vars.MODEL_PARSER);
	public static List<String> indegreeLines = new ArrayList<String>();
	public static List<String> harmonicLines = new ArrayList<String>();

	public static void setItemTitle(String itemTitle) {
		ItemFeaturesExtractorJSON.itemTitle = itemTitle;
	}

	static String text = "";
	

	
	public static ItemFeatures extractFeaturesYTComment(String comment, String videoID)
			throws Exception {

		// info
		System.out.println("Extracting Item features for " + videoID
				+ "...");

		// define the ItemFeatures object that holds the features
		ItemFeatures feat = new ItemFeatures();

		// define the value of the text...
		 text = comment;		

		// preprocess the text
		String str = text.replaceAll("http+s*+://[^ ]+", "")
				.replaceAll("@[^ ]+", "").replaceAll("#[^ ]+ ", "")
				.replaceAll("RT", "").toLowerCase().trim();

		/** Features depending on the Item(Tweet) **/
		// id
		feat.setId(videoID);		
		// item length
		feat.setItemLength(text.length());
		System.out.println("Tweet text length: " + text.length());		
		// num of words
		feat.setNumWords(getNumItemWords());
		System.out.println("Number of words: " + feat.getNumWords());
		// contains "?"
		feat.setContainsQuestionMark(containsSymbol("?"));
		System.out.println("Contains question mark "
				+ feat.getContainsQuestionMark());
		// contains "!"
		feat.setContainsExclamationMark(containsSymbol("!"));
		System.out.println("Contains exclamation mark "
				+ feat.getContainsExclamationMark());
		// num of "!"
		feat.setnumExclamationMark(getNumSymbol("!"));
		System.out.println("Number of exclamation marks: "
				+ feat.getNumExclamationMark());
		// num of "?"
		feat.setNumQuestionMark(getNumSymbol("?"));
		System.out.println("Number of question marks: "
				+ feat.getNumQuestionMark());
		// contains happy emoticon
		feat.setContainsHappyEmo(containsEmo(Vars.HAPPY_EMO_PATH));
		System.out.println("Contains happy emoticon: "
				+ feat.getContainsHappyEmo());
		// contains sad emoticon
		feat.setContainsSadEmo(containsEmo(Vars.SAD_EMO_PATH));
		System.out
				.println("Contains sad emoticon: " + feat.getContainsSadEmo());
		// num of uppercase chars
		feat.setNumUppercaseChars(getNumUppercaseChars());
		System.out.println("Number of uppercase characters: "
				+ feat.getNumUppercaseChars());		
		// has colon
		feat.setHasColon(containsSymbol(":"));
		System.out.println("Has ':' symbol: " + feat.getHasColon());
		// has please
		feat.setHasPlease(containsSymbol("please"));
		System.out.println("Has 'please' word: " + feat.getHasPlease());
		// external links (except for the image link)			

		/** Additional features depending on the Comment's language **/

		String lang = TextProcessing.getInstance().getLanguage(str);

		// english
		if (lang.equals("en")) {
			// num of positive sentiment words
			feat.setNumPosSentiWords(getNumSentiWords(Vars.POS_WORDS_ENG_PATH));
			System.out.println("Number of positive sentiment words: "
					+ feat.getNumPosSentiWords());
			// num of negative sentiment words
			feat.setNumNegSentiWords(getNumSentiWords(Vars.NEG_WORDS_ENG_PATH));
			System.out.println("Number of negative words: "
					+ feat.getNumNegSentiWords());

			// contains first,second and third order pronoun
			feat.setContainsFirstOrderPron(containsPronoun(Vars.FIRST_PRON_PATH));
			System.out.println("Contains 1st person pronoun: "
					+ feat.getContainsFirstOrderPron());
			feat.setContainsSecondOrderPron(containsPronoun(Vars.SECOND_PRON_PATH));
			System.out.println("Contains 2nd person pronoun: "
					+ feat.getContainsSecondOrderPron());
			feat.setContainsThirdOrderPron(containsPronoun(Vars.THIRD_PRON_PATH));
			System.out.println("Contains 3rd person pronoun: "
					+ feat.getContainsThirdOrderPron());

			// Features only available in english
			// num of slang words
			feat.setNumSlangs(getNumSlangs(Vars.SLANG_ENG_PATH, "en"));
			System.out.println("Number of slang words: " + feat.getNumSlangs());
		
			// redability score
			Double readability = getReadability();
			if (readability != null) 
				feat.setReadability(readability);
			
			System.out.println("Readability score: " + feat.getReadability());

			// spanish
		} else if (lang.equals("es")) {
			feat.setNumPosSentiWords(getNumSentiWords(Vars.POS_WORDS_ES_PATH));
			System.out.println("Number of positive sentiment words: "
					+ feat.getNumPosSentiWords());
			feat.setNumNegSentiWords(getNumSentiWords(Vars.NEG_WORDS_ES_PATH));
			System.out.println("Number of negative words: "
					+ feat.getNumNegSentiWords());

			feat.setContainsFirstOrderPron(containsPronoun(Vars.FIRST_PRON_ES_PATH));
			System.out.println("Contains 1st person pronoun: "
					+ feat.getContainsFirstOrderPron());

			feat.setContainsSecondOrderPron(containsPronoun(Vars.SECOND_PRON_ES_PATH));
			System.out.println("Contains 2nd person pronoun: "
					+ feat.getContainsSecondOrderPron());

			feat.setContainsThirdOrderPron(containsPronoun(Vars.THIRD_PRON_ES_PATH));
			System.out.println("Contains 3rd person pronoun: "
					+ feat.getContainsThirdOrderPron());

			feat.setNumSlangs(getNumSlangs(Vars.SLANG_ES_PATH, "es"));
			System.out.println("Number of slang words: " + feat.getNumSlangs());
			// german
		} else if (lang.equals("de")) {
			feat.setNumPosSentiWords(getNumSentiWords(Vars.POS_WORDS_DE_PATH));
			System.out.println("Number of positive sentiment words: "
					+ feat.getNumPosSentiWords());

			feat.setNumNegSentiWords(getNumSentiWords(Vars.NEG_WORDS_DE_PATH));
			System.out.println("Number of negative words: "
					+ feat.getNumNegSentiWords());

			feat.setContainsFirstOrderPron(containsPronoun(Vars.FIRST_PRON_DE_PATH));
			System.out.println("Contains 1st person pronoun: "
					+ feat.getContainsFirstOrderPron());
			feat.setContainsSecondOrderPron(containsPronoun(Vars.SECOND_PRON_DE_PATH));
			System.out.println("Contains 2nd person pronoun: "
					+ feat.getContainsSecondOrderPron());
			feat.setContainsThirdOrderPron(containsPronoun(Vars.THIRD_PRON_DE_PATH));
			System.out.println("Contains 3rd person pronoun: "
					+ feat.getContainsThirdOrderPron());
		}

		System.out.println("-");
		return feat;
	}

	/**
	 * Calculates the number of words contained in the tweet's text
	 * 
	 * @return Integer the number of words found
	 */
	public static Integer getNumItemWords() {

		// call the tokenizer to get the words of the string
		tokens = TextProcessing.getInstance().tokenizeText(text);		
		// find the number of words
		Integer numWords = tokens.length;
		return numWords;
	}

	public static Boolean containsSymbol(String symbol) {

		String str = text.replaceAll("http://[^ ]+", " "); // drop urls
		// check if the text contains the given symbol
		// print info
		// System.out.println("Symbol: " + symbol + " " + str.contains(symbol));
		return str.contains(symbol);
	}

	public static Integer getNumSymbol(String symbol) {
		Integer numSymbols = 0;

		// check every single character of text for the given symbol
		for (int i = 0; i < text.length(); i++) {
			Character ch = text.charAt(i);
			if (ch.toString().equals(symbol)) {
				numSymbols++;
			}
		}
		// print info
		// System.out.println("num of " + symbol + ": " + numSymbols);
		return numSymbols;
	}

	public static Boolean containsEmo(String filePath) {
		Boolean containsEmo = false;
		BufferedReader br = null;
		// hashset that contains the emoticons from the txt file
		HashSet<String> emoticons = new HashSet<String>();

		try {
			File fileEmoticons = new File(filePath);
			if (!fileEmoticons.exists()) {
				fileEmoticons.createNewFile();
			}
			String currentLine;
			// create the file reader
			br = new BufferedReader(new FileReader(fileEmoticons));
			// read the txt file and add each line to the hash set
			while ((currentLine = br.readLine()) != null) {
				emoticons.add(currentLine);
			}
			// use the iterator to get elements from the hashset
			// check if text contains each of the elements
			Iterator<String> iterator = emoticons.iterator();
			while (iterator.hasNext()) {
				String emo = iterator.next().toString();
				if (text.contains(emo)) {
					containsEmo = true;
				}
			}

			br.close();
			// print info
			//System.out.println("Contains emoticon: " + containsEmo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return containsEmo;

	}

	public static Integer getNumUppercaseChars() {
		Integer numUppercaseChars = 0;
		Character ch = null;
		// drop all URLs, hashtags and mentions ("http://", "#anyhashtag",
		// "@anymention", "@anymentionwithspace")- no need to count the
		// uppercase
		// chars on them
		String str = text.replaceAll("http://[^ ]+", "")
				.replaceAll("@ [^ ]+ ", "").replaceAll("@[^ ]+", "")
				.replaceAll("#[^ ]+", "");

		// count the uppercase chars
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (Character.isUpperCase(ch)) {
				numUppercaseChars++;
			}
		}
		if (text.contains("RT ") && numUppercaseChars > 1) {
			numUppercaseChars = numUppercaseChars - 2;
		}
		// print info
		// System.out.println("Num of uppercase chars: " + numUppercaseChars);

		return numUppercaseChars;
	}

	public static Boolean containsPronoun(String filePath) {

		Boolean containsPron = false;
		BufferedReader br = null;

		// hash set that contains the words from the txt file
		HashSet<String> pronounWords = new HashSet<String>();

		try {
			File Prons = new File(filePath);
			if (!Prons.exists()) {
				Prons.createNewFile();
			}
			String currentLine;
			br = new BufferedReader(new FileReader(Prons));

			// save to hashset every line of the txt file
			while ((currentLine = br.readLine()) != null) {
				pronounWords.add(currentLine);
			}

			for (int j = 0; j < tokens.length; j++) {
				if (pronounWords.contains(tokens[j].replaceAll("[^A-Za-z0-9 ]",
						"").toLowerCase())) {
					containsPron = true;
				}
			}

			// print info
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return containsPron;
	}

	public static Integer getNumSentiWords(String filePath) {
		Integer numSentiWords = 0;
		BufferedReader br = null;
		// use hashset to save the words from the txt file
		HashSet<String> sentiwords = new HashSet<String>();
		try {
			File sentiWords = new File(filePath);
			if (!sentiWords.exists()) {
				sentiWords.createNewFile();
			}
			String currentLine;
			br = new BufferedReader(new FileReader(sentiWords));

			while ((currentLine = br.readLine()) != null) {
				sentiwords.add(currentLine);
			}

			for (int i = 0; i < tokens.length; i++) {

				String clearToken = tokens[i].replaceAll("[^A-Za-z0-9 ]", "")
						.toLowerCase();

				if (sentiwords.contains(clearToken)) {
					numSentiWords++;
					// print info
					// System.out.println("Senti word found:" + tokens[i]);
				}
			}

			// print info
			//System.out.println("Number of senti words: " + numSentiWords);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return numSentiWords;
	}

	public static Integer getNumSlangs(String filePath, String lang)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		// declare the class to use
		Class stemClass = Class
				.forName("org.tartarus.snowball.ext.spanishStemmer");

		// declare the auxiliary variables
		Integer numSlangs = 0, foundCounter = 0, indexHolder = 0, prevIndexHolder = 0, i = 0;

		// array of the tokens of the comment text
		String[] justTokens = new String[tokens.length];

		// variable to declare the Word attributes (by stanford parser)
		Morphology m = new Morphology();

		// buffered to read the file containing the slangs
		BufferedReader br = null;

		try {
			File slangWords = new File(filePath);
			if (!slangWords.exists()) {
				slangWords.createNewFile();
			}
			String currentLine;
			br = new BufferedReader(new FileReader(slangWords));

			String wrdResult = null;
			// create the hashset that contains the tokens
			for (String token : tokens) {

				token = token.replaceAll("[^A-Za-z0-9 ]", "");
				if (token != null) {
					if (lang == "en") {

						Word wrd = new Word(token);
						wrd = m.stem(wrd);
						wrdResult = wrd.toString();
						// System.out.println(token + " " + wrdResult);

					} else if (lang == "es") {

						SnowballStemmer stemmer = (SnowballStemmer) stemClass
								.newInstance();
						stemmer.setCurrent(token);
						stemmer.stem();
						wrdResult = stemmer.getCurrent();
					}
					justTokens[i] = wrdResult;
					i++;
				}
				// System.out.print(wrdResult+" ");
			}

			// check every line of the file
			while ((currentLine = br.readLine()) != null) {
				String regex = " ";
				if (currentLine.contains("-")) {
					if (currentLine.indexOf("-") != 0
							|| (currentLine.indexOf("-") != (currentLine
									.length() - 1))) {
						regex = "-";
					}
				}
				String[] words = currentLine.split(regex);
				foundCounter = 0;
				indexHolder = 0;
				prevIndexHolder = 0;

				for (String word : words) {

					String prefix = "#";
					if (word.endsWith("-")) {
						prefix = word.replace("-", "");
					}
					String suffix = "!";
					if (word.startsWith("-")) {
						suffix = word.replace("-", "");
					}
					if (lang == "en") {

						Word wrd = new Word(word);
						wrd = m.stem(wrd);
						word = wrd.toString();

					} else if (lang == "es") {
						SnowballStemmer stemmer = (SnowballStemmer) stemClass
								.newInstance();
						stemmer.setCurrent(word);
						stemmer.stem();
						word = stemmer.getCurrent();
						// System.out.println(word+" "+wrdResult);
					}

					for (String token : justTokens) {
						if (token != null) {
							if (token.equals(word) || token.startsWith(prefix)
									|| token.endsWith(suffix)) {
								indexHolder = Arrays.asList(justTokens)
										.indexOf(word);
								// System.out.println(token+" "+word);
								// System.out.println();
								// System.out.println("Found " + word +
								// " with index "+ indexHolder);

								if (indexHolder > prevIndexHolder) {
									prevIndexHolder = indexHolder;
									foundCounter++;
								}
								if (words.length == 1) {
									foundCounter++;
									// System.out.println(foundCounter);
									break;
								}
							}
						}
					}

				}
				// System.out.println(foundCounter);

				if (foundCounter == words.length) {
					numSlangs++;
					// System.out.println("num slangs " + numSlangs+
					// " for phrase ");
					for (String p : words) {
						// System.out.println("- " + p);
					}
				}

			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("NUM TO RETURN " + numSlangs);
		return numSlangs;
	}

	static ArrayList<Integer[]> occurrenceArr = new ArrayList<Integer[]>();

	/**
	 * Function that given a string, a pattern and an initial index, finds the
	 * indexes where the pattern is observed. The method is recursive, given as
	 * initial index for searching, the last index of the previous iteration.
	 * 
	 * @param str
	 *            String that holds the adapted text of the original tweet text
	 * @param pattern
	 *            String that holds the pattern whose existence is checked
	 * @param index
	 *            the start index from where the pattern is searched
	 * @return ArrayList<Integer[]> list of the pairs [startIndex, endIndex] of
	 *         the patern occurrences found
	 */
	public static ArrayList<Integer[]> findPatternOccurrence(String str,
			String pattern, int index) {

		Integer occurStart = str.indexOf("http", index);
		// System.out.println("start " + occurStart);
		// System.out.println("string length "+str.length());
		Integer[] occurs = new Integer[2];
		Integer occurEnd = -1;
		if (occurStart != -1) {
			occurs[0] = occurStart;
			occurEnd = str.indexOf(" ", occurStart);

			if (str.substring(occurStart, occurEnd).contains("\n")) {
				occurEnd = str.indexOf("\n", occurStart);

			}
			// System.out.println("end " + occurEnd);
		}

		if (occurEnd.equals(-1)) {
			occurs[1] = str.length();
			// System.out.println("end again "+occurs[1]);
		} else {
			occurs[1] = occurEnd;
		}
		// System.out.println("string "+itemTitle.substring(occurStart,occurs[1]));

		if (occurs[0] != null && occurs[1] != null
				&& ((occurs[1] - occurs[0]) > 7)) {
			// System.out.println("occurs before added "+occurs[0]+" "+occurs[1]);
			occurrenceArr.add(occurs);
		}
		// System.out.println(occurEnd+" "+occurStart);
		if (occurEnd < str.length() && !occurStart.equals(-1)
				&& !occurEnd.equals(-1)) {
			// System.out.println("hello");
			findPatternOccurrence(str, pattern, occurEnd + 1);

		}

		return occurrenceArr;
	}



	/**
	 * Organizes the procedure of computing the readability of the comments's text
	 * 
	 * @return Double the calculated value of readability
	 */
	public static Double getReadability() {

		Double readability = null;

		String str = text.replaceAll("http+s*+://[^ ]+", "")
				.replaceAll("#[^ ]+ ", "").replaceAll("@[^ ]+", "");
		str = str.replaceAll("  ", " ");

		if (!str.isEmpty()) {
			Readability r = new Readability(str);
			readability = r.getFleschReadingEase();

		}
		return readability;
	}

}
