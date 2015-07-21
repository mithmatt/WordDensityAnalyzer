/**
 * User defined exception class for handling exceptions caused udring parsing of
 * HTML or connection
 * 
 * @author Matt
 * @version 1.0
 */
public class ParseHtmlException extends Exception {

	private static final long serialVersionUID = 12L;

	/**
	 * Constructor class
	 * 
	 * @param msg
	 *            exception message
	 */
	public ParseHtmlException(String msg) {
		super(msg);
	}

}
