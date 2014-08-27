/**
 * 
 */
package ngat.opsgui.test;

import ngat.util.logging.BasicLogFormatter;
import ngat.util.logging.FileLogHandler;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class OpenXLoggerToFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			FileLogHandler ff = new FileLogHandler("/home/eng/flogtest", new BasicLogFormatter(), FileLogHandler.HOURLY_ROTATION);
			ff.setLogLevel(5);
			
			Logger alogger = LogManager.getLogger("TEST");
			alogger.setLogLevel(5);
			alogger.addExtendedHandler(ff);
			
			LogGenerator logger = alogger.generate().system("TESTING").subSystem("FileTest").srcCompClass("OpenXLogger").srcCompId("1");
			
			for (int i = 0; i < 100; i++) {			
				int ll = (int)(1.0 + Math.random()*4.0);
				logger.create()
				.category("Msg")
				.fatal()	
				.level(ll)
				.msg("The "+i+"th message for you at level "+ll)
				.send();
			}
			
			ff.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
