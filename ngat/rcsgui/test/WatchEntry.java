/**
 * 
 */
package ngat.rcsgui.test;

/**
 * @author eng
 *
 */
public class WatchEntry {
		
		long time;
		
		boolean selected;
		
		double score;
		
		String errorTypeName;

		double wscore;
		
		/**
		 * @param time
		 * @param selected
		 * @param score
		 * @param errorTypeName
		 * @param wscore
		 */
		public WatchEntry(long time, boolean selected, double score, String errorTypeName, double wscore) {
			super();
			this.time = time;
			this.selected = selected;
			this.score = score;
			this.errorTypeName = errorTypeName;
			this.wscore = wscore;
		}
		
	}
	
