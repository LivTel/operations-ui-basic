/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ngat.rcs.tms.TaskDescriptor;

/** A JTree to display task status information.
 * @author eng
 *
 */
public class TaskDisplayPanel extends JPanel {

	private JTree tree;

	private DefaultMutableTreeNode root;
	
	private DefaultTreeModel model;
	
	/**
	 * @param tree
	 */
	public TaskDisplayPanel() {
		super(true);
		
		TaskNodeDescriptor rootTask = new TaskNodeDescriptor(new TaskDescriptor("OPSMGR", "OPS_MGR"), "RUNNING");
		
		// root node is just textual
		root = new DefaultMutableTreeNode(rootTask);
		
		root.add(new DefaultMutableTreeNode(new TaskNodeDescriptor(new TaskDescriptor("SOCA", "Scheduled"), "IDLE")));
		root.add(new DefaultMutableTreeNode(new TaskNodeDescriptor(new TaskDescriptor("TOCA", "Target of Opp"), "IDLE")));
		root.add(new DefaultMutableTreeNode(new TaskNodeDescriptor(new TaskDescriptor("BGCA", "Background"), "IDLE")));
		root.add(new DefaultMutableTreeNode(new TaskNodeDescriptor(new TaskDescriptor("CAL",  "Calibration"),  "IDLE")));
		
		model = new DefaultTreeModel(root);
		
		// create a tree and set it up with model
		tree = new JTree(model);
		
		TaskDisplayRender render = new TaskDisplayRender();
		tree.setCellRenderer(render);
				
		// pop it into a vertical scrolling JSP
		JScrollPane jsp = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setLayout(new BorderLayout());
		add(jsp);
	}
	
	public TaskNodeDescriptor getRoot() {
		return (TaskNodeDescriptor)root.getUserObject();
	}
	
	public void clear() {
		// clear all the tree nodes below the root node...
		root.removeAllChildren();
	}
	
	public void clear(TaskDescriptor task) {
		// clear all children of this task
		DefaultMutableTreeNode taskNode= findNode(task.getName());
		taskNode.removeAllChildren();
	
	}
	
	public void addTaskNode(TaskDescriptor mgr, TaskDescriptor task) {
		System.err.println("TDP::Request to add node to mgr: "+mgr.getName()+", task: "+task.getName());
		
		String mgrName = mgr.getName();
	
		DefaultMutableTreeNode mgrNode = findNode(mgrName);
		if (mgrNode != null) {
			System.err.println("TDP::Found mgr node: "+mgrNode);
			// create a new child node in IDLE state
			TaskNodeDescriptor taskNodeDesc = new TaskNodeDescriptor(task, "IDLE");
			DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(taskNodeDesc);
			mgrNode.add(taskNode);
			model.reload(mgrNode);
		}
		
	}
	
	/** Update the state of the specified node.*/
	public void updateNode(TaskDescriptor task, String state) {
		DefaultMutableTreeNode taskNode = findNode(task.getName());
		if (taskNode != null) {
			System.err.println("TDP::Update node: "+taskNode);
			TaskNodeDescriptor taskNodeDesc = (TaskNodeDescriptor)taskNode.getUserObject();
			taskNodeDesc.state = state;
		}
	}
	
	/** Find the manager of the node with the specified name or null.
	 * @param name The name of the node whose manager is to be found.
	 * @return The node with this name or null if not found.
	 */
	public DefaultMutableTreeNode findManagerNode(String name) {
		
		Enumeration e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			TaskNodeDescriptor taskNodeDesc = (TaskNodeDescriptor)child.getUserObject();
			
			// weve found the bounder !
			if (taskNodeDesc.task.getName().equals(name)) {
				if (child.getParent() != null)
				return (DefaultMutableTreeNode)child.getParent();		
			}
		}
		
		return null;
	}
	
	/** Find the node with the specified name or null.
	 * @param name The name of the node to find.
	 * @return The node with this name or null if not found.
	 */
	private DefaultMutableTreeNode findNode(String name) {
		
		Enumeration e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			TaskNodeDescriptor taskNodeDesc = (TaskNodeDescriptor)child.getUserObject();
			// weve found the bounder !
			if (taskNodeDesc.task.getName().equals(name))
				return child;		
		}
		
		return null;
	}
	
}
