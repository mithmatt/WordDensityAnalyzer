/**
 * DotThread class for printing dots while waiting for results
 * 
 * @author Matt
 * @version 1.0
 */
public class DotThread extends Thread {

	/**
	 * Method to print a dot every 250ms
	 */
	public void run() {
		while (true) {
			System.out.print(".");
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}
	}

}
