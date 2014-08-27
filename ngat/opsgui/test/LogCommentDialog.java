package ngat.opsgui.test;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.*;
import java.io.*;

import ngat.util.*;
import ngat.util.logging.*;
import ngat.util.charting.*;
import ngat.astrometry.*;
import ngat.swing.*;
import ngat.phase2.*;
import ngat.message.GUI_RCS.*;

/** Collects engineer comments to add to RCS logs.*/
public class LogCommentDialog extends JDialog {

   // RcsGUI gui;

    /** Layout manager.*/
    GridBagLayout grid;

    /** Layout constraints.*/
    GridBagConstraints gc;

    JRadioButton taskLogBtn;
    JRadioButton opsLogBtn;
    JRadioButton errorLogBtn;
    JRadioButton pcaLogBtn;
    JRadioButton ctrlLogBtn;

    JComboBox categoryBox;
    JSpinner levelBox;
    JTextArea commentField;
    JComboBox idBox;
    
    String logName;

    JButton sendBtn;
    JButton clearBtn;

    /** Create a LogCommentDialog.*/
    public LogCommentDialog() {
    	super();
	//super(gui.getFrame());

	//this.gui = gui;

	setTitle("Engineer Log Comments");
	
	grid  = new GridBagLayout();
	gc    = new GridBagConstraints();
	gc.ipadx  = 10;
	gc.insets = new Insets(0,4,0,4);
	gc.anchor = GridBagConstraints.WEST;
	gc.fill   = GridBagConstraints.HORIZONTAL;

	//RcsGUI.UTC for time.

	JPanel panel = createPanel();

	getContentPane().add(panel);
	pack();	

    }


    /** Creates the panel.*/
    private JPanel createPanel() {

	JPanel panel = new JPanel(true);
	panel.setLayout(grid);

	// Log Name.

	LogNameListener ll = new LogNameListener();

	JPanel namePanel = new JPanel(true);
	namePanel.setBorder(BorderFactory.createTitledBorder("Select Logger"));
	namePanel.setLayout(grid);

	ButtonGroup nameGrp = new ButtonGroup();
	
	taskLogBtn = new JRadioButton("TASK");
	taskLogBtn.addActionListener(ll);
	nameGrp.add(taskLogBtn);	
	makeComp(namePanel, grid, gc, taskLogBtn, 0,0,1,1);
	
	opsLogBtn  = new JRadioButton("OPERATIONS");
	opsLogBtn.addActionListener(ll);
	nameGrp.add(opsLogBtn);
	makeComp(namePanel, grid, gc, opsLogBtn, 0,1,1,1);
	
	errorLogBtn  = new JRadioButton("ERROR");
	errorLogBtn.addActionListener(ll);
	nameGrp.add(errorLogBtn);
	makeComp(namePanel, grid, gc, errorLogBtn, 0,2,1,1);
	
	pcaLogBtn  = new JRadioButton("PLANETARIUM");
	pcaLogBtn.addActionListener(ll);
	nameGrp.add(pcaLogBtn);
	makeComp(namePanel, grid, gc, pcaLogBtn, 0,3,1,1);
	
	ctrlLogBtn  = new JRadioButton("CTRL");
	ctrlLogBtn.addActionListener(ll);
	nameGrp.add(ctrlLogBtn);
	makeComp(namePanel, grid, gc, ctrlLogBtn, 0,4,1,1);

	makeComp(panel, grid, gc, namePanel, 0,0,2,5);

	// Category.

	makeComp(panel, grid, gc, new JLabel("Category"), 2,0,1,1);

	categoryBox = new JComboBox();
	makeComp(panel, grid, gc, categoryBox, 3,0,1,1);
	categoryBox.addItem("INFO");
	categoryBox.addItem("ERROR");
	categoryBox.addItem("WARNING");
	categoryBox.addItem("FATAL");
	categoryBox.addItem("BIZARRE");

	// Level.
	
	makeComp(panel, grid, gc, new JLabel("Level"), 2,1,1,1);

	levelBox = new JSpinner(new SpinnerNumberModel(1,1,10,1));
	makeComp(panel, grid, gc, levelBox, 3,1,1,1);

	// Buttons.

	SendListener sl = new SendListener();

	sendBtn = new JButton("SEND");
	sendBtn.setBackground(Color.orange);
	sendBtn.setForeground(Color.blue);
	sendBtn.addActionListener(sl);
	makeComp(panel, grid, gc, sendBtn, 4,0,1,1);

	ClearListener cl = new ClearListener();

	clearBtn = new JButton("CLEAR");
	clearBtn.setBackground(Color.cyan);
	clearBtn.setForeground(Color.red.darker());
	clearBtn.addActionListener(cl);
	makeComp(panel, grid, gc, clearBtn, 4,1,1,1);

	// Id.
	
	makeComp(panel, grid, gc, new JLabel("Engineer"), 2,2,1,1);
	idBox = new JComboBox();
	idBox.addItem("cjm");
	idBox.addItem("dxc");
	idBox.addItem("ias");
	idBox.addItem("jmm");
	idBox.addItem("mdt");
	idBox.addItem("rjs");
	idBox.addItem("snf");

	makeComp(panel, grid, gc, idBox, 3,2,1,1);
	

	// Comment.

	JPanel commentPanel = new JPanel(true);
	commentPanel.setBorder(BorderFactory.createLoweredBevelBorder());
	commentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	commentField = new JTextArea(5, 25);
	commentField.setBackground(new Color(102,204,255));
	commentField.setForeground(Color.blue);
	commentField.setWrapStyleWord(true);
	commentField.setLineWrap(true);
	JScrollPane pane = new JScrollPane(commentField,
					   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	commentPanel.add(pane);
	
	makeComp(panel, grid, gc, commentPanel, 2,3,3,2);
	
	return panel;

    }

    /** Make a GridBag component.*/
    private void makeComp(JPanel             panel,  
			  GridBagLayout      grid, 
			  GridBagConstraints c, 
			  Component          comp, 
			  int x, int y, int w, int h) {
	c.gridx = x;
	c.gridy = y;
	c.gridwidth = w;
	c.gridheight = h;
	//c.anchor = GridBagConstraints.WEST;
	grid.setConstraints(comp, c);
	panel.add(comp);
    }


    private class SendListener implements ActionListener {

	public void actionPerformed(ActionEvent ae) {

	    String cmd = ae.getActionCommand();

	    // Collect and send.

	    System.err.println("Sending: Level="+levelBox.getValue()+", Cat="+categoryBox.getSelectedItem()+" ["+commentField.getText()+"]");
	    int reply = JOptionPane.showConfirmDialog(LogCommentDialog.this,
						      "<html> Sending Comment:"+
						      "<p><table border = 1>"+
						      "<tr><td>Time: </td><td>"+new Date()+"</td></tr>"+
						      "<tr><td>Engineer:  </td><td>"+idBox.getSelectedItem()+"</td></tr>"+
						      "<tr><td>Logger  </td><td>"+logName+"</td></tr>"+
						      "<tr><td>Category:  </td><td>"+categoryBox.getSelectedItem()+"</td></tr>"+
						      "<tr><td>Level:  </td><td>"+levelBox.getValue()+"</td></tr>"+
						      "<tr><td colspan = 2>"+commentField.getText()+"</td></tr>",
						      "Confirm Log Comment",
						      JOptionPane.OK_CANCEL_OPTION);
	    
	    if (reply == JOptionPane.OK_OPTION) {
		
		String cat   = (String)categoryBox.getSelectedItem();
		String eng   = (String)idBox.getSelectedItem();
		int    level = ((Integer)levelBox.getValue()).intValue();
		
		SEND_LOG slog = new SEND_LOG("gui");
		slog.setCategory(cat);
		slog.setLogName(logName);
		slog.setClazz("GUI");
		slog.setName("test");
		slog.setLevel(level);
		slog.setMessage(commentField.getText());

		//final GUIClient client = new SendLogClient(gui, slog);
		
		//(new Thread(client)).start();
		
	    }
	    
	}
    }

    private class LogNameListener  implements ActionListener {
	
	public void actionPerformed(ActionEvent ae) {
	      String cmd = ae.getActionCommand();

	      logName = cmd;
	}
    }

    private class ClearListener implements ActionListener {
	
	public void actionPerformed(ActionEvent ae) {
	      String cmd = ae.getActionCommand();

	      int len = (commentField.getText() != null ? commentField.getText().length() : 0);

	      commentField.replaceRange("",0, len);
	      
	      categoryBox.setSelectedItem("INFO");

	      levelBox.setValue(new Integer(1));
	      
	}
    }

public static void main(String args[]) {

	LogCommentDialog lp = new LogCommentDialog();
	
	lp.setVisible(true);
	
	
}

}
