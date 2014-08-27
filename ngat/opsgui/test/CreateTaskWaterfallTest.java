/**
 * 
 */
package ngat.opsgui.test;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import ngat.rcs.tms.TaskDescriptor;
import ngat.rcs.tms.events.TaskCompletedEvent;
import ngat.rcs.tms.events.TaskCreatedEvent;
import ngat.rcs.tms.events.TaskInitializedEvent;
import ngat.rcs.tms.events.TaskLifecycleEvent;
import ngat.rcs.tms.events.TaskStartedEvent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;


/**
 * @author eng
 * 
 */
public class CreateTaskWaterfallTest {
	
	static final int ROOT_TASK = 1;
	static final int MODAL_TASK = 2;
        static final int TLM_TASK = 3;
	static final int MGR_TASK = 4;
	static final int EXEC_TASK = 5;
	
	TaskLifecycleWaterfallPanel twp;
	TBActionListener tbal;

	public void exec(long lookback, int nt) throws Exception {

		twp = new TaskLifecycleWaterfallPanel(lookback);
		tbal = new TBActionListener();

		JToolBar bar = createToolBar();

		JFrame f = new JFrame("Task history");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(twp, BorderLayout.CENTER);
		f.getContentPane().add(bar, BorderLayout.NORTH);
		f.pack();
		f.setVisible(true);

		// now setup some nice tasks
		TaskCycler topc = createTop(twp, nt);
		topc.start();

		while (true) {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException ix) {
			}
			twp.repaint();
		}
	}

	private JToolBar createToolBar() {
		JToolBar bar = new JToolBar("tasks");
		bar.add(createTooBarButton("Z+"));
		bar.add(createTooBarButton("z-"));
		bar.add(createTooBarButton("<<"));
		bar.add(createTooBarButton(">>"));
		bar.add(createTooBarButton(">>|"));
		bar.add(createTooBarButton("|<<"));
		bar.addSeparator();
		bar.add(createTooBarButton("@"));
		bar.add(createTooBarButton("?"));
		bar.add(createTooBarButton("A"));
		bar.add(createTooBarButton("V"));
		return bar;
	}

	private JButton createTooBarButton(String label) {
		JButton btn = new JButton(label);
		btn.setActionCommand(label);
		btn.addActionListener(tbal);
		return btn;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			int nt = Integer.parseInt(args[0]);
			long lookback = Integer.parseInt(args[1]) * 60000;

			CreateTaskWaterfallTest test = new CreateTaskWaterfallTest();
			test.exec(lookback, nt);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TaskCycler createTop(TaskLifecycleWaterfallPanel twp, int nt) throws Exception {
		TaskDescriptor top = new TaskDescriptor("OPS", "OpsManager");
		top.setIsManager(true);
		long start = System.currentTimeMillis();
		twp.taskLifecycleEventNotification(new TaskCreatedEvent(start, null, top));
		TaskCycler topc = new CreateTaskWaterfallTest.TaskCycler(top, nt, ROOT_TASK, twp);
		return topc;
	}

	public class TaskCycler extends Thread {

	
		int ii = 0;

		int mycycles;

		int type;

		TaskDescriptor me;

		TaskLifecycleWaterfallPanel twp;

		boolean ended;

		List<TaskCycler> subtasks;

		/**
		 * @param me
		 * @param ismanager
		 */
		public TaskCycler(TaskDescriptor me, int mycycles, int type, TaskLifecycleWaterfallPanel twp) {
			super();
			this.me = me;
			this.mycycles = mycycles;
			this.type = type;
			this.twp = twp;
			// only needed if ismanager
			subtasks = new Vector<TaskCycler>();
		}

		@Override
		public void run() {

			System.err.println("Starting cycler for: " + me);
			long t = 0;
			switch (type) {
			case ROOT_TASK:
				
				t = delay(10, 20);
				notification(new TaskInitializedEvent(t, me));
				t = delay(20, 40);
				notification(new TaskStartedEvent(t, me));
				
				for (int ic = 0; ic < mycycles; ic++) {
					// every so often create a new subtask...
					t = delay(0, 30);

					// create a modal (20%)
					TaskDescriptor mt = new TaskDescriptor("CA" + (++ii), "Mgr");
					mt.setIsManager(true);
					// how many cycles

					double rnd = Math.random();
					// at least 5 modal instances
					int nc = Math.max(5, 1 + (int) (mycycles * rnd));
					notification(new TaskCreatedEvent(t, me, mt));
					TaskCycler exec = new TaskCycler(mt, 10, MODAL_TASK, twp);
					exec.start();
					subtasks.add(exec);
					
					while (! exec.ended) {
						t = delay(2, 5);
					}
				
				} // next new top manager

				notification(new TaskCompletedEvent(t, me));
				ended = true;
			
				break;
				
			case MODAL_TASK:
				
				t = delay(10, 20);
				notification(new TaskInitializedEvent(t, me));
				t = delay(20, 40);
				notification(new TaskStartedEvent(t, me));
				
				for (int ic = 0; ic < mycycles; ic++) {
					// every so often create a new subtask...
					t = delay(0, 30);

					
					// create a manager (20%)
					TaskDescriptor mt = new TaskDescriptor(me.getName() + "/T" + (++ii), "TopMgr");
					mt.setIsManager(true);
					// how many cycles

					double rnd = Math.random();
					
					notification(new TaskCreatedEvent(t, me, mt));
					TaskCycler exec = new TaskCycler(mt, 10, TLM_TASK, twp);
					exec.start();
					subtasks.add(exec);
					
					while (! exec.ended) {
					    t = delay(2, 5);
					}
				
				} // next new top manager

				notification(new TaskCompletedEvent(t, me));
				ended = true;
			
				break;
				
			case MGR_TASK:	
			    t = delay(10, 20);
			    notification(new TaskInitializedEvent(t, me));
			    t = delay(20, 40);
			    notification(new TaskStartedEvent(t, me));

			    for (int ic = 0; ic < mycycles; ic++) {
				// every so often create a new subtask...
				t = delay(0, 30);

				if (Math.random() > 0.2) {
				    // create an executive (80%)
				    TaskDescriptor et = new TaskDescriptor(me.getName() + "/" + (++ii), "Exec");
				    et.setIsManager(false);
				    notification(new TaskCreatedEvent(t, me, et));
				    TaskCycler exec = new TaskCycler(et, 0, EXEC_TASK, twp);
				    exec.start();
				    subtasks.add(exec);
				} else {
				    // create a manager (20%)
				    TaskDescriptor mt = new TaskDescriptor(me.getName() + "/" + (++ii), "Mgr");
				    mt.setIsManager(true);
				    // how many cycles

				    double rnd = Math.random();
				    int nc = 1 + (int) (3.0 * rnd);
				    notification(new TaskCreatedEvent(t, me, mt));
				    TaskCycler exec = new TaskCycler(mt, nc, MGR_TASK, twp);
				    exec.start();
				    subtasks.add(exec);
				}
			    } // next new subtask

			    // wait till all subtasks are ended
			    int neta = 0;
			    while (neta < mycycles) {
				neta = 0;
				for (int it = 0; it < mycycles; it++) {
				    TaskCycler stc = subtasks.get(it);
				    if (stc.ended)
					neta++;
				}
				t = delay(2, 5);
			    }

			    notification(new TaskCompletedEvent(t, me));
			    ended = true;

			    break;

			case TLM_TASK:
				t = delay(10, 20);
				notification(new TaskInitializedEvent(t, me));
				t = delay(20, 40);
				notification(new TaskStartedEvent(t, me));
				
				for (int ic = 0; ic < mycycles; ic++) {
					// every so often create a new subtask...
					t = delay(0, 30);

					if (Math.random() > 0.2) {
						// create an executive (80%)
						TaskDescriptor et = new TaskDescriptor(me.getName() + "/" + (++ii), "Exec");
						et.setIsManager(false);
						notification(new TaskCreatedEvent(t, me, et));
						TaskCycler exec = new TaskCycler(et, 0, EXEC_TASK, twp);
						exec.start();
						subtasks.add(exec);
					} else {
						// create a manager (20%)
						TaskDescriptor mt = new TaskDescriptor(me.getName() + "/" + (++ii), "Mgr");
						mt.setIsManager(true);
						// how many cycles

						double rnd = Math.random();
						int nc = 1 + (int) (3.0 * rnd);
						notification(new TaskCreatedEvent(t, me, mt));
						TaskCycler exec = new TaskCycler(mt, nc, MGR_TASK, twp);
						exec.start();
						subtasks.add(exec);
					}
				} // next new subtask

				// wait till all subtasks are ended
				int net = 0;
				while (net < mycycles) {
					net = 0;
					for (int it = 0; it < mycycles; it++) {
						TaskCycler stc = subtasks.get(it);
						if (stc.ended)
							net++;
					}
					t = delay(2, 5);
				}

				notification(new TaskCompletedEvent(t, me));
				ended = true;
			
				break;
				
			case EXEC_TASK:
				// c=0 do executive stuff
				// we know our lifetime
				t = delay(10, 60);
				notification(new TaskInitializedEvent(t, me));
				t = delay(10, 60);
				notification(new TaskStartedEvent(t, me));
				t = delay(50, 200);
				notification(new TaskCompletedEvent(t, me));
				ended = true;
				break;
			}
			


		}

		private long delay(int secs1, int secs2) {
			long rsecs = (int) (Math.random() * (secs2 - secs1)) + secs1;
			// System.err.println("...exec in " + (rsecs) + "...");
			try {
				Thread.sleep(rsecs * 1000L);
			} catch (InterruptedException ix) {
			}
			return System.currentTimeMillis();
		}

		private void notification(TaskLifecycleEvent ev) {
			try {
				twp.taskLifecycleEventNotification(ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class TBActionListener implements ActionListener {

	    //@Override
		@Override
		public void actionPerformed(ActionEvent ae) {
			String cmd = ae.getActionCommand();
			if (cmd.equals("Z+")) {
				twp.setLbScaleFactor(twp.getLbScaleFactor() / 2.0);
				twp.repaint();
			} else if (cmd.equals("z-")) {
				twp.setLbScaleFactor(twp.getLbScaleFactor() * 2.0);
				twp.repaint();
			} else if (cmd.equals("@")) {
				twp.setLbScaleFactor(1.0);
				twp.repaint();
			}
		}

	}

}
