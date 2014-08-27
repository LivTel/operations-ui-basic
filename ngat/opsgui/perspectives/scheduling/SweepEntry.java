package ngat.opsgui.perspectives.scheduling;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class SweepEntry {

	/** When the sweep started. */
	private long time;
	
	/** Sweep unique (all time) id.*/
	private int sweepId;

	/** Duration of the sweep (ms). */
	private long duration;

	/** List of potential candidates fro selection during sweep. */
	private List<CandidateEntry> candidates;

	/** List of groups rejected during sweep. */
	private List<RejectEntry> rejects;

	/**
	 * Create a new SweepEntry.
	 * 
	 * @param time
	 *            The start time of the sweep.
	 */
	public SweepEntry(long time, int sweepId) {
		this.time = time;
		this.sweepId = sweepId;
		candidates = new Vector<CandidateEntry>();
		rejects = new Vector<RejectEntry>();
	}

	/**
	 * @return the sweepId
	 */
	public int getSweepId() {
		return sweepId;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addCandidate(CandidateEntry c) {
		return candidates.add(c);
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addReject(RejectEntry r) {
		return rejects.add(r);
	}

	/**
	 * @return the candidates
	 */
	public List<CandidateEntry> getCandidates() {
		return candidates;
	}

	/**
	 * @return the rejects
	 */
	public List<RejectEntry> getRejects() {
		return rejects;
	}

	public void sortByRank() {
		Collections.sort(candidates);
		for (int is = 0; is < candidates.size(); is++) {
			CandidateEntry c = candidates.get(is);
			c.setRank(is+1);
		}
	}
	
	// Special search methods

	/** Find a group in the Candidate list or NULL.
	 * @param gid The group's ID.
	 * @return The candidate or NULL.
	 */
	public CandidateEntry findCandidate(long gid) {
		for (int ic = 0; ic < candidates.size(); ic++) {
			CandidateEntry entry = candidates.get(ic);
			if (entry.getGroup().getID() == gid)
				return entry;
		}
		return null;
	}
       
	/** Find a group in the Candidate list or NULL.
	 * @param gid The group's ID.
	 * @return The candidate or NULL.
	 */
	public RejectEntry findReject(long gid) {
		for (int ic = 0; ic < rejects.size(); ic++) {
			RejectEntry entry = rejects.get(ic);
			if (entry.getGroup().getID() == gid)
				return entry;
		}
		return null;
	}
	
       
}