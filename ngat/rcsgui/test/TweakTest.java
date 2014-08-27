/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.Naming;
import java.util.Date;

import ngat.astrometry.AstroFormatter;
import ngat.message.ISS_INST.CONFIG;
import ngat.message.ISS_INST.TIMED_MULTRUNAT;
import ngat.net.cil.CilService;
import ngat.net.cil.test.TestHandler;
import ngat.phase2.THORConfig;
import ngat.phase2.THORDetector;
import ngat.phase2.XPeriodRunAtExposure;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class TweakTest {

	public static final long TWEAK_TIMEOUT = 20000L;

	public static final long OFFBY_TIMEOUT = 20000L;

	public static final String CIL_SVC = "TCSCilService";

	private String thorHost;

	private int thorPort;

	private String cilHost;

	private double ra;

	private double dec;

	private CilService cil;

	private long initDelay;

	private long tweakDelay;

	private boolean tweaks;

	/**
	 * @param thorHost
	 * @param thorPort
	 * @param cilHost
	 * @param ra
	 * @param dec
	 */
	public TweakTest(String thorHost, int thorPort, String cilHost, double ra, double dec, long initDelay,
			long tweakDelay, boolean tweaks) throws Exception {
		super();
		this.thorHost = thorHost;
		this.thorPort = thorPort;
		this.cilHost = cilHost;
		this.ra = ra;
		this.dec = dec;
		this.initDelay = initDelay;
		this.tweakDelay = tweakDelay;
		this.tweaks = tweaks;

		cil = (CilService) Naming.lookup("rmi://" + cilHost + "/" + CIL_SVC);

	}

	public static void main(String args[]) {

		try {

			ConfigurationProperties config = CommandTokenizer.use("--").parse(args);

			String cilHost = config.getProperty("cil-host");

			String thorHost = config.getProperty("thor-host");

			int thorPort = config.getIntValue("thor-port");

			double ra = AstroFormatter.parseHMS(config.getProperty("ra"), ":");
			double dec = AstroFormatter.parseDMS(config.getProperty("dec"), ":");

			long initDelay = config.getIntValue("init-delay", 30000);
			long twkDelay = config.getIntValue("tweak-delay", 30000);

			boolean tweaks = config.getProperty("mode", "tweak").equalsIgnoreCase("tweak");

			TweakTest tt = new TweakTest(thorHost, thorPort, cilHost, ra, dec, initDelay, twkDelay, tweaks);

			tt.startThorThread();

			// wait a few seconds then start tweaking
			tt.startTweakThread();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startThorThread() {
		ThorThread tt = new ThorThread();
		tt.start();
	}

	public void startTweakThread() {
		TweakThread tt = new TweakThread();
		tt.start();
	}

	private class ThorThread extends Thread {

		@Override
		public void run() {
			try {

				// Configure thor
				THORConfig thorc = new THORConfig("thor-1");
				THORDetector thordetector = (THORDetector) thorc.getDetector(0);
				thordetector.clearAllWindows();
				thordetector.setXBin(1);
				thordetector.setYBin(1);
				thorc.setEmGain(100);
				CONFIG config = new CONFIG("thor");
				config.setConfig(thorc);

				JMSCLient1 jms = new JMSCLient1("THOR:CFG", thorHost, thorPort, config, 60000L);
				jms.run();

				// 500ms for 5 minutes
				long now = System.currentTimeMillis();
				XPeriodRunAtExposure xper = new XPeriodRunAtExposure(500, 2 * 60 * 1000.0, now);

				TIMED_MULTRUNAT timedmultrunat = new TIMED_MULTRUNAT("test");
				timedmultrunat.setTotalDuration((int) xper.getTotalExposureDuration());
				timedmultrunat.setExposureTime((int) xper.getExposureLength());
				timedmultrunat.setStartTime(new Date(now));

				JMSCLient1 jms2 = new JMSCLient1("THOR:EXP", thorHost, thorPort, timedmultrunat, 200000L);
				jms2.run();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private class TweakThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(initDelay);
			} catch (Exception e) {
			}

			if (tweaks) {
				try {
					tweak(5.0, 0.0);
					tweak(-5.0, 5.0);
					tweak(0.0, -5.0);
					tweak(-5.0, 0.0);
					tweak(5.0, -5.0);
					tweak(0.0, 5.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					offby(5.0, 0.0);
					offby(0.0, 5.0);
					offby(0.0, 0.0);
					offby(-5.0, 0.0);				
					offby(0.0, -5.0);
					offby(0.0, 0.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void tweak(double x, double y) throws Exception {

			TestHandler handler2 = new TestHandler();

			String message = String.format("TWEAK %6.4f %6.4f", x, y);
			log("CIL", "Sending: " + message);
			cil.sendMessage(message, handler2, TWEAK_TIMEOUT);
			handler2.waitNotification(TWEAK_TIMEOUT + 5000L);
			if (handler2.error) {
				log("CIL", "TWEAK failed: " + handler2.errMsg);
				return;
			} else {
				log("CIL", "TWEAK completed");
			}

			try {
				Thread.sleep(tweakDelay);
			} catch (Exception e) {
			}
		}

		private void offby(double x, double y) throws Exception {

			TestHandler handler2 = new TestHandler();

			String message = String.format("OFFBY ARC %6.4f %6.4f", x, y);
			log("CIL", "Sending: " + message);
			cil.sendMessage(message, handler2, OFFBY_TIMEOUT);
			handler2.waitNotification(OFFBY_TIMEOUT + 5000L);
			if (handler2.error) {
				log("CIL", "OFFBY failed: " + handler2.errMsg);
				return;
			} else {
				log("CIL", "OFFBY completed");
			}

			try {
				Thread.sleep(tweakDelay);
			} catch (Exception e) {
			}
		}
	}

	public void log(String name, String message) {
		long time = System.currentTimeMillis();
		System.err.printf("%tF %tT :           %5s : %s \n", time, time, name, message);
	}

}
