/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Map;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ngat.phase2.IProposal;
import ngat.phase2.ISequenceComponent;
import ngat.sms.GroupItem;

/**
 * @author eng
 * 
 */
public class Phase2TreePane extends JPanel implements Phase2CacheUpdateListener {

	public static ImageIcon PROPOSAL_ICON = new ImageIcon("proposal.gif");

	public static ImageIcon GROUP_ICON = new ImageIcon("group.gif");

	private JTree tree;

	private DefaultMutableTreeNode rootNode;

	Map<Long, DefaultMutableTreeNode> proposalMap;

	Map<Long, DefaultMutableTreeNode> groupMap; // WHATS THIS FOR ???
	
	private DefaultMutableTreeNode selectedNode;

	private GroupSelection selection;
	
	 /** A popup menu for proposals.*/
    JPopupMenu proposalPopup;

    /** A popup menu for groups.*/
    JPopupMenu groupPopup;
    
	// TODO Use an enhanced treenode with built in tooltip info
	// TODO ie groupname, type, prop, user, etc
	// TODO override getToolTipText(mousevt) in Jtree.

	/**
	 * 
	 */
	public Phase2TreePane(GroupSelection selection) {
		super(true);
		
		this.selection = selection;

		setLayout(new BorderLayout());

		proposalMap = new HashMap<Long, DefaultMutableTreeNode>();
		groupMap = new HashMap<Long, DefaultMutableTreeNode>();

		rootNode = new DefaultMutableTreeNode("Phase2Cache");

		tree = new JTree(rootNode);

		JScrollPane treeView = new JScrollPane(tree);
		// we may need this later to show popups in the right place ???
		// TODO JViewport viewport = treeView.getViewport();
		
		
		// TODO tree.addMouseListener(a popuplistener); this will be node type specific
		tree.setCellRenderer(new MyRenderer());
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new MySelectionListener());
		

		add(treeView, BorderLayout.CENTER);
		
		// Detect popup requests
		MouseListener pl = new PopupDetectionListener();
	    tree.addMouseListener(pl);
	    proposalPopup = createProposalPopupMenu();
	    groupPopup = createGroupPopupMenu();
	}
	
	 private JPopupMenu createProposalPopupMenu() {
		 
	        ActionListener ml = new ProposalPopupListener();

	        JPopupMenu popup = new JPopupMenu();

	      
	        popup.add(createMenuItem("Reload",   "gifs/new-iterator.gif",    "proposal-reload", ml));
	        popup.add(createMenuItem("De-activate",   "gifs/new-iterator.gif",    "proposal-deactivate", ml));
	        return popup;
	    }
	 
	 private JPopupMenu createGroupPopupMenu() {
		 
	        ActionListener ml = new GroupPopupListener();

	        JPopupMenu popup = new JPopupMenu();
	        // these need to be context sensitive ie we build the menu once we know what the target is
	        popup.add(createMenuItem("Disable",   "gifs/new-iterator.gif",    "group-disable", ml));
	        popup.add(createMenuItem("Urgent",   "gifs/new-iterator.gif",    "group-urgent", ml));
	        return popup;
	    }
	 

	    private JMenuItem createMenuItem(String text, String iconText, String cmd, ActionListener al) {

	        JMenuItem item = new JMenuItem(text);
	        item.setIcon(new ImageIcon(iconText));
	        item.setActionCommand(cmd);
	        item.addActionListener(al);
	        return item;

	    }

	/** Notification that a new Group has been added to the cache. */
	@Override
	public void phase2GroupAdded(GroupItem group) {

		// lets see if we have a node for the group's proposal

		IProposal proposal = group.getProposal();
		if (proposal != null) {

			DefaultMutableTreeNode pnode = proposalMap.get(proposal.getID());
			if (pnode == null) {
				pnode = new DefaultMutableTreeNode(proposal);
				rootNode.add(pnode);
				proposalMap.put(proposal.getID(), pnode);
			}

			// found or created parent, add group under it.
			DefaultMutableTreeNode gnode = new DefaultMutableTreeNode(group);
			pnode.add(gnode);

			groupMap.put(group.getID(), gnode);

		}

	}

	public void addGroup(GroupItem group) {
		// TODO WHY !!! see if we have a node mapped to this group or its proposal

	}

	private class MySelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

			if (node == null)
				return;

			Object uo = node.getUserObject();

			System.err.println("You have selected and will display:" + uo.getClass().getName());
			
			if (uo instanceof GroupItem) {
				GroupItem group = (GroupItem)uo;
				ISequenceComponent root = group.getSequence();				
				//System.err.println(DisplaySeq.display(0, root));
				
				// TODO display this group in the group info and sequence panels
				// TODO and maybe mark it to follow in p2 and sched perspectives ?
				selection.selectGroup(group);
			}
			

		}

	}

	/** Render the nodes. */
	private class MyRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object uo = node.getUserObject();

			if (uo instanceof IProposal) {
				IProposal proposal = (IProposal) uo;
				setText("[" + proposal.getName() + "]");
				setIcon(PROPOSAL_ICON);
			} else if (uo instanceof GroupItem) {
				GroupItem group = (GroupItem) uo;
				setText("[" + group.getID() + "/" + group.getName());
				setIcon(GROUP_ICON);

			}
			return this;
		}

	}

	 /** Detects popup and multi-mouse-click triggers.*/
    private class PopupDetectionListener extends MouseAdapter {

        @Override
		public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
		public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
            
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {

                int      row  = tree.getRowForLocation(e.getX(), e.getY());
                TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

                System.err.println("We are at: X="+e.getX()+", Y="+e.getY()+" Component="+node);

                tree.setSelectionPath(path);

                selectedNode = node;

                if (selectedNode == null)
                    return;

                Object uo = node.getUserObject();

                if (uo == null)                   
                    return;
            
              if (uo instanceof GroupItem) {                
                  groupPopup.show(e.getComponent(),
                                  e.getX(), e.getY());
              } else if
                  (uo instanceof IProposal) {
                 proposalPopup.show(e.getComponent(),
                               e.getX(), e.getY());
             } 

            } 
        }

    }

    /** ActionListener for Generic Popup Menu.*/
    private class GroupPopupListener implements ActionListener {

        @Override
		public void actionPerformed(ActionEvent ae) {

            String cmd = ae.getActionCommand();
            
            Object uo = selectedNode.getUserObject();


            if (cmd.equals("group-deactivate")) {
            	// de-activate
            } else if
            (cmd.equals("group-urgent")) {
            	// make urgent
            }
           // ETC

        }

    }

    /** ActionListener for Generic Popup Menu.*/
    private class ProposalPopupListener implements ActionListener {

        @Override
		public void actionPerformed(ActionEvent ae) {

            String cmd = ae.getActionCommand();
            
            Object uo = selectedNode.getUserObject();

           if (cmd.equals("proposal-reload")) {
        	   // reload the proposal
        	   actionProposalReload((IProposal)uo);
           }
           // ETC
      
        }

    }
    
	private void actionProposalReload(IProposal proposal) {
		ProposalReloadAction pla = new ProposalReloadAction(proposal);
		(new Thread(pla)).start();
	}
	
	
}
