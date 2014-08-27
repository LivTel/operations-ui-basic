/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;
import ngat.sms.ScoreMetricsSet;

/**
 * @author eng
 * 
 */
public class CandidateRow { // implements Comparable<CandidateRow> {

	public int rank;

	public GroupItem group;

	public ScoreMetricsSet metrics;

	public double xt;

	public double score;

	public CandidateRow() {
	}

	public CandidateRow(CandidateEntry entry) {
		// entry.getQueueName();
		group = entry.getGroup();
		score = entry.getScore();
		rank = entry.getRank();
		metrics = entry.getMetrics();
		xt = 0.0; // TODO
	}

	/*@Override
	public int compareTo(CandidateRow o) {
		// we want them sorted in highest first order
		if (this.score > o.score)
			return -1;
		else if
		(this.score < o.score)
			return 1;
		return 0;
		
	}*/

}
