package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.util.*;

/** Transports TelFocus completion information.*/

//public class TelFocusInfo extends TelemetryInfo {


public class TelFocusInfo {

    public long time;

    /** Stores the focus/fwhm measurements as CoordinatePairs.*/
    public Vector measurements;
    
    /** */
    public int run;

    /** */
    public double focusStart;

    /** */
    public double focusStop;

    /** */
    public double focusInc;

    /** */
    public double calibStarMagnitude;

    /** */
    public double snr;

    /** */
    public long startTime;
    
    /** */
    public long endTime;

    /** */
    public double aParam;

    /** */
    public double bParam;

    /** */
    public double cParam;

    /** */
    public double chi;

    /** */
    public double optimalFocus;

    /** */
    public double minFwhm;

    @Override
	public String toString() {
	return "TelFocusInfo : "+time+" : Run="+run+
	    ", FocusStart="+focusStart+
	    ", FocusStop="+focusStop+
	    ", FocusInc="+focusInc+
	    ", StartTime="+startTime+
	    ", EndTime="+endTime+
	    ", CalibMag="+calibStarMagnitude+
	    ", SNR="+snr+
	    ", AParm="+aParam+
	    ", BParm="+bParam+
	    ", CParm="+cParam+
	    ", Chi2="+chi+
	    ", Optimal="+optimalFocus+
	    ", Seeing="+minFwhm;
    }

}
