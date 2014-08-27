/**
 * 
 */
package ngat.opsgui.test;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.rcs.tms.TaskDescriptor;
import ngat.rcs.tms.TaskLifecycleListener;
import ngat.rcs.tms.events.TaskAbortedEvent;
import ngat.rcs.tms.events.TaskCancelledEvent;
import ngat.rcs.tms.events.TaskCompletedEvent;
import ngat.rcs.tms.events.TaskCreatedEvent;
import ngat.rcs.tms.events.TaskFailedEvent;
import ngat.rcs.tms.events.TaskInitializedEvent;
import ngat.rcs.tms.events.TaskLifecycleEvent;
import ngat.rcs.tms.events.TaskStartedEvent;

/**
 * @author eng
 * 
 */
public class TaskLifecycleWaterfallTest implements TaskLifecycleListener, ServiceAvailabilityListener {

    public static final int MODAL_LEVEL = 1;

	private Map<Long, LevelDescriptor> levelMap;
	private Map<Long, TaskLifecycle> taskMap;

	private LevelDescriptor[] levels;

	private TaskDescriptor ops;
	
	private String info;

	/**
	 * 
	 */
	public TaskLifecycleWaterfallTest() {
		taskMap = new HashMap<Long, TaskLifecycleWaterfallTest.TaskLifecycle>();
		levelMap = new HashMap<Long, LevelDescriptor>();
		levels = new LevelDescriptor[100];
		for (int il = 0; il < 100; il++) {
			levels[il] = new LevelDescriptor(il);
		}
		
	}

	// @Override
	@Override
	public void taskLifecycleEventNotification(TaskLifecycleEvent event) throws RemoteException {
		System.err.println("Event: " + event);
		long time = event.getEventTimeStamp();
		TaskDescriptor task = event.getTask();

		if (isBollocks(task))
			return;
		
		if (event instanceof TaskCreatedEvent) {

			if (isTop(task)) {
				ops = task;
			}
			
			if (isModal(task)) {
				// dont create anything yet
				return;
			}

			// find the next free level above the level of its manager
			TaskDescriptor mgr = ((TaskCreatedEvent) event).getMgr();
			int ml = 0;
			if (mgr == null) {
				ml = 0;
			} else {
				LevelDescriptor ld = findLevel(mgr);
				ml = ld.level;
			}
			int nfl = nextFreeLevel(ml, time);
			// if nfl = -1 there are no free levels....
			LevelDescriptor nld = levels[nfl];
			// add a new task into the list
			TaskLifecycle tlc = new TaskLifecycle(task, time);
			tlc.mgr = mgr;
			tlc.created = true;
			nld.add(tlc);
			taskMap.put(task.getUid(), tlc);

		} else if (event instanceof TaskInitializedEvent) {

			if (isModal(task)) {
				LevelDescriptor nld = levels[MODAL_LEVEL];
				// add a new task into the list
				TaskLifecycle tlc = new TaskLifecycle(task, time);
				tlc.mgr = ops;
				tlc.created = true;
				nld.add(tlc);
				tlc.initTime = time;
				tlc.initialized = true;
				
			} else {

				//LevelDescriptor ld = findLevel(task);
				//if (ld == null)
				//	return;
				//TaskLifecycle current = ld.current();
				TaskLifecycle current = taskMap.get(task.getUid());
				current.initTime = time;
				current.initialized = true;
			}
		} else if (event instanceof TaskStartedEvent) {
			//LevelDescriptor ld = findLevel(task);
			//if (ld == null)
			//	return;
			//TaskLifecycle current = ld.current();
			TaskLifecycle current = taskMap.get(task.getUid());
			current.startTime = time;
			current.started = true;
		} else if (event instanceof TaskCompletedEvent) {
			//LevelDescriptor ld = findLevel(task);
			//if (ld == null)
			//	return;
			//TaskLifecycle current = ld.current();
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.ended = true;
		} else if (event instanceof TaskFailedEvent) {
			//LevelDescriptor ld = findLevel(task);
			//if (ld == null)
			//	return;
			//TaskLifecycle current = ld.current();
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.error = true;
			current.ended = true;
			// ((TaskFailedEvent) event).getError()
		} else if (event instanceof TaskAbortedEvent) {
			//LevelDescriptor ld = findLevel(task);
			//if (ld == null)
			//	return;
			//TaskLifecycle current = ld.current();
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.error = true;
			current.ended = true;
			// ((TaskAbortedEvent) event).getError()
		} else if (event instanceof TaskCancelledEvent) {
			//LevelDescriptor ld = findLevel(task);
			//if (ld == null)
			//	return;
			//TaskLifecycle current = ld.current();
			TaskLifecycle current = taskMap.get(task.getUid());
			current.cancelled = true;
			current.initialized = true;
			current.initTime = current.createTime;
			current.started = true;
			current.startTime = current.createTime;
			current.endTime = time;
			current.error = true;
			current.ended = true;
			// ((TaskAbortedEvent) event).getError()
		}
		
	}

	private boolean isModal(TaskDescriptor t) {
		String name = t.getName();
		if (name.equals("SOCA") || name.equals("TOCA") || name.equals("BGCA") || name.equals("CAL"))
			return true;
		return false;
	}

	private boolean isTop(TaskDescriptor t) {
		String name = t.getName();
		if (name.equals("OCM"))
			return true;
		return false;
	}
	
	private boolean isBollocks(TaskDescriptor t) {
		String name = t.getName();
		if (name.equals("CONTROL_AGENT"))
			return true;		
		return false;
	}

	/** Find the next free level above the specified level. */
	private int nextFreeLevel(int ml, long time) {
		System.err.println("Check next free level above: " + ml);

		for (int il = ml; il < 100; il++) {
			LevelDescriptor ld = levels[il];
			System.err.println("Check level: " + il + " curr: " + ld.current());
			// nothing current at this level
			if (ld.current() == null)
				return il;
			// current task has ended
			if (ld.current().ended && (ld.current().endTime < time))
				return il;
		}
		return -1;
	}

	private LevelDescriptor findLevel(TaskDescriptor t) {
		if (t == null)
			return null;
		LevelDescriptor ld = levelMap.get(t.getUid());
		return ld;
	}
	
	
	/**
	 * Describes the content of a level. A series of task lifecycles.
	 * 
	 * @author eng
	 * 
	 */
	public class LevelDescriptor {

		public int level;

		public List<TaskLifecycle> tasks;

		/**
		 * @param latestTime
		 * @param level
		 * @param task
		 */
		public LevelDescriptor(int level) {
			super();
			this.level = level;
			tasks = new Vector<TaskLifecycle>();
		}

		public void add(TaskLifecycle tlc) {
			tasks.add(tlc);
			levelMap.put(tlc.task.getUid(), this);
			System.err.println("Level: " + level + " add: " + tlc);
		}

		public TaskLifecycle current() {
			if (tasks.isEmpty())
				return null;
			return tasks.get(tasks.size() - 1);
		}
	}

	public class TaskLifecycle {

		public TaskDescriptor task;

		public TaskDescriptor mgr;
		public long createTime;

		public long initTime;

		public long startTime;

		public long endTime;

		public boolean created;
		public boolean initialized;
		public boolean started;
		public boolean ended;
		public boolean cancelled;

		public boolean error;

		/**
		 * @param task
		 * @param createTime
		 */
		public TaskLifecycle(TaskDescriptor task, long createTime) {
			super();
			this.task = task;
			this.createTime = createTime;
		}

		@Override
		public String toString() {
			long t = System.currentTimeMillis();
			return String.format("%20s %6d %6d %6d %8s %8s \n", task.getName(), t - createTime, t - startTime, t - endTime,
					(ended?"END":"not-ended"), (cancelled?"CAN":"not-can"));
		}

	}

	// @Override
	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub

	}


}
