package ngat.rcsgui.test;

import java.rmi.*;
import java.rmi.server.*;
import ngat.rcs.ers.*;


public class WeatherRulesUpdateHandler extends UnicastRemoteObject implements ReactiveSystemUpdateListener {

    private WeatherRulesUpdatePanel wrup;

    double humhi, wshi, rhi, mhi, wmshi, tlo, chi, dhi;
    double hlo1, hlo2, wslo, rlo, wmslo, mlo, thi, clo1, clo2, dlo;

    public WeatherRulesUpdateHandler(WeatherRulesUpdatePanel wrup) throws RemoteException {
	super();
	this.wrup = wrup;
    }


    @Override
	public void filterUpdated(String filterName, long time, Number updateValue, Number filterOutputValue) 
	throws RemoteException {}

    @Override
	public void criterionUpdated(String critName, long time, boolean critOutputValue)
	throws RemoteException {}

    @Override
	public void ruleUpdated(String ruleName, long time, boolean ruleOutputValue) 
	throws RemoteException {

	boolean v = ruleOutputValue;
	
	if (ruleName.equals("HUM_HI")) {
	    humhi = x(v);    
	} else if (ruleName.equals("WS_HI")) {
	    wshi = x(v);
	} else if (ruleName.equals("RAIN_ALERT")) {
	    rhi = x(v);
	} else if (ruleName.equals("MOIST_HI")) {
	    mhi = x(v);
	} else if (ruleName.equals("WMS_ALERT")) {
	    wmshi = x(v);
	} else if (ruleName.equals("TEMP_LO")) {
	    tlo = x(v);
	} else if (ruleName.equals("CLOUD_HI")) {
	    chi = x(v);
	} else if (ruleName.equals("DUST_HI")) {
	    dhi = x(v);

	} else if (ruleName.equals("HUM_LO_1")) {
	    hlo1 = x(v);
	} else if (ruleName.equals("HUM_LO_2")) {
	    hlo2 = x(v);
	} else if (ruleName.equals("WS_LO")) {
	    wslo = x(v);
	} else if (ruleName.equals("RAIN_CLEAR")) {
	    rlo = x(v);
	} else if (ruleName.equals("WMS_CLEAR")) {
	    wmslo = x(v);
	} else if (ruleName.equals("MOIST_LO")) {
	    mlo = x(v);
	} else if (ruleName.equals("TEMP_HI")) {
	    thi = x(v);
	} else if (ruleName.equals("CLOUD_LO_1")) {
	    clo1 = x(v);
	} else if (ruleName.equals("CLOUD_LO_2")) {
	    clo2 = x(v);
	} else if (ruleName.equals("DUST_LO")) {
	    dlo = x(v);
	}

	// humhi, hlo1, hlo2
	wrup.updateHumidity(humhi, 0.5*(hlo1+hlo2));

	// wshi, wslo
	wrup.updateWs(wshi, wslo);

	// rhi, rlo
	wrup.updateRain(rhi, rlo);

	// mhi, mlo
	wrup.updateMoist(mhi, mlo);

	// wmshi, wmslo
	wrup.updateWms(wmshi, wmslo);

	// tlo, thi
	wrup.updateTemp(tlo, thi);

	// chi, clo1, clo2
	wrup.updateCloud(chi, 0.5*(clo1+clo2));

	// dhi, dlo1, dlo2
	wrup.updateDust(dhi, dlo);

    }

    private double x(boolean v) {
	return (v ? 1.0 : 0.0);
    }
    

}