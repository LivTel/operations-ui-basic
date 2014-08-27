/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ngat.astrometry.AstroCatalog;

/**
 * Component to allow catalog display parameters to be edited.
 * 
 * @author eng
 * 
 */
public class CatalogDisplayEditorPanel extends JPanel {

	

	JDialog dlg;

	private TrackingPerspective trackingPerspective;

	private AstroCatalog catalog;
	
	private CatalogTableModel ctm;
	
	private JTable catalogTable;

	private JCheckBox symbolCheck;

	private JCheckBox labelCheck;

	private JCheckBox showAllCheck;

	private JTextField catNameField;

	private JSpinner countSpinner;

	private int useSymbol;
	// EDIT MODE - Fill with display info
	private Color useColor;

	private boolean useShowSymbol;

	private boolean useShowLabel;

	private String useCatName;

	private boolean edit;

	/**
	 * @param aitoff
	 * @param catalog
	 * @param cdd
	 */
	public CatalogDisplayEditorPanel(JDialog dlg, TrackingPerspective trackingPerspective, AstroCatalog catalog) {
		super();
		this.dlg = dlg;
		this.trackingPerspective = trackingPerspective;
		this.catalog = catalog;

		createPanel();

	}

	private void createPanel() {

		setLayout(new BorderLayout());

		// left panel - target table, buttons
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

		 ctm = new CatalogTableModel();
		//ctm.setRows(catalog);

		CatalogTableRenderer renderer = new CatalogTableRenderer();
		catalogTable = new CatalogTable(ctm);

		int nc = catalogTable.getColumnModel().getColumnCount();
		for (int ic = 0; ic < nc; ic++) {
			catalogTable.getColumnModel().getColumn(ic).setCellRenderer(renderer);
		}

		JScrollPane scroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(catalogTable);
		left.add(scroll);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		ActionListener cbl = new CommandButtonListener();

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(cbl);
		saveButton.setActionCommand("save");
		buttonPanel.add(saveButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(cbl);
		cancelButton.setActionCommand("cancel");

		buttonPanel.add(cancelButton);

		left.add(buttonPanel);

		add(left, BorderLayout.CENTER);

		// right panel - symbols, colors, booleans
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		catNameField = new JTextField(12);
		right.add(catNameField);

		// symbols
		JPanel symbolPanel = new JPanel();
		symbolPanel.setLayout(new GridLayout(2, 4));
		symbolPanel.add(createSymbolButton("O", 1));
		symbolPanel.add(createSymbolButton("F", 2));
		symbolPanel.add(createSymbolButton("R", 3));
		symbolPanel.add(createSymbolButton("S", 4));	// System.err.println("ragridat:" + ra + "," + dec);
		symbolPanel.add(createSymbolButton("D", 5));
		symbolPanel.add(createSymbolButton("U", 6));
		symbolPanel.add(createSymbolButton("C", 7));
		symbolPanel.add(createSymbolButton("P", 8));
		right.add(symbolPanel);

		// colors
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(2, 4));
		colorPanel.add(createColorButton(Color.cyan));
		colorPanel.add(createColorButton(Color.green));
		colorPanel.add(createColorButton(Color.blue.brighter()));
		colorPanel.add(createColorButton(Color.magenta.brighter()));
		colorPanel.add(createColorButton(Color.yellow));
		colorPanel.add(createColorButton(Color.orange));
		colorPanel.add(createColorButton(Color.red.brighter()));

		right.add(colorPanel);

		// booleans settings
		symbolCheck = new JCheckBox("Show symbol");
		right.add(symbolCheck);

		labelCheck = new JCheckBox("Show label");
		right.add(labelCheck);

		showAllCheck = new JCheckBox("Show ALL points");
		showAllCheck.addChangeListener(new ShowAllListener());
		right.add(showAllCheck);

		countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		right.add(countSpinner);

		add(right, BorderLayout.EAST);
		
	}

	public void createCatalogDisplay() {
		edit = false;
		ctm.setRows(catalog);	// System.err.println("ragridat:" + ra + "," + dec);
		// nothing to setup
	}

	public void editCatalogDisplay(CatalogDisplayDescriptor cdd) {
		edit = true;

		catNameField.setText(catalog.getCatalogName());
		catNameField.setEditable(false);
		useColor = cdd.getColor();
		useSymbol = cdd.getSymbol();
		symbolCheck.setSelected(cdd.isShowSymbol());
		labelCheck.setSelected(cdd.isShowLabel());

		if (cdd.isShowAll()) {
			showAllCheck.setSelected(true);
			countSpinner.getModel().setValue(1);
			countSpinner.setEnabled(false);
		} else {
			showAllCheck.setSelected(false);
			countSpinner.getModel().setValue(cdd.getShowCount());
			countSpinner.setEnabled(true);
		}

	}

	private JButton createColorButton(Color color) {

		ColorAction sba = new ColorAction(color);
		JButton b = new JButton(sba);

		return b;

	}

	public class ColorAction extends AbstractAction {

		private Color color;

		/**
		 * @param symbol
		 */
		public ColorAction(Color color) {
			super(null, makeIcon(color));
			this.color = color;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			useColor = color;
		}

	}

	private Icon makeIcon(Color color) {

		final Color fcolor = color;

		Icon icon = new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(fcolor);
				g.fillRect(2, 2, 12, 12);
			}

			@Override
			public int getIconWidth() {
				return 16;
			}

			@Override
			public int getIconHeight() {
				return 16;
			}
		};

		return icon;

	}

	private JButton createSymbolButton(String label, int symbol) {

		SymbolAction sba = new SymbolAction(label, symbol);
		JButton b = new JButton(sba);

		return b;

	}

	public class SymbolAction extends AbstractAction {

		private int symbol;

		/**
		 * @param symbol
		 */
		public SymbolAction(String label, int symbol) {
			super(label);
			this.symbol = symbol;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			useSymbol = symbol;
		}

	}

	/**
	 * @author eng
	 * 
	 */
	public class CommandButtonListener implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent ae) {
			String cmd = ae.getActionCommand();
			if (cmd.equals("save")) {

				useCatName = catNameField.getText().trim();
				CatalogDisplayDescriptor cdd = new CatalogDisplayDescriptor(useCatName);
				cdd.setName(useCatName);
				cdd.setColor(useColor);
				cdd.setSymbol(useSymbol);
				cdd.setShowLabel(labelCheck.isSelected());
				cdd.setShowSymbol(symbolCheck.isSelected());
				cdd.setShowAll(showAllCheck.isSelected());
				
				if (!cdd.isShowAll()) {
					int showCount = (Integer) countSpinner.getValue();
					cdd.setShowCount(showCount);
				}
				
				CatalogDisplay display = new CatalogDisplay(catalog, cdd);
				
				if (edit) {
					
					try {
						trackingPerspective.updateCatalogDisplay(display);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					catalog.setCatalogName(useCatName);
					

					try {
						trackingPerspective.addCatalogDisplay(display);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				dlg.dispose();

			} else if (cmd.equals("cancel")) {
			}
		}

	}
	public class ShowAllListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (showAllCheck.isSelected())
				countSpinner.setEnabled(false);
			else
				countSpinner.setEnabled(true);
		}

	}
	
	

}
