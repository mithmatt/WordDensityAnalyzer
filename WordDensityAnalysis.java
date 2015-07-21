import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Main class for running the program and obtaining the words that describe the
 * website
 * 
 * @author Matt
 * @version 1.0
 */
public class WordDensityAnalysis {

	// map for keywords (phrases) and their assigned relevance values
	static HashMap<String, Double> keyWords = new HashMap<String, Double>();
	// map to have the list of keywords sorted by descending order of their
	// relevance
	static TreeMap<Double, ArrayDeque<String>> sortedKeyWords = new TreeMap<Double, ArrayDeque<String>>(
			Collections.reverseOrder());

	// no of keywords to be printed
	final static int TOP_LIMIT = 5;

	// weights associated to keywords from different parts of the document
	final static double URL_WEIGHT = 2.0;
	final static double TITLE_WEIGHT = 3.5;
	final static double METADATA_WEIGHT = 3.0;
	final static double HEADER_WEIGHT = 1.0;
	final static double NORMAL_WEIGHT = 0.01;

	final static int NGRAM_LEN = 4;

	/**
	 * Main method
	 * 
	 * @param args
	 *            first command argument is the URL for the website
	 */
	public static void main(String[] args) {
		try {
			String url = args[0];
			if (url == null)
				throw new NullPointerException(
						"No website URL entered. Try again!");
			printKeywords(url);
		} catch (NullPointerException e) {
			System.out.println("Oops! Something seems to have gone wrong!");
			System.out.println(e.getLocalizedMessage());
		} catch (ParseHtmlException e) {
			System.out.println("Oops! Something seems to have gone wrong!");
			System.out.println(e.getLocalizedMessage());
		} catch (Exception e) {
			System.out.println("Oops! Something seems to have gone wrong!");
		}
	}

	/**
	 * Method implementing the core logic and calling all other necessary
	 * methods to print the list of keywords most relevant to the website
	 * 
	 * @param url
	 *            the URL of the website
	 * @throws Exception
	 */
	private static void printKeywords(String url) throws Exception {

		ParseHtml pHtml = new ParseHtml(url);
		HashMap<String, Double> temp = null;

		System.out.print("Please be patient while the website is being parsed");

		// start printing dots
		DotThread dots = new DotThread();
		dots.start();

		int startIndex = getIndex(url, '/', 3);
		int endIndex = getIndex(url, '?', 1);

		// obtaining the keyword mapping from the URL itself
		keyWords = NGramGenerator.ngrams(url.substring(startIndex,
				((endIndex > startIndex) ? endIndex : url.length())),
				NGRAM_LEN, URL_WEIGHT);

		// obtaining the keyword mapping from the title of the HTML document and
		// adding to the keyWords hashmap
		temp = NGramGenerator.ngrams(pHtml.parseTitle(), NGRAM_LEN,
				TITLE_WEIGHT);
		addToMap(temp);

		// obtaining the keyword mapping from the metadata description of the
		// HTML document and adding to the keyWords hashmap
		temp = NGramGenerator.ngrams(pHtml.parseMetaDescription(), NGRAM_LEN,
				METADATA_WEIGHT);
		addToMap(temp);

		// obtaining the keyword mapping from the different headers of the HTML
		// document and adding to the keyWords hashmap
		for (int i = 1; i <= 4; i++) {
			for (String eachHeader : pHtml.parseHeaders(i)) {
				temp = NGramGenerator.ngrams(eachHeader, NGRAM_LEN,
						HEADER_WEIGHT * Math.pow(2, 1 - i));
				addToMap(temp);
			}
		}

		// obtaining the keyword mapping from the HTML document text and adding
		// to the keyWords hashmap
		temp = NGramGenerator.ngrams(pHtml.parseDocument(), NGRAM_LEN,
				NORMAL_WEIGHT);
		addToMap(temp);

		// transforming the hashmap to a treemap sorted on relevance
		transformMap();

		// stop printing dots
		dots.stop();

		// print out the top the N keywords
		printTop();
	}

	/**
	 * Adds the key - value pairs to the keyword hashmap. If the keyword exists,
	 * add the relevance to the existing entry, else create a new entry
	 * 
	 * @param temp
	 *            map whose entries have to be added to the keyword map
	 */
	private static void addToMap(HashMap<String, Double> temp) {
		for (String each : temp.keySet()) {
			double value = 0.0;

			if (keyWords.containsKey(each))
				value = keyWords.get(each);

			keyWords.put(each, value + temp.get(each));
		}
	}

	/**
	 * Transforms hashmap of (keyword -> relevance) to (relevance ->
	 * List[keyword]) sorted by relevance
	 */
	private static void transformMap() {
		for (String each : keyWords.keySet()) {
			double value = keyWords.get(each);

			// transform only if relevance greater than 1.0
			if (value > 1.0) {
				ArrayDeque<String> tempList = new ArrayDeque<String>();
				if (sortedKeyWords.containsKey(value))
					tempList = sortedKeyWords.get(value);

				tempList.add(each);
				sortedKeyWords.put(value, tempList);
			}
		}
	}

	/**
	 * Iterates the keyword map sorted by relevance, and prints top n keywords
	 * 
	 * @param n
	 *            no. of top keywords to be printed out
	 */
	private static void printTop() {

		DecimalFormat df = new DecimalFormat("#.000");

		int i = 1;
		System.out.println("\n\nRank\tRelevance\tKeywords");
		System.out.println("----\t---------\t--------");

		for (double each : sortedKeyWords.keySet()) {
			// break after nth keyword
			if (i > TOP_LIMIT)
				break;

			System.out.println(i + "\t" + df.format(each) + "\t\t"
					+ printValues(sortedKeyWords.get(each)));
			i++;
		}

		System.out
				.println("\n\n- Higher the rank, the most relevant is the keyword\n"
						+ "- Relevance is relative\n"
						+ "- The list of keywords are comma separated");
	}

	/**
	 * Helper method to print the values from the array deque as a string
	 * 
	 * @param keywords
	 *            array deque of keywords
	 * @return formatted string of keywords
	 */
	private static String printValues(ArrayDeque<String> keywords) {

		StringBuilder sb = new StringBuilder();
		Iterator it = keywords.iterator();

		if (it.hasNext())
			sb.append(it.next());

		while (it.hasNext()) {
			sb.append(", ");
			sb.append(it.next());
		}

		return sb.toString();
	}

	/**
	 * Method returns the nth occurrence of a character in a string
	 * 
	 * @param input
	 *            string
	 * @param ch
	 *            character
	 * @param n
	 *            nth occurrence
	 * @return -1 if character not found or nth occurrence does not exist, else
	 *         nth occurrence
	 */
	private static int getIndex(String input, char ch, int n) {
		int count = 1;
		int index = input.indexOf(ch);

		if (index == -1)
			return -1;
		else {
			int length = input.length();
			for (int i = index + 1; i < length; i++)
				if (input.charAt(i) == ch) {
					if (++count == n) {
						index = i;
						break;
					}
				}
			return index;
		}
	}

}
