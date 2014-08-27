package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;

/** Summary of a sweep.*/
public class SummaryEntry {

    public int sweepNumber;

    public long sweepTime;

    public long sweepDuration;

    public int countCandidates;

    public int countRejects;

    public GroupItem winningGroup;

    public double winningScore;


    public SummaryEntry(int sweepNumber, SweepEntry sweep, ScheduleItem sched) {
	this.sweepNumber = sweepNumber;
	sweepTime = sweep.getTime();
	sweepDuration = sweep.getDuration();
	countCandidates = sweep.getCandidates().size();
	winningGroup = (sched != null ? sched.getGroup() : null);
	winningScore = (sched != null ? sched.getScore() : 0.0);
    }
  
}