/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class TaskLifecycleWaterfallPanel extends JPanel implements TaskLifecycleListener, ServiceAvailabilityListener {

	
	
	/**
	 * @author eng
	 * 
	 */
    public class PopupDisplay {
	
	JTextField nameField;
	JTextField uidField;
	JTextField typeField;    
	JTextField mgrNameField;
	JTextField createField;
	JTextField initField;
	JTextField startField;
	JTextField endField;
	JTextField cancelField;
	JTextField errorField;
	
	
	private PopupDisplay() {
	    JFrame f = new JFrame("Detail");
	    nameField = createField();
	    uidField = createField();
	    typeField  = createField();
	    mgrNameField = createField();
	    createField = createField();
	    initField = createField();
	    startField = createField();
	    endField = createField();
	    cancelField = createField();
	    errorField = createField();
	    
	    JPanel panel = new JPanel(true);
	    panel.setLayout(new GridLayout(10,2));
	    panel.add(new JLabel("Name"));
	    panel.add(nameField);
	    panel.add(new JLabel("UID"));
	    panel.add(uidField);
	    panel.add(new JLabel("Type"));
	    panel.add(typeField );
	    panel.add(new JLabel("Mgr"));
	    panel.add(mgrNameField);
	    panel.add(new JLabel("Created"));
	    panel.add(createField);
	    panel.add(new JLabel("Initialized"));
	    panel.add(initField);
	    panel.add(new JLabel("Started"));
	    panel.add(startField);
	    panel.add(new JLabel("Ended"));
	    panel.add(endField);
	    panel.add(new JLabel("Cancelled"));
	    panel.add(cancelField);
	    panel.add(new JLabel("Status"));
	    panel.add(errorField);
	    
	    f.getContentPane().add(panel);
	    f.pack();
	    f.setVisible(true);
	}

	private JTextField createField() {
	    
	    JTextField f = new JTextField();
	    f.setPreferredSize(TFSIZE);
	    f.setBackground(Color.gray.brighter());
	    f.setForeground(Color.blue);
	    return f;
	}
	
	private void update(TaskLifecycle tlc) {
	    
	    TaskDescriptor t = tlc.task;
	    TaskDescriptor m = tlc.mgr;
	    
	    if (t != null) {
		nameField.setText(t.getName());
		uidField.setText(String.format("%6d",t.getUid()));
		typeField.setText(t.getTypeName());
	    }
	    
	    if (m != null) {
		mgrNameField.setText(m.getName());
	    }
	    
	    if (tlc.created) 
		createField.setText(String.format("%tT", tlc.createTime));
	    else
		createField.setText("");
	    
	    if (tlc.initialized) 
		initField.setText(String.format("%tT", tlc.initTime));
	    else
		initField.setText("");
	    
	    if (tlc.started)
		startField.setText(String.format("%tT", tlc.startTime));
	    else
		startField.setText("");
	    
	    if (tlc.ended)
		endField.setText(String.format("%tT", tlc.endTime));
	    else
		endField.setText("");
	    
	    if (tlc.cancelled)
		cancelField.setText("Cancelled");
	    else
		cancelField.setText("");
	    
	    if (tlc.error) {
		errorField.setText("Error");
	    } else {
		if (tlc.ended)
		    errorField.setText("Okay");
		else if
		    (tlc.started)
		    errorField.setText("Running...");
		else if
		    (tlc.initialized)
		    errorField.setText("Initialized");
		else
		    errorField.setText("Waiting initialization...");
	    }
	}

    }
    public void createPopup() {
	popup = new PopupDisplay();
    }
    

    public static final Dimension TFSIZE = new Dimension(250,20);
    
	public static final int TOP_LEVEL = 0;
	public static final int MODAL_LEVEL = 1;
	
	public static final Dimension SIZE = new Dimension(800, 500);

	public static final Color MGR_COLOR = Color.blue;
	public static final Color EXEC_COLOR = Color.red;
	public static final Color CREATE_COLOR = Color.gray;
	public static final Color INIT_COLOR = Color.orange;
	public static final Color RUN_COLOR = Color.cyan;
	public static final Color END_COLOR = Color.green;

	public static final Color BGCA_COLOR = Color.cyan.darker();
	public static final Color SOCA_COLOR = Color.yellow.darker();
	public static final Color TOCA_COLOR = Color.pink;
	public static final Color CAL_COLOR = Color.pink.darker();
	
	public static final Font LABEL_FONT = new Font("serif", Font.ITALIC, 7);
	public static final Font INFO_FONT = new Font("serif", Font.ITALIC, 18);

	private Map<Long, LevelDescriptor> levelMap;
	private Map<Long, TaskLifecycle> taskMap;

	private LevelDescriptor[] levels;

	private TaskDescriptor ops;
	
	private long baselookBackTime;
	private long lookBackTime;

	private double lbScaleFactor;

	private PopupDisplay popup;

	/**
	 * @param popup
	 *            the popup to set
	 */
	public void setPopup(PopupDisplay popup) {
		this.popup = popup;
	}

	/**
	 * @return the lbScaleFactor
	 */
	public double getLbScaleFactor() {
		return lbScaleFactor;
	}

	/**
	 * @param lbScaleFactor
	 *            the lbScaleFactor to set
	 */
	public void setLbScaleFactor(double lbScaleFactor) {
		this.lbScaleFactor = lbScaleFactor;
	}

	private String info;

	/**
	 * 
	 */
	public TaskLifecycleWaterfallPanel(long lookBackTime) {
		super(true);
		this.baselookBackTime = lookBackTime;
		setPreferredSize(SIZE);
		taskMap = new HashMap<Long, TaskLifecycleWaterfallPanel.TaskLifecycle>();
		levelMap = new HashMap<Long, LevelDescriptor>();
		levels = new LevelDescriptor[100];
		for (int il = 0; il < 100; il++) {
			levels[il] = new LevelDescriptor(il);
		}
		lbScaleFactor = 1.0;
		addMouseListener(new MyMouseListener());

	}

	// @Override
	@Override
	public void taskLifecycleEventNotification(TaskLifecycleEvent event) throws RemoteException {
		System.err.println("Event: " + event.getClass().getName() + " " + event);
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
			if (taskMap.get(task.getUid()) != null) {
				System.err.println("ERROR: Duplicate create key: "+task.getUid());
				return;
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
				if (taskMap.get(task.getUid()) != null) {
					System.err.println("ERROR: Duplicate create key: "+task.getUid());
					return;
				}
				LevelDescriptor nld = levels[MODAL_LEVEL];
				// add a new task into the list
				TaskLifecycle tlc = new TaskLifecycle(task, time);
				tlc.mgr = ops;
				tlc.created = true;
				nld.add(tlc);
				taskMap.put(task.getUid(), tlc);
				tlc.initTime = time;
				tlc.initialized = true;
				
			} else {
				TaskLifecycle current = taskMap.get(task.getUid());
				current.initTime = time;
				current.initialized = true;
			}
		} else if (event instanceof TaskStartedEvent) {		
			TaskLifecycle current = taskMap.get(task.getUid());
			current.startTime = time;
			current.started = true;
		} else if (event instanceof TaskCompletedEvent) {		
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.ended = true;
		} else if (event instanceof TaskFailedEvent) {			
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.error = true;
			current.ended = true;
			// ((TaskFailedEvent) event).getError()
		} else if (event instanceof TaskAbortedEvent) {
			TaskLifecycle current = taskMap.get(task.getUid());
			current.endTime = time;
			current.error = true;
			current.ended = true;
			// ((TaskAbortedEvent) event).getError()
		} else if (event instanceof TaskCancelledEvent) {
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
		repaint();
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
	
	

	// @Override
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.setFont(LABEL_FONT);

		int ww = getSize().width;
		int hh = getSize().height;

		g.setColor(getBackground());
		g.fillRect(0, 0, ww, hh);

		lookBackTime = (long) (baselookBackTime * lbScaleFactor);

		long dt = (long) (1.25 * lookBackTime);
		long now = System.currentTimeMillis();
		long te = now + lookBackTime / 4;
		long ts = now - lookBackTime;

		// loop over list of levels
		for (int il = 0; il < 100; il++) {

			int hs = hh - 25 * il - 25;

			LevelDescriptor ld = levels[il];
			for (int it = 0; it < ld.tasks.size(); it++) {

				TaskLifecycle tl = ld.tasks.get(it);

				int ws = (int) ((double) ww * (double) (tl.createTime - ts) / dt);
				g.setColor(CREATE_COLOR);
				//g.drawString(tl.task.getName(), ws, hs - 2);
				g.drawString("("+tl.task.getUid()+")", ws, hs - 2);
				if (tl.task.isManager()) {
					g.setColor(MGR_COLOR);
					g.drawRect(ws - 3, hs + 3, 9, 9);
				} else {
					g.setColor(EXEC_COLOR);
					g.drawOval(ws - 4, hs + 4, 8, 8);
				}

				// now draw a line from the mgr to the task creation node
				TaskDescriptor mgr = tl.mgr;
				LevelDescriptor mld = findLevel(mgr);
				int ml = 0;
				if (mld != null)
					ml = mld.level;
				int hm = hh - 25 * ml - 20;
				g.setColor(CREATE_COLOR);
				g.fillOval(ws - 2, hm + 2, 4, 4);
				g.drawLine(ws, hm, ws, hs + 8);

				int dw = 0;
				Color color = null;

				if (tl.started) { // done or running
					ws = (int) ((double) ww * (double) (tl.startTime - ts) / dt);
					if (tl.ended)
						dw = (int) ((double) ww * (double) (tl.endTime - tl.startTime) / dt);
					else
						dw = (int) ((double) ww * (double) (now - tl.startTime) / dt);
					if (isModal(tl.task)) 
						g.setColor(selectModalColor(tl.task));
					 else 
					g.setColor(RUN_COLOR);
					g.fillRect(ws, hs, dw, 15);
				}

				if (tl.initialized) { // running or initializing
					ws = (int) ((double) ww * (double) (tl.initTime - ts) / dt);
					if (tl.started)
						dw = (int) ((double) ww * (double) (tl.startTime - tl.initTime) / dt);
					else
						dw = (int) ((double) ww * (double) (now - tl.initTime) / dt);

					if (isModal(tl.task)) 
						g.setColor(selectModalColor(tl.task));
					 else 
						g.setColor(INIT_COLOR);
					g.fillRect(ws, hs, dw, 15);
				}

				if (tl.created) { // initializing or idle
					ws = (int) ((double) ww * (double) (tl.createTime - ts) / dt);
					if (tl.initialized)
						dw = (int) ((double) ww * (double) (tl.initTime - tl.createTime) / dt);
					else
						dw = (int) ((double) ww * (double) (now - tl.createTime) / dt);
					
					if (isModal(tl.task)) 
						g.setColor(selectModalColor(tl.task));
					 else 
					g.setColor(CREATE_COLOR);
					g.drawLine(ws, hs + 7, ws + dw, hs + 7);
				}

			}

		}

		int ws = (int) ((double) ww * (double) (System.currentTimeMillis() - ts) / dt);
		g.setColor(Color.green);
		g.drawLine(ws, 0, ws, hh);

		/*
		 * if (info != null) { g.setFont(INFO_FONT); g.setColor(Color.magenta);
		 * g.drawString(info, 20, 30); }
		 */

	}

	private Color selectModalColor(TaskDescriptor task) {
		if (task.getName().equals("SOCA"))
			return SOCA_COLOR;
		else if
		(task.getName().equals("BGCA"))
			return BGCA_COLOR;
		else if
		(task.getName().equals("TOCA"))
			return TOCA_COLOR;
		else if
		(task.getName().equals("CAL"))
			return CAL_COLOR;
		return RUN_COLOR;
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

	private class MyMouseListener extends MouseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.Mousevent
		 * )
		 */
		// @Override
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			// System.err.println("Mouse at: ["+e.getX()+","+e.getY());

			long lbt = lookBackTime;
			int x = e.getX();
			int y = e.getY();

			int h = getSize().height;
			int w = getSize().width;

			long start = System.currentTimeMillis() - lbt;

			long tt = start + (long) (x * 1.25 * lbt / w);

			int ll = (h - y + 25) / 25 - 1;

			TaskLifecycle mtl = null;
			if (ll >= 0 && ll < 100) {
				LevelDescriptor ld = levels[ll];
				for (int it = 0; it < ld.tasks.size(); it++) {

					TaskLifecycle tl = ld.tasks.get(it);

					if (tl.ended && (tl.createTime < tt && tl.endTime >= tt) || (tl.createTime < tt)) {
						mtl = tl;
					}

				}
			}

			//System.err.printf("Mouse at Time: %tT level: %4d Task: %s \n", tt, ll, (mtl != null ? mtl : "none"));
			info = String.format("%tT %4d %s", tt, ll, (mtl != null ? mtl : "none"));

			if (popup != null) {
				popup.update(mtl);
			}
		}

	}
	
}
