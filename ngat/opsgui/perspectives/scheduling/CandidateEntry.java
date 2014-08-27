package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;
import ngat.sms.ScoreMetricsSet;

public class CandidateEntry extends TableEntry implements Comparable<CandidateEntry> {

    /** The name of the queue. e.g. PRIMARY, BACKGROUND, FIXED.*/
    private String queueName;

    /** The group which is the candidate.*/
    private GroupItem group;

    /** Group's overall score.*/
    private double score;

    /** Set of metrics.*/
    private ScoreMetricsSet metrics;
    
    /** The group's ranking 1=top.*/
    private int rank;
    
	/**
	 * @param queueName
	 * @param group
	 * @param score
	 * @param rank
	 */
	public CandidateEntry(String queueName, GroupItem group, ScoreMetricsSet metrics, double score, int rank) {
		super();
		this.queueName = queueName;
		this.group = group;
		this.metrics = metrics;
		this.score = score;
		this.rank = rank;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return the group
	 */
	public GroupItem getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(GroupItem group) {
		this.group = group;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	
	
	/**
	 * @return the metrics
	 */
	public ScoreMetricsSet getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(ScoreMetricsSet metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
    
	@Override
	public int compareTo(CandidateEntry o) {
		// we want them sorted in highest first order
		if (this.score > o.getScore())
			return -1;
		else if
		(this.score < o.getScore())
			return 1;
		return 0;
		
	}


}