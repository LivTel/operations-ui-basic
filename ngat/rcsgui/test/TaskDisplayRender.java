/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author eng
 *
 */
public class TaskDisplayRender implements TreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
	
		DefaultMutableTreeNode taskNode = (DefaultMutableTreeNode)value;
		TaskNodeDescriptor taskNodeDesc = (TaskNodeDescriptor)taskNode.getUserObject();
		String typeName = taskNodeDesc.task.getTypeName();
		String state = taskNodeDesc.state;
		
		return new JLabel(typeName+" : "+state);
		
	}

	
	
}
