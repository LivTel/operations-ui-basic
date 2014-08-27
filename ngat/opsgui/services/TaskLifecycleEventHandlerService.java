/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.rcs.tms.TaskArchive;
import ngat.rcs.tms.TaskLifecycleListener;
import ngat.rcs.tms.TaskMonitor;
import ngat.rcs.tms.events.TaskLifecycleEvent;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 *
 */
public class TaskLifecycleEventHandlerService extends BasicServiceProvider implements ServiceProvider,
		TaskLifecycleListener {
	
	private String tmonUrl;
	
	private String tmaUrl;
	
	private long lookBackTime;
	
	private List<TaskLifecycleEvent> liveCache;
	private List<TaskLifecycleEvent> archiveCache;
	private List<TaskLifecycleListener> listeners;
	
	/** Logging. */
	private LogGenerator logger;
	
	public TaskLifecycleEventHandlerService() throws RemoteException {
		super();
		liveCache = new Vector<TaskLifecycleEvent>();
		archiveCache = new Vector<TaskLifecycleEvent>();
		listeners = new Vector<TaskLifecycleListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate()
					.system("GUI")
					.subSystem("Services")
					.srcCompClass(this.getClass().getSimpleName())
					.srcCompId("TASK");
	}
	
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			try{
				TaskLifecycleEventHandlerService tss = new TaskLifecycleEventHandlerService();
				tss.tmonUrl = "rmi://ltsim1/TaskGateway";
				tss.tmaUrl = "rmi://ltsim1/TaskGateway";
				tss.lookBackTime = 1800000L;			
				tss.loadServiceArchive();
				try {Thread.sleep(10000L);} catch (InterruptedException ix){}
				tss.registerService();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		public void addListener(TaskLifecycleListener l) {
			listeners.add(l);
		}

		/**
		 * @return the tmonUrl
		 */
		public String getTmonUrl() {
			return tmonUrl;
		}

		/**
		 * @param tmonUrl the tmonUrl to set
		 */
		public void setTmonUrl(String tmonUrl) {
			this.tmonUrl = tmonUrl;
		}

		/**
		 * @return the tmaUrl
		 */
		public String getTmaUrl() {
			return tmaUrl;
		}

		/**
		 * @param tmaUrl the tmaUrl to set
		 */
		public void setTmaUrl(String tmaUrl) {
			this.tmaUrl = tmaUrl;
		}

		/**
		 * @return the lookBackTime
		 */
		public long getLookBackTime() {
			return lookBackTime;
		}

		/**
		 * @param lookBackTime the lookBackTime to set
		 */
		public void setLookBackTime(long lookBackTime) {
			this.lookBackTime = lookBackTime;
		}

		

    //@Override
		@Override
		public String getServiceProviderName() {
			return "TASK";
		}

    //	@Override
		@Override
		public void registerService() throws Exception {		
			TaskMonitor tmon = (TaskMonitor)Naming.lookup(tmonUrl);
			tmon.addTaskEventListener(this);	
			logger.create().info().level(2).extractCallInfo()
			.msg("Registering with remote service provider: "+tmonUrl).send();
		}

    //	@Override
		@Override
		public int loadServiceArchive() throws Exception {
			TaskArchive tsa = (TaskArchive)Naming.lookup(tmaUrl);
			
			long t2 = System.currentTimeMillis();
			long t1 = t2 - lookBackTime; 
			List<TaskLifecycleEvent> list = tsa.getTaskLifecycleHistory(t1, t2);
			
			// add these to the archive cache	
			logger.create().info().level(3).extractCallInfo()
			.msg("Received "+list.size()+" events").send();
			archiveCache.addAll(list);
			
			return list.size();
		}

    //	@Override
		@Override
		public void broadcastServiceAvailability(boolean available) {
			logger.create().info().level(3).extractCallInfo()
			.msg("Broadcasting service availability: "+(available ? "ONLINE" : "OFFLINE")).send();
			long time = System.currentTimeMillis();
			for (int il = 0; il < listeners.size(); il++) {
				TaskLifecycleListener l = listeners.get(il);
				try {
					((ServiceAvailabilityListener)l).serviceAvailable(getServiceProviderName(), time, available);
				} catch (Exception e) {
					// log and maybe mark listener for deletion
					logger.create().info().level(3).extractCallInfo()
					.msg("Exception updating listener: "+l+": "+e).send();
				}
			}
			
		}

    //	@Override
		@Override
		public int broadcastStatus() throws Exception {
			// cycle thro archive-cache and send out to each registered listener	
			int as = archiveCache.size();
			for (int ic = 0; ic < as; ic++) {
				TaskLifecycleEvent event = archiveCache.get(ic);
				for (int il = 0; il < listeners.size(); il++) {
					TaskLifecycleListener l = listeners.get(il);
					try {
					l.taskLifecycleEventNotification(event);
					} catch (Exception e) {
						// log and maybe mark listener for deletion
						logger.create().info().level(3).extractCallInfo()
						.msg("Exception updating listener: "+l+": "+e).send();
						e.printStackTrace();
					}
				}
			}
			archiveCache.subList(0, as).clear();
			// cycle thro live-cache and send out to each registered listener	
			int ls = liveCache.size();
			for (int ic = 0; ic < ls; ic++) {
				TaskLifecycleEvent event = liveCache.get(ic);
				for (int il = 0; il < listeners.size(); il++) {
					TaskLifecycleListener l = listeners.get(il);
					try {
					l.taskLifecycleEventNotification(event);
					} catch (Exception e) {
						// log and maybe mark listener for deletion
						logger.create().info().level(3).extractCallInfo()
						.msg("Exception updating listener: "+l+": "+e).send();
						e.printStackTrace();
					}
				}
			}
			// clear the elements weve already processed
			liveCache.subList(0, ls).clear();
			return ls;
		}

    //	@Override
		@Override
		public void taskLifecycleEventNotification(TaskLifecycleEvent event) throws RemoteException {
			logger.create().info().level(3).extractCallInfo()
			.msg("Received "+event).send();		
			liveCache.add(event);
			
		}



}
