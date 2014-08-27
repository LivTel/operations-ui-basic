/**
 * 
 */
package ngat.opsgui.base;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ngat.opsgui.util.StateColorMap;
import ngat.phase2.IObservingConstraint;
import ngat.phase2.XSkyBrightnessConstraint;

/** Provides graphical and other GUI resources.
 * @author eng
 *
 */
public class Resources {

	private String base;
	
	private static Map<String, Icon> icons;
	
	private static Map<String, Font> fonts;
	
	private static Map<String, String> labels;
	
	private static Map<String, Color> colors;
	
	private static Map<String, StateColorMap> colorMaps;
	
	public static Icon getIcon(String key) throws MissingResourceException {
		return icons.get(key);
	}
	
	public static String getLabel(String key) throws MissingResourceException {
		return labels.get(key);
	}
	
	public static Font getFont(String key) throws MissingResourceException {
		if (fonts.containsKey(key))
			return fonts.get(key);
		return new Font("helvetica", Font.PLAIN, 8);
	}
	
	public static Color getColor(String key) throws MissingResourceException {
		return colors.get(key);
	}
	
	public static StateColorMap getColorMap(String key) throws MissingResourceException {
		return colorMaps.get(key);
	}
	
	public static void setDefaults(String base) {
		// add some fonts
		fonts = new HashMap<String, Font>();
		fonts.put("label.font", new Font("palatino", Font.PLAIN, 10));
		fonts.put("display.label.font", new Font("palatino", Font.PLAIN, 12));
		fonts.put("text.font", new Font("helvetica", Font.PLAIN, 9));
		fonts.put("text.button.font", new Font("helvetica", Font.PLAIN, 9));
		fonts.put("tabs.title.font", new Font("serif",  Font.BOLD + Font.ITALIC, 26));
		fonts.put("border.title.font", new Font("helevetica",  Font.PLAIN, 9));
		fonts.put("chart.title.font", new Font("helvetica", Font.PLAIN, 9));
		fonts.put("chart.legend.font", new Font("Dialog", Font.PLAIN, 10));
		fonts.put("login.title.font", new Font("helevetica",  Font.ITALIC, 18));
		
		// colors
		colors = new HashMap<String, Color>();
		colors.put("tabs.foreground.color", Color.blue);
		colors.put("chart.background.color", new Color(222,222,252));
		colors.put("chart.legend.background.color", new Color(200, 200, 255, 50));
		
		// colormaps
		colorMaps = new HashMap<String, StateColorMap>();
		StateColorMap skyBrightnessColorMap = new StateColorMap(Color.gray, "UNKNOWN");
	
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.DARK, new Color(0, 0, 128), "DARK");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_0P75, new Color(205, 92, 92), "0.75");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_1P5, new Color(135, 206, 250), "1.5");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_2, new Color(176, 224, 230), "2.0");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_4, new Color(25, 38, 238), "4.0");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_6, new Color(224, 55, 255), "6.0");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.MAG_10, new Color(27, 255, 0), "10.0");
		skyBrightnessColorMap.addColorLabel(IObservingConstraint.DAYTIME, new Color(255, 215, 0), "DAYTIME");
		
		colorMaps.put("sky.brightness.colors", skyBrightnessColorMap);
		
		StateColorMap mechanismStateColorMap;
		
		StateColorMap serviceStateColorMap;
		
		
		
		// icons
		icons = new HashMap<String, Icon>();
		
		icons.put("login.icon", new ImageIcon(base+"/icons/login-icon.png"));
		icons.put("clippy.icon", new ImageIcon(base+"/icons/lt.gif"));
		
		icons.put("scheduling.perspective.icon", new ImageIcon(base+"/icons/scheduling-perspective-icon.png"));
		icons.put("tracking.perspective.icon",   new ImageIcon(base+"/icons/tracking-perspective-icon.png"));
		icons.put("astrometry.perspective.icon", new ImageIcon(base+"/icons/astrometry-perspective-icon.png"));
		icons.put("services.perspective.icon",   new ImageIcon(base+"/icons/services-perspective-icon.png"));
		icons.put("phase2.perspective.icon",     new ImageIcon(base+"/icons/phase2-perspective-icon.png"));
		icons.put("meteorology.perspective.icon",new ImageIcon(base+"/icons/meteorology-perspective-icon.png"));
		icons.put("instruments.perspective.icon",new ImageIcon(base+"/icons/instruments-perspective-icon.png"));
		
		icons.put("trend.graph.icon",      new ImageIcon(base+"/icons/trend-graph-icon.png"));
		icons.put("statistics.graph.icon", new ImageIcon(base+"/icons/statistics-graph-icon.png"));
		
		icons.put("summary.expand.icon",          new ImageIcon(base+"/icons/shortcut-icon.png"));
		icons.put("summary.expand.selected.icon", new ImageIcon(base+"/icons/feed-plus-icon.png"));
		icons.put("summary.expand.rollover.icon", new ImageIcon(base+"/icons/arrow-move-icon.png"));
		
		icons.put("run.forward.icon",   new ImageIcon(base+"/icons/run-forward-icon.png"));
		icons.put("step.forward.icon",  new ImageIcon(base+"/icons/step-forward-icon.png"));
		icons.put("fast.forward.icon",  new ImageIcon(base+"/icons/fast-forward-icon.png"));
		icons.put("goto.end.icon",      new ImageIcon(base+"/icons/goto-end-icon.png"));
		icons.put("run.backward.icon",  new ImageIcon(base+"/icons/run-backward-icon.png"));
		icons.put("step.backward.icon", new ImageIcon(base+"/icons/step-backward-icon.png"));
		icons.put("fast.backward.icon", new ImageIcon(base+"/icons/fast-backward-icon.png"));
		icons.put("goto.start.icon",    new ImageIcon(base+"/icons/goto-start-icon.png"));
		icons.put("pause.icon",         new ImageIcon(base+"/icons/pause-icon.png"));
		
		icons.put("pan.left.icon",     new ImageIcon(base+"/icons/run-backward-icon.png"));
		icons.put("pan.right.icon",    new ImageIcon(base+"/icons/run-forward-icon.png"));
		icons.put("zoom.in.icon",      new ImageIcon(base+"/icons/zoom-in-icon.png"));
		icons.put("zoom.out.icon",     new ImageIcon(base+"/icons/zoom-out-icon.png"));
		icons.put("reset.time.icon",   new ImageIcon(base+"/icons/reset-time-icon.png"));

		icons.put("sweep.first.icon",        new ImageIcon(base+"/icons/Fast-backward-icon.png"));
		icons.put("sweep.back.icon",         new ImageIcon(base+"/icons/Skip-backward-icon.png"));
		icons.put("sweep.next.icon",         new ImageIcon(base+"/icons/Skip-forward-icon.png"));
		icons.put("sweep.last.icon",         new ImageIcon(base+"/icons/Fast-forward-icon.png"));
		
		
		icons.put("refresh.icon",     new ImageIcon(base+"/icons/refresh-icon.png"));
		icons.put("info.icon",        new ImageIcon(base+"/icons/info-icon.png"));
		
		icons.put("apply.changes.icon",  new ImageIcon(base+"/icons/apply-changes-icon.png"));
		icons.put("simulate.icon",       new ImageIcon(base+"/icons/simulate-icon.png"));
	}
    
    
    
    
    
    
}
