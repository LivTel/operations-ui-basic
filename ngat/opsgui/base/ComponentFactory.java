package ngat.opsgui.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

import ngat.opsgui.components.LinePanel;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;
import ngat.opsgui.util.TimeField;

/** Factory for building standard swing components. */
/**
 * @author eng
 *
 */
public class ComponentFactory {

	public static final Dimension CHART_FULL = new Dimension(600, 500);
	public static final Dimension ICON_BUTTON_SIZE = new Dimension(40, 24);
    public static final Dimension TEXT_BUTTON_SIZE = new Dimension(60, 24);
	public static final Dimension SMALL_BUTTON_SIZE = new Dimension(16, 16);
	public static final Dimension HUGE_LABEL_SIZE  = new Dimension(120, 16);
	public static final Dimension LARGE_LABEL_SIZE = new Dimension(60, 16);
	public static final Dimension SMALL_LABEL_SIZE = new Dimension(24, 16);
	
	public static LinePanel makeLinePanel() {
		return new LinePanel();
	}

    public static LinePanel makeLinePanel(int width) {
	return new LinePanel(width);
    }

	
	public static JButton makeRedirectButton() {
		// TODO add action command here
		JButton button = new JButton(Resources.getIcon("summary.expand.icon"));
		button.setBackground(Resources.getColor(""));

		button.setRolloverIcon(Resources.getIcon("redirect.button.rollover.icon"));
		button.setBorderPainted(false);
		button.setPreferredSize(SMALL_BUTTON_SIZE);
		button.setRolloverEnabled(true);
		button.setSelectedIcon(Resources.getIcon("redirect.expand.selected.icon"));
		button.setRolloverIcon(Resources.getIcon("redirect.expand.rollover.icon"));
		// actionCommand should force a call to:
		// Gui.selectPerspective(perspectivePart(redirectCommand))
		// .selectSubpanel(subPanelPart(redirectCommand));

		return button;		
	}

    public static JButton makeTextButton(String text) {
	JButton button = new JButton(text);
	button.setFont(Resources.getFont("text.button.font"));
	button.setBackground(Resources.getColor(""));
	button.setBorderPainted(true);
	button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	button.setPreferredSize(TEXT_BUTTON_SIZE);


	return button;
    }
    
    public static JButton makeIconButton(Icon icon) {
    	JButton button = new JButton(icon);
		button.setBackground(Resources.getColor(""));

		//button.setRolloverIcon(Resources.getIcon("redirect.button.rollover.icon"));
		button.setBorderPainted(true);
		button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		button.setPreferredSize(ICON_BUTTON_SIZE);
		return button;
        }
    
	
	public static JButton makeSmallButton(Icon icon) {
		JButton button = new JButton(icon);
		button.setBackground(Resources.getColor(""));

		//button.setRolloverIcon(Resources.getIcon("redirect.button.rollover.icon"));
		button.setBorderPainted(false);
		button.setPreferredSize(SMALL_BUTTON_SIZE);
		//button.setRolloverEnabled(true);
		//button.setSelectedIcon(Resources.getIcon("redirect.expand.selected.icon"));
		//button.setRolloverIcon(Resources.getIcon("redirect.expand.rollover.icon"));
		// actionCommand should force a call to:
		// Gui.selectPerspective(perspectivePart(redirectCommand))
		// .selectSubpanel(subPanelPart(redirectCommand));

		return button;		
	}

    public static JButton makeSmallButton(Icon icon, String tooltiptext) {
	JButton b = makeSmallButton(icon);
	b.setToolTipText(tooltiptext);
	return b;
    }

	public static StateField makeStateField(int w, StateColorMap colorMap) {
		StateField field = new StateField(w);
		// field.setText(text);
		field.setFont(Resources.getFont("text.font"));
		field.setMap(colorMap);
		return field;

	}
	
	public static DataField makeDataField(int w, String format) {
		DataField field = new DataField(w, format);
		field.setFont(Resources.getFont("text.font"));
		return field;
	}
	
	public static TimeField makeTimeField(int w, String format) {
		TimeField field = new TimeField(w, format);
		field.setFont(Resources.getFont("text.font"));
		return field;
	}
	
	public static DataField makeIntegerDataField(int w, String format) {
		DataField field = new DataField(w, format, false);
		field.setFont(Resources.getFont("text.font"));
		return field;
	}
	
	
	public static JTextField makeEntryField(int w) {
		JTextField field = new JTextField(w);
		field.setFont(Resources.getFont("text.font"));
		return field;
	}
	
	public static JComboBox makeCombo() {
		JComboBox box = new JComboBox();
		box.setFont(Resources.getFont("text.font"));
		return box;
	}

    public static JLabel makeUnsizedDisplayLabel(String text) {
	JLabel label = new JLabel(text, SwingConstants.LEFT);
	label.setFont(Resources.getFont("display.label.font"));
	//label.setPreferredSize(LARGE_LABEL_SIZE);
	return label;
    }

	
	public static JLabel makeUnsizedLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.LEFT);		
		label.setFont(Resources.getFont("label.font"));
		//label.setPreferredSize(LARGE_LABEL_SIZE);
		return label;
	}
	
	public static JLabel makeHugeLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.LEFT);
		label.setFont(Resources.getFont("label.font"));
		label.setPreferredSize(new Dimension(HUGE_LABEL_SIZE));
		return label;
	}
	
	public static JLabel makeLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.LEFT);		
		label.setFont(Resources.getFont("label.font"));
		label.setPreferredSize(LARGE_LABEL_SIZE);
		return label;
	}
	
	public static JLabel makeSmallLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.LEFT);
		label.setFont(Resources.getFont("label.font"));
		label.setPreferredSize(new Dimension(SMALL_LABEL_SIZE));
		return label;
	}
	
	public static JLabel makeFixedWidthLabel(String text, int width) {
		JLabel label = new JLabel(text, SwingConstants.LEFT);
		label.setFont(Resources.getFont("label.font"));
		label.setPreferredSize(new Dimension(width, 16));
		return label;
	}
	
	/** Create a time-series chart.
	 * @param tsc The time series collection.
	 * @param title Chart title.
	 * @param ylabel Label for y-axis.
	 * @param lo Lo bound for range axis.
	 * @param hi Hi bound for range axis.
	 * @param dateformat
	 * @return
	 *
	 */
	public static JFreeChart makeTimeChart(TimeSeriesCollection tsc, String title, String ylabel, double lo, double hi, SimpleDateFormat dateformat) {
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		Color bg = Resources.getColor("chart.background.color");
		plot.setBackgroundPaint(bg);
		plot.setDomainGridlinePaint(Color.cyan);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.cyan);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		// XYItemRenderer r = plot.getRenderer();

		chart.getTitle().setFont(Resources.getFont("chart.title.font"));

		ValueAxis axis = plot.getDomainAxis();
		// System.err.println("X axis is a " + axis.getClass().getName());
		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(dateformat);
			System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		// axis.setFixedAutoRange(2 * 3600000.0); // 12 hour
		axis.setAutoRangeMinimumSize(2 * 3600 * 1000.0); 
	
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lo, hi);

		// Legend - this is something of a fudge but seems to work, 
		// TODO there must be a better way ??	
		LegendTitle lt = new LegendTitle(plot);
		lt.setItemFont(Resources.getFont("chart.legend.font"));
		lt.setBackgroundPaint(Resources.getColor("chart.legend.background.color"));
		lt.setItemPaint(Color.blue);
		// chart.legend.foreground.color
		lt.setFrame(new BlockBorder(Color.white));
		// chart.legend.frame.color
		
		lt.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(lt);
		XYTitleAnnotation xyta = new XYTitleAnnotation(0.001, 0.999, lt, RectangleAnchor.TOP_LEFT);
		plot.addAnnotation(xyta);

		// XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02,
		// lt,RectangleAnchor.TOP_RIGHT);
		// ta.setMaxWidth(0.25);
		// plot.addAnnotation(ta);

		return chart;
		
	}
	
	
}