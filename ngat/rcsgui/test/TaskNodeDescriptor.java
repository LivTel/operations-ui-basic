/**
 * 
 */
package ngat.rcsgui.test;

import ngat.rcs.tms.TaskDescriptor;

/** Describes a task-node.
 * @author eng
 *
 */
public class TaskNodeDescriptor {

	public TaskDescriptor task;
	
	public String state;

	/**
	 * @param task
	 * @param state
	 */
	public TaskNodeDescriptor(TaskDescriptor task, String state) {
		super();
		this.task = task;
		this.state = state;
	}
	
	@Override
	public String toString() {
			return task.getTypeName()+" : "+state;
	}
	
}
