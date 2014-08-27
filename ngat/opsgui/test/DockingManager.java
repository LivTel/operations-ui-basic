package ngat.opsgui.test;

import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DockingManager {

	public static final Dimension STANDALONE_SIZE = new Dimension(600, 500);

	private JTabbedPane tabs;
	
	/** Special behaviour performed after attachment.*/
	private Behaviour postAttachmentBehaviour;
	
	/** Special behaviour performed before detachment.*/
	private Behaviour preDetachmentBehaviour;

	/** Special behaviour performed after selection.*/
	private Behaviour postSelectionBehaviour;
	
	
	public DockingManager(JTabbedPane tabs) {
		this.tabs = tabs;
	}

	
	
	/**
	 * @param postAttachmentBehaviour the postAttachmentBehaviour to set
	 */
	public void setPostAttachmentBehaviour(Behaviour postAttachmentBehaviour) {
		this.postAttachmentBehaviour = postAttachmentBehaviour;
	}



	/**
	 * @param preDetachmentBehaviour the preDetachmentBehaviour to set
	 */
	public void setPreDetachmentBehaviour(Behaviour preDetachmentBehaviour) {
		this.preDetachmentBehaviour = preDetachmentBehaviour;
	}



	/**
	 * @param postSelectionBehaviour the postSelectionBehaviour to set
	 */
	public void setPostSelectionBehaviour(Behaviour postSelectionBehaviour) {
		this.postSelectionBehaviour = postSelectionBehaviour;
	}



	/** Pass the selection on to the managed tab-pane. */
	public void selectComponent(ComponentDescriptor cd) {
		tabs.setSelectedComponent(cd.getComponent());
		// TODO selection behaviour
		if (postSelectionBehaviour != null)
			postSelectionBehaviour.perform();
	}

	/** Attach a component to the manager tab-pane. */
	public void attach(ComponentDescriptor cd) {

		tabs.addTab(cd.getTagText(), cd.getComponent());
		int newTabIndex = tabs.getTabCount();
		tabs.setTabComponentAt(newTabIndex - 1, new DockingLabel(this, cd));
		
		// TODO attachment behaviour - eg adding menus to enclosing frame.
		if (postAttachmentBehaviour != null)
			postAttachmentBehaviour.perform();
		
	}

	public JFrame detach(ComponentDescriptor cd) {
		JFrame f = new JFrame(cd.getTagText());

		f.getContentPane().setLayout(new BorderLayout());

		Dimension size = cd.getComponent().getSize();
		
		// TODO detachment behaviour - eg remove menus from enclosing frame.
		if (preDetachmentBehaviour != null)
			preDetachmentBehaviour.perform();
		
		f.getContentPane().add(cd.getComponent(), BorderLayout.CENTER);
		f.getContentPane().setPreferredSize(size);
		f.getContentPane().setMinimumSize(size);

		// set the size based on component's previous size...
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		final JFrame ff = f;
		final ComponentDescriptor fcd = cd;
		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowIconified(WindowEvent we) {
				// remove the component from the the standalone frame,
				// then attach to main frame and dispose the standalone frame.
				ff.removeAll();
				attach(fcd);
				ff.dispose();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				ff.removeAll();
				attach(fcd);
				ff.dispose();
			}

		});
		return f;
	}

}