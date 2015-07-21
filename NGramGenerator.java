import java.util.HashMap;

/**
 * Class that contains the static method to generate n-grams from an input
 * string
 * 
 * @author Matt
 * @version 1.0
 */
public class NGramGenerator {

	// min limit for no. of words in an n-gram
	final static int MIN_LIMIT = 2;

	// min phrase character length to be considered for n-gram
	final static int MIN_PHRASE_LEN = 4;

	/**
	 * Static method to generate a hashmap with ngrams and their computed
	 * relevance (double) from an input string
	 * 
	 * @param line
	 *            input string
	 * @param n
	 *            max-limit of no. of words for n-grams
	 * @param relevance
	 *            double value to be added as relevance for each n-gram
	 * @return a hashmap of (keyword -> relevance)
	 */
	public static HashMap<String, Double> ngrams(String line, int n,
			double relevance) {

		HashMap<String, Double> phrases = new HashMap<String, Double>();

		// split the input line into words
		String[] words = line.toLowerCase().split("[^a-z]");

		for (int k = MIN_LIMIT; k <= n; k++) {
			for (int i = 0; i < words.length - k + 1; i++) {

				StringBuilder phraseBuild = new StringBuilder();

				// generate the phrase
				for (int j = i; j < i + k; j++) {
					if (j > i)
						phraseBuild.append(" ");
					phraseBuild.append(words[j]);
				}

				// replace all multiple spaces by single space
				String phrase = phraseBuild.toString().trim()
						.replaceAll("( )+", " ");

				if (phrase.length() > MIN_PHRASE_LEN) {
					double curRelevance = 0.0;

					if (phrases.containsKey(phrase))
						curRelevance = phrases.get(phrase);

					// add to existing relevance
					phrases.put(phrase, curRelevance + relevance);
				}
			}
		}

		return phrases;
	}
}
