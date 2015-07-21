import java.util.ArrayDeque;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class for parsing the HTML page
 * 
 * @author Matt
 * @version 1.0
 */
public class ParseHtml {

	String url;
	Document doc = null;

	/**
	 * Constructor to initialize the object
	 * 
	 * @param url
	 *            the website to be parsed
	 * @throws ParseHtmlException
	 */
	public ParseHtml(String url) throws ParseHtmlException {
		this.url = url;

		try {
			doc = Jsoup.connect(url).timeout(7000).get();
		} catch (Exception e2) {
			throw new ParseHtmlException(
					"Please check URL and Internet Connectivity, and try again.");
		}
	}

	/**
	 * Parses the HTML document and returns the title of the document
	 * 
	 * @return title
	 */
	public String parseTitle() {
		return doc.title();
	}

	/**
	 * Parses the HTML document and returns the metadata description
	 * 
	 * @return
	 */
	public String parseMetaDescription() {

		Elements description = doc.select("meta[name=description]");

		StringBuilder sb = new StringBuilder();

		for (Element each : description) {
			sb.append(" ");
			sb.append(each.attr("content"));
		}

		return sb.toString();
	}

	/**
	 * Parses the HTML document and gets the headers h* (h1, h2, ...) based on
	 * the input no.
	 * 
	 * @param headerNo
	 *            the header no.
	 * @return headers
	 */
	public ArrayDeque<String> parseHeaders(int headerNo) {

		Elements headers = doc.select("h" + headerNo);
		ArrayDeque<String> temp = new ArrayDeque<String>();

		for (Element each : headers)
			temp.add(each.text());

		return temp;
	}

	/**
	 * Parses the whole HTML document and returns the text within it
	 * 
	 * @return the whole text in the HTML document
	 */
	public String parseDocument() {
		return doc.body().text();
	}

}
