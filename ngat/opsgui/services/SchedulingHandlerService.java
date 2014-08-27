/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingArchiveGateway;
import ngat.sms.SchedulingStatusProvider;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;
import ngat.sms.events.CandidateAddedEvent;
import ngat.sms.events.CandidateRejectedEvent;
import ngat.sms.events.SchedulingStatus;
import ngat.sms.events.SweepCompletedEvent;
import ngat.sms.events.SweepStartingEvent;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 *
 */
public class SchedulingHandlerService extends BasicServiceProvider implements SchedulingStatusUpdateListener, ServiceProvider {
	
	private String schedUrl;
	
	private String schedArchiveUrl;
	
	private long lookBackTime;
	
	private List<SchedulingStatus> liveCache;
	private List<SchedulingStatus> archiveCache;
	private List<SchedulingStatusUpdateListener> listeners;
	
	/** Logging. */
	private LogGenerator logger;
	
	public SchedulingHandlerService() throws RemoteException {
		super();
		liveCache = new Vector<SchedulingStatus>();
		archiveCache = new Vector<SchedulingStatus>();
		listeners = new Vector<SchedulingStatusUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate()
					.system("GUI")
					.subSystem("Services")
					.srcCompClass(this.getClass().getSimpleName())
					.srcCompId("SCHED");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try{
			SchedulingHandlerService shs = new SchedulingHandlerService();
			shs.schedUrl = "rmi://ltsim1/Scheduler";
			shs.schedArchiveUrl= "rmi://ltsim1/SchedulerGateway";
			shs.lookBackTime = 1800000L;
			//tss.registerService();
			shs.loadServiceArchive();
			try {Thread.sleep(10000L);} catch (InterruptedException ix){}
			shs.registerService();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addListener(SchedulingStatusUpdateListener l) {
		listeners.add(l);
	}

	/**
	 * @return the schedUrl
	 */
	public String getSchedUrl() {
		return schedUrl;
	}

	/**
	 * @param schedUrl the schedUrl to set
	 */
	public void setSchedUrl(String schedUrl) {
		this.schedUrl = schedUrl;
	}

	/**
	 * @return the schedArchiveUrl
	 */
	public String getSchedArchiveUrl() {
		return schedArchiveUrl;
	}

	/**
	 * @param schedArchiveUrl the schedArchiveUrl to set
	 */
	public void setSchedArchiveUrl(String schedArchiveUrl) {
		this.schedArchiveUrl = schedArchiveUrl;
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

	@Override
	public String getServiceProviderName() {
		return "SCHED";
	}

	@Override
	public void registerService() throws Exception {
		//SchedulingStatusProvider sms = (SchedulingStatusProvider)Naming.lookup(schedUrl);	
		SchedulingStatusProvider sms = (SchedulingStatusProvider)Naming.lookup(schedArchiveUrl);	
		sms.addSchedulingUpdateListener(this);
		logger.create().info().level(2).extractCallInfo()
		.msg("Registering with remote service provider: "+schedUrl).send();
	}

	@Override
	public int loadServiceArchive() throws Exception {
		SchedulingArchiveGateway ssa = (SchedulingArchiveGateway)Naming.lookup(schedArchiveUrl);
		
		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime; 
		List<SchedulingStatus> list = ssa.getSchedulerStatusHistory(t1, t2);
		
		// add these to the archive cache	
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+list.size()+" status items").send();
		archiveCache.addAll(list);
		
		return list.size();
	}

	@Override
	public void broadcastServiceAvailability(boolean available) {
		logger.create().info().level(3).extractCallInfo()
		.msg("Broadcasting service availability: "+(available ? "ONLINE" : "OFFLINE")).send();
		long time = System.currentTimeMillis();
		for (int il = 0; il < listeners.size(); il++) {
			SchedulingStatusUpdateListener l = listeners.get(il);
			try {
				((ServiceAvailabilityListener)l).serviceAvailable(getServiceProviderName(), time, available);
			} catch (Exception e) {
				// log and maybe mark listener for deletion
				logger.create().info().level(3).extractCallInfo()
				.msg("Exception updating listener: "+l+": "+e).send();
			}
		}
		
	}

	@Override
	public int broadcastStatus() throws Exception {
		// cycle thro archive-cache and send out to each registered listener	
				int as = archiveCache.size();
				for (int ic = 0; ic < as; ic++) {
					SchedulingStatus status = archiveCache.get(ic);
					for (int il = 0; il < listeners.size(); il++) {
						SchedulingStatusUpdateListener l = listeners.get(il);
						try {
							if (status instanceof CandidateAddedEvent) {						
								CandidateAddedEvent cav = (CandidateAddedEvent)status;								
								l.candidateAdded(cav.getQueueId(), cav.getGroup(), cav.getMetrics(), cav.getScore(), cav.getRank());
							} else if
							(status instanceof CandidateRejectedEvent) {
								CandidateRejectedEvent crj = (CandidateRejectedEvent)status;
								l.candidateRejected(crj.getQueueId(), crj.getGroup(), crj.getReason());
								
							} else if
							(status instanceof SweepStartingEvent) {
								SweepStartingEvent sse = (SweepStartingEvent)status;
								l.scheduleSweepStarted(sse.getStatusTimeStamp(), sse.getSweepId());
							}else if
							(status instanceof SweepCompletedEvent) {
								SweepCompletedEvent sce = (SweepCompletedEvent)status;
								l.candidateSelected(sce.getStatusTimeStamp(), sce.getSchedule());
							}
							// TODO canddiate failed.
						} catch (Exception e) {
							// log and maybe mark listener for deletion
							logger.create().info().level(3).extractCallInfo()
							.msg("Exception updating listener: "+l+": "+e).send();
						}
					}
				}
				archiveCache.subList(0, as).clear();
				// cycle thro live-cache and send out to each registered listener	
				int ls = liveCache.size();
				//System.err.println("SMS broadcasting upto: "+liveCache.size()+" items");
				for (int ic = 0; ic < ls; ic++) {
					SchedulingStatus status = liveCache.get(ic);
					for (int il = 0; il < listeners.size(); il++) {
						SchedulingStatusUpdateListener l = listeners.get(il);
						try {							
							if (status instanceof CandidateAddedEvent) {						
								CandidateAddedEvent cav = (CandidateAddedEvent)status;								
								l.candidateAdded(cav.getQueueId(), cav.getGroup(), cav.getMetrics(), cav.getScore(), cav.getRank());
								
							} else if
							(status instanceof CandidateRejectedEvent) {
								CandidateRejectedEvent crj = (CandidateRejectedEvent)status;
								l.candidateRejected(crj.getQueueId(), crj.getGroup(), crj.getReason());
								
							} else if
							(status instanceof SweepStartingEvent) {
								SweepStartingEvent sse = (SweepStartingEvent)status;
								l.scheduleSweepStarted(sse.getStatusTimeStamp(), sse.getSweepId());
							}else if
							(status instanceof SweepCompletedEvent) {
								SweepCompletedEvent sce = (SweepCompletedEvent)status;
								l.candidateSelected(sce.getStatusTimeStamp(), sce.getSchedule());
							}
							
							// TODO canddiate failed.
							
						} catch (Exception e) {
							// log and maybe mark listener for deletion
							logger.create().info().level(3).extractCallInfo()
							.msg("Exception updating listener: "+l+": "+e).send();
						}
					}
				}
				// clear the elements weve already processed
				liveCache.subList(0, ls).clear();
		return ls;
	}

	@Override
	public void candidateAdded(String queueId, GroupItem group, ScoreMetricsSet metrics, double score, int rank)
			throws RemoteException {
		CandidateAddedEvent cav = new CandidateAddedEvent();
		cav.setQueueId(queueId);
		cav.setGroup(group);
		cav.setMetrics(metrics);
		cav.setScore(score);
		cav.setRank(rank);
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+cav).send();		
		liveCache.add(cav);		
	}
	
	@Override
	public void candidateRejected(String q, GroupItem group, String reason) throws RemoteException {
		CandidateRejectedEvent crj = new CandidateRejectedEvent();
		crj.setGroup(group);
		crj.setReason(reason);
		//crj.setStatusTimeStamp(statusTimeStamp)
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+crj).send();		
		liveCache.add(crj);		
	}
	
	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		SweepCompletedEvent sce = new SweepCompletedEvent();
		// TODO the sched does not come with its own timestamp !!!!!	
		sce.setStatusTimeStamp(time);
		sce.setSchedule(sched);
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+sched).send();		
		liveCache.add(sce);				
	}

	@Override
	public void scheduleSweepStarted(long time, int sweepId) throws RemoteException {
		SweepStartingEvent sse = new SweepStartingEvent();
		sse.setStatusTimeStamp(time);
		sse.setSweepId(sweepId);
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+sse).send();		
		liveCache.add(sse);
	}

	@Override
	public void schedulerMessage(int arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	


}
