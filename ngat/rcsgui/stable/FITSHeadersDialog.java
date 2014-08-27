package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.event.*;
import java.awt.*;
import ngat.message.GUI_RCS.*;

/** Sets FITS headers.*/
public class FITSHeadersDialog extends JDialog {
   
    public static NumberFormat nf = NumberFormat.getInstance();

    public static final Font BIG_FONT = new Font("serif", Font.BOLD, 20);

    static final Color CTRL_COLOR = new Color(182, 182, 104);

    /** The RCS GUI.*/
    RcsGUI gui;

    /** Layout manager.*/
    GridBagLayout grid;

    /** Layout constraints.*/
    GridBagConstraints gc;

    // ### Use a special panel which holds the various subcomponents 
    // ### and gets the wee button onto it. call specpanel.getText(), inccnt() zero() etc

    // Fields.
    JTextField tagField;
    JTextField userField;
    JTextField propField;
    JTextField groupField;
    JTextField obsField;

    // Buttons.
    JButton sendBtn;
    JButton clearBtn;
    JButton autoBtn;

    // Vars.
    String tagId;
    String userId;
    String propId;
    String groupId;
    String obsId;

    /** Create a stand-alone FITSHeadersDialog.*/
    public FITSHeadersDialog(RcsGUI gui) {

	super();

	setTitle("FITS Header mode");

	this.gui = gui;

	JPanel panel = createPanel();

	getContentPane().add(panel);

	pack();

    }

    /** Create the main panel.*/
    private JPanel createPanel() {
	
	grid  = new GridBagLayout();
	gc    = new GridBagConstraints();
	gc.ipadx  = 10;
	gc.insets = new Insets(0,4,0,4);
	gc.anchor = GridBagConstraints.WEST;
	gc.fill   = GridBagConstraints.HORIZONTAL;
	
	JPanel panel = new JPanel(true);

	panel.setLayout(grid);

	tagField = new JTextField(8);
	addCompPanel("TAG ID", tagField, panel, 0,0,1,1);

	userField = new JTextField(8);
	addCompPanel("User ID", userField, panel, 0,1,1,1);

	propField = new JTextField(8);
	addCompPanel("Proposal ID", propField, panel, 0,2,1,1);

	groupField = new JTextField(8);
	addCompPanel("Group ID", groupField, panel, 0,3,1,1);

	obsField = new JTextField(8);
	addCompPanel("Obs ID", obsField, panel, 0,4,1,1);

	JPanel ctrlPanel = new JPanel(true);
	ctrlPanel.setLayout(grid);
	Border inner = BorderFactory.createEmptyBorder(20,5,20,5);
	Border outer = BorderFactory.createLoweredBevelBorder();
	ctrlPanel.setBorder(BorderFactory.createCompoundBorder(outer, inner));
	ctrlPanel.setBackground(CTRL_COLOR);

	BtnListener bl = new BtnListener();

	sendBtn= new JButton("Set Manual");
	sendBtn.setBackground(Color.red.darker());
	sendBtn.setForeground(Color.yellow);
	sendBtn.setBorder(BorderFactory.createRaisedBevelBorder());
	sendBtn.addActionListener(bl);
	sendBtn.setActionCommand("send");
	makeComp(ctrlPanel, grid, gc, sendBtn, 0,0,1,1);		

	clearBtn = new JButton("Clear");
	clearBtn.setBackground(Color.orange);
	clearBtn.setForeground(Color.blue);
	clearBtn.setBorder(BorderFactory.createRaisedBevelBorder());
	clearBtn.addActionListener(bl);
	clearBtn.setActionCommand("clear");
	makeComp(ctrlPanel, grid, gc, clearBtn, 1,0,1,1);	



	autoBtn = new JButton("Automatic");
	autoBtn.setBackground(Color.blue);
	autoBtn.setForeground(Color.orange);
	autoBtn.setBorder(BorderFactory.createRaisedBevelBorder());
	autoBtn.addActionListener(bl);
	autoBtn.setActionCommand("auto");
	makeComp(ctrlPanel, grid, gc, autoBtn, 2,0,1,1);	

	


	makeComp(panel, grid, gc, ctrlPanel, 0,5,2,2);

	return panel;

    }

    private void addCompPanel(String text, JTextField field, JPanel panel, int x, int y, int w, int h) {

	JPanel inner = new JPanel(true);
	inner.setBorder(BorderFactory.createLoweredBevelBorder());
	JLabel label = new JLabel(text);
	makeComp(inner, grid, gc, label, 0,0,1,1);
	makeComp(inner, grid, gc, field, 1,0,1,1);
	makeComp(panel, grid, gc, inner, x,y,w,h);

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
    
    /** Popup a standalone PCA Dialog.*/
    public static void main(String args[]) {
	MetalLookAndFeel.setCurrentTheme(new MyLNF());

	FITSHeadersDialog fhDlg = new FITSHeadersDialog(null);
	fhDlg.setVisible(true);

    }

    private void displayError(String message) {

	JOptionPane.showMessageDialog(gui.getFrame(), 
				      message,
				      "FITS Set Manual Headers", 
				      JOptionPane.INFORMATION_MESSAGE);
	
	
    }
    
    private class BtnListener implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent ae) {
	    
	    String cmd = ae.getActionCommand();
	    
	    if (cmd.equals("send")) {

		tagId = tagField.getText();
		if (tagId == null || tagId.equals("")) {
		    displayError("TAG field is not set");
		    return;
		}

		userId = userField.getText();
		if (userId == null || userId.equals("")) {
		    displayError("User field is not set");
		    return;
		}

		propId = propField.getText();
		if (propId == null || propId.equals("")) {
		    displayError("Proposal field is not set");
		    return;
		}

		groupId = groupField.getText();
		if (groupId == null || groupId.equals("")) {
		    displayError("Group field is not set");
		    return;
		}
		
		obsId = obsField.getText();
		if (obsId == null || obsId.equals("")) {
		    displayError("Obs field is not set");
		    return;
		}

		ISS_SET_HEADERS command = new ISS_SET_HEADERS("gui-fits");
		command.setTagId(tagId);
		command.setUserId(userId);
		command.setProposalId(propId);
		command.setGroupId(groupId);
		command.setObsId(obsId);
		command.setManual(true);
	
		final FitsSetHeadersClient client = new FitsSetHeadersClient(gui, command);
		(new Thread(client)).start();

	    } else if
		(cmd.equals("clear")) {
	       
		tagField.setText("");
		userField.setText("");
		propField.setText("");
		groupField.setText("");
		obsField.setText("");

	    } else if
		(cmd.equals("auto")) {

		tagField.setText("");
		userField.setText("");
		propField.setText("");
		groupField.setText("");
		obsField.setText("");

		ISS_SET_HEADERS command = new ISS_SET_HEADERS("gui-fits");
		command.setManual(false);

		final FitsSetHeadersClient client = new FitsSetHeadersClient(gui, command);
		(new Thread(client)).start();


	    }

	}

    }


}
