package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;

/** Summary of a sweep.*/
public class SummaryRow {

    public int sweepNumber;

    public long sweepTime;

    public long sweepDuration;

    public int countCandidates;

    public int countRejects;

    public GroupItem winningGroup;

    public double winningScore;

    public SummaryRow(SummaryEntry summary) {
	sweepNumber = summary.sweepNumber;
	sweepTime = summary.sweepTime;
	sweepDuration = summary.sweepDuration;
	countCandidates = summary.countCandidates;
	countRejects = summary.countRejects;
	winningGroup = summary.winningGroup;
	winningScore = summary.winningScore;
    }
  
}