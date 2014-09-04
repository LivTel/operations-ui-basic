/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ngat.phase2.IExecutionFailureContext;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.IInstrumentConfig;
import ngat.sms.ExecutionResource;
import ngat.sms.ExecutionResourceBundle;
import ngat.sms.ExecutionResourceUsageEstimationModel;
import ngat.sms.GroupItem;
import ngat.sms.ComponentSet;
import ngat.sms.bds.TestResourceUsageEstimator;

/**
 * @author eng
 * 
 */
public class OperationsPanel extends JPanel {

	static int count = 0;

	private JTable table;

	private OpsTableModel model;

	private Map<String, OpsTableRow> groups;

	private JScrollPane scroll;

	// ngat.rcs.sciops.ExecutionTimingCalculator etc;
	private ExecutionResourceUsageEstimationModel xrm;

	/** Map instrument name to FITS file prefix. */
	public static final Map i2i = new HashMap() {
	};
	static {
		i2i.put("RATCAM", "C");
		i2i.put("FRODO_BLUE", "B");
		i2i.put("FRODO_RED", "R");
		i2i.put("RISE", "Q");
		i2i.put("RINGO3", "D");
		i2i.put("IO:O", "H");
		i2i.put("IO:THOR", "W");
	}

	public static final Object[] lv = { new Integer(55), "15:12:23",
			"JM12B123bx", "Albertina.Smetherington", "Standards",
			"Somegroup-1234-34-IO", "Completed.", "C:B:R",
			"Autoguider failed to acquire" };

	public OperationsPanel() {
		super(true);
		// setPreferredSize(new Dimension(width, height))
		setLayout(new BorderLayout());
		// etc = new ngat.rcs.sciops.ExecutionTimingCalculator();

		xrm = new TestResourceUsageEstimator();
		groups = new HashMap<String, OpsTableRow>();

		// lm = new DefaultListModel();

		model = new OpsTableModel();

		// list = new JList(lm);
		// list.setCellRenderer(new MyCellRenderer());

		TableCellRenderer mr = new OpsTableRenderer();

		table = new JTable(model);
		table.getColumnModel().getColumn(0).setCellRenderer(mr);
		table.getColumnModel().getColumn(1).setCellRenderer(mr);
		table.getColumnModel().getColumn(2).setCellRenderer(mr);
		table.getColumnModel().getColumn(3).setCellRenderer(mr);
		table.getColumnModel().getColumn(4).setCellRenderer(mr);
		table.getColumnModel().getColumn(5).setCellRenderer(mr);
		table.getColumnModel().getColumn(6).setCellRenderer(mr);
		table.getColumnModel().getColumn(7).setCellRenderer(mr);
		table.getColumnModel().getColumn(8).setCellRenderer(mr);

		// initialize column sizes

		for (int icol = 0; icol < 9; icol++) {
			TableColumn col = table.getColumnModel().getColumn(icol);

			Component comp = col.getCellRenderer()
					.getTableCellRendererComponent(table, lv[icol], false,
							false, 0, icol);

			int cellWidth = comp.getPreferredSize().width;
			col.setPreferredWidth(cellWidth);
		}

		scroll = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(500, 200));

		add(scroll, BorderLayout.CENTER);
	}

	public void update(GroupItem group) {
		if (group == null)
			return;
		++count;
		System.err.println("OPS:Update group: " + group);

		ISequenceComponent root = group.getSequence();
		long exec = 0L;
		long shutter = 0L;
		try {
			// exec = etc.calcExecTimeOfSequence((XIteratorComponent) root);
			ExecutionResourceBundle xrb = xrm.getEstimatedResourceUsage(group);
			ExecutionResource xt = xrb.getResource("TIME");
			exec = (long) xt.getResourceUsage(); // this is millis
			ExecutionResource xs = xrb.getResource("EXPOSURE");
			shutter = (long) xs.getResourceUsage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String ilist = new String("");
		try {
			int iic = 0;
			ComponentSet cs = new ComponentSet(root);
			Iterator<IInstrumentConfig> ii = cs.listInstrumentConfigs();
			while (ii.hasNext()) {
				IInstrumentConfig ic = ii.next();
				String iname = ic.getInstrumentName().toUpperCase();
				String iab = (String) i2i.get(iname);
				if (ilist.indexOf(iab) == -1) {
					// not found add to list
					if (iic == 0)
						ilist = ilist.concat(iab);
					else
						ilist = ilist.concat(":" + iab);
					iic++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ilist = "N/A";
		}

		OpsTableRow row = new OpsTableRow();
		row.group = group;
		row.hasCompleted = false;
		row.error = null;
		row.startTime = System.currentTimeMillis();
		row.exec = exec;
		row.shutter = shutter;
		// lm.addElement(g);
		row.details = ilist;

		model.addEntry(row);
		groups.put(group.getName(), row);
	}

	public void completed(GroupItem group) {
		// find the relevant element.} else if
		String gname = group.getName();
		OpsTableRow row = groups.get(gname);
		if (row == null)
			return;
		row.hasCompleted = true;
		row.completionTime = System.currentTimeMillis();
		model.fireTableDataChanged();
	}

	public void failed(GroupItem group, IExecutionFailureContext error) {
		String gname = group.getName();
		OpsTableRow row = groups.get(gname);
		if (row == null)
			return;
		row.hasCompleted = true;
		row.completionTime = System.currentTimeMillis();
		row.error = error;
		model.fireTableDataChanged();
	}

	class GroupInfo {

		public GroupItem group;

		public boolean completed;

		public IExecutionFailureContext error;

	}

}
