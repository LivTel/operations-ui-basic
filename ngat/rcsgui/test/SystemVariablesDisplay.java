package ngat.rcsgui.test;

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.awt.*;

import javax.swing.*;

import ngat.opsgui.base.*;
import ngat.opsgui.util.*;
import ngat.opsgui.components.*;
import ngat.util.*;
import ngat.tcm.*;
import ngat.astrometry.BasicSite;
import ngat.ems.*;
import ngat.rcs.ers.*;
import ngat.rcs.ers.test.*;
import ngat.rcs.newstatemodel.*;

public class SystemVariablesDisplay  extends UnicastRemoteObject implements ReactiveSystemUpdateListener {

    ngat.opsgui.util.StateField weatherField;
    ngat.opsgui.util.StateField systemField;
    ngat.opsgui.util.StateField axesField;
    ngat.opsgui.util.StateField ctrlField;
    ngat.opsgui.util.StateField networkField;
    ngat.opsgui.util.StateField encField;
    ngat.opsgui.util.StateField pmcField;
    ngat.opsgui.util.StateField intentField;
    ngat.opsgui.util.StateField todField;

    StateColorMap stateMap;

    JPanel panel;

    public SystemVariablesDisplay() throws RemoteException {
	
	stateMap = createColorMap();

	panel = createPanel();

    }

	private StateColorMap createColorMap() {

	    StateColorMap stateMap = new StateColorMap(Color.yellow.darker(), "UNKNOWN");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  SYSTEM_SUSPEND, Color.RED, "SUSPEND");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  SYSTEM_STANDBY, Color.YELLOW, "STANDBY");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  SYSTEM_OKAY, Color.GREEN, "OKAY");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  SYSTEM_FAIL, Color.RED, "FAIL");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  CONTROL_ENABLED, Color.GREEN, "ENABLED");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  CONTROL_DISABLED, Color.RED, "DISABLED");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  NETWORK_OKAY, Color.GREEN, "ONLINE");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  NETWORK_ALERT, Color.RED, "OFFLINE");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  WEATHER_ALERT, Color.RED, "ALERT");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  WEATHER_CLEAR, Color.GREEN, "CLEAR");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  ENCLOSURE_OPEN, Color.GREEN, "OPEN");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  ENCLOSURE_CLOSED, Color.RED, "CLOSED");
	    //stateMap.addColorLabel(EnvironmentChangeEvent.  ENCLOSURE_ERROR, Color.RED, "FAIL");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  AXES_OKAY, Color.GREEN, "OKAY");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  AXES_ERROR, Color.RED, "FAIL");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  INTENT_OPERATIONAL, Color.GREEN, "AUTO");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  INTENT_ENGINEERING, Color.YELLOW, "ENG");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  DAY_TIME, Color.ORANGE, "DAYTIME");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  NIGHT_TIME, Color.BLUE, "NIGHTIME");

	    stateMap.addColorLabel(EnvironmentChangeEvent.  MIRR_COVER_OPEN, Color.GREEN, "OPEN");
	    stateMap.addColorLabel(EnvironmentChangeEvent.  MIRR_COVER_CLOSED, Color.RED, "CLOSED");
	    //stateMap.addColorLabel(EnvironmentChangeEvent.  MIRR_COVER_ERROR,Color.RED, "FAIL");

	    return stateMap;

	}

    public JPanel getPanel() {
	return panel;
    }

    private JPanel createPanel() {
	
	JPanel p = new JPanel(true);
	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

	weatherField = addLine(p, "Weather");
	systemField = addLine(p, "System");
	axesField = addLine(p, "Axes");
	ctrlField = addLine(p, "Control");
	networkField = addLine(p, "Network");
	encField = addLine(p, "Enclosure");
	pmcField = addLine(p, "PMC");
	intentField = addLine(p, "Intent");
	todField = addLine(p, "Time");
	
	
	return p;

    }

    private ngat.opsgui.util.StateField addLine(JPanel panel, String label) {
	
	ngat.opsgui.util.StateField field = ComponentFactory.makeStateField(10, stateMap);

        LinePanel line = ComponentFactory.makeLinePanel();
        line.add(ComponentFactory.makeLabel(label));
        line.add(field);
        panel.add(line);
	// this will set the default state
	field.updateState(-999);
	return field;
    }
    

    public static void main(String args[]) {

	try {
	ConfigurationProperties config = CommandTokenizer.use("--").parse(args);

	Resources.setDefaults(config.getProperty("base", "/home/eng/rcsgui/"));

	SystemVariablesDisplay svd = new  SystemVariablesDisplay();

	JFrame f = new JFrame("System Variables");

	JPanel panel = svd.getPanel();

	f.getContentPane().add(panel);
	f.pack();
	f.setVisible(true);

	String xml = config.getProperty("xml");

	BasicReactiveSystem ts = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
	XmlConfigurator.use(new File(xml)).configure(ts);
	System.err.println("Created TRS");

	ts.addReactiveSystemUpdateListener(svd);
	System.err.println("Created Added svd as rssl");

	String host = config.getProperty("host", "localhost");

	Telescope scope = (Telescope) Naming.lookup("rmi://"+host+"/Telescope");
	System.err.println("Found scope: " + scope);
	TelescopeStatusProvider tsp = scope.getTelescopeStatusProvider();
	System.err.println("Found TSP: " + tsp);
	tsp.addTelescopeStatusUpdateListener(ts);
	
	MeteorologyStatusProvider meteo = (MeteorologyStatusProvider) Naming.lookup("rmi://"+host+"/Meteorology");
	meteo.addMeteorologyStatusUpdateListener(ts);

	ts.startCacheReader();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
	public void criterionUpdated(String cname, long t, boolean cout) throws RemoteException {}
    @Override
	public void filterUpdated(String fname, long t, Number fin, Number fout) throws RemoteException {}
    
    @Override
	public void ruleUpdated(String rname, long t, boolean rout) throws RemoteException {
	System.err.printf("Rule   updated: %tF %tT %6s %4b \n", t, t, rname, rout);

	if (! rout)
	    return;

	if (rname.equals("METEO_ALERT")) 
	    weatherField.updateState(EnvironmentChangeEvent.WEATHER_ALERT);
	else if (rname.equals("METEO_CLEAR"))
            weatherField.updateState(EnvironmentChangeEvent.WEATHER_CLEAR);
	else if (rname.equals("AXES_ALERT"))
            axesField.updateState(EnvironmentChangeEvent.AXES_ERROR);
	else if (rname.equals("AXES_CLEAR"))
            axesField.updateState(EnvironmentChangeEvent.AXES_OKAY);
	else if (rname.equals("CONTROL_ALERT"))
            ctrlField.updateState(EnvironmentChangeEvent.CONTROL_DISABLED);
	else if (rname.equals("CONTROL_CLEAR"))
            ctrlField.updateState(EnvironmentChangeEvent.CONTROL_ENABLED);
	else if (rname.equals("NETW_ALERT"))
            networkField.updateState(EnvironmentChangeEvent.NETWORK_ALERT);
	else if (rname.equals("NETW_CLEAR"))
            networkField.updateState(EnvironmentChangeEvent.NETWORK_OKAY);
	else if (rname.equals("SYS_OKAY"))
            systemField.updateState(EnvironmentChangeEvent.SYSTEM_OKAY);
	else if (rname.equals("SYS_STBY"))
            systemField.updateState(EnvironmentChangeEvent.SYSTEM_STANDBY);
	else if (rname.equals("SYS_SUSP"))
            systemField.updateState(EnvironmentChangeEvent.SYSTEM_SUSPEND);
	else if (rname.equals("SYS_FAIL"))
            systemField.updateState(EnvironmentChangeEvent.SYSTEM_FAIL);
	else if (rname.equals("ENC_OPEN"))
	    encField.updateState(EnvironmentChangeEvent.ENCLOSURE_OPEN);
	else if (rname.equals("ENC_CLOSED"))
            encField.updateState(EnvironmentChangeEvent.ENCLOSURE_CLOSED);
	else if (rname.equals("PMC_OPEN"))
            pmcField.updateState(EnvironmentChangeEvent.MIRR_COVER_OPEN);
        else if (rname.equals("PMC_CLOSED"))
            pmcField.updateState(EnvironmentChangeEvent.MIRR_COVER_CLOSED);

    }
}