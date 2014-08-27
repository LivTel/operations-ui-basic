/**
 * 
 */
package ngat.opsgui.test;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ngat.rcs.ers.Criterion;
import ngat.rcs.ers.Filter;
import ngat.rcs.ers.ReactiveSystemMonitor;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.ers.ReactiveSystemUpdateListener;
import ngat.rcs.ers.Rule;

/**
 * @author eng
 *
 */
public class LookupReactiveSystemStructure extends UnicastRemoteObject implements ReactiveSystemUpdateListener {

	/**
	 * 
	 */
	public LookupReactiveSystemStructure() throws RemoteException {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
	
			LookupReactiveSystemStructure ll = new LookupReactiveSystemStructure();
			ll.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() throws Exception  {
		
		ReactiveSystemStructureProvider rsp = (ReactiveSystemStructureProvider)Naming.lookup("rmi://ltsim1/ReactiveSystem");
		
		System.err.println("Found ReactiveSystemStructureProvider: "+rsp);
		
		System.err.println("Lookup filters...");
		List<Filter> filters = rsp.listFilters();
		for (int i =0; i < filters.size(); i++) {
			System.err.println("Found: "+filters.get(i));
		}
		
		System.err.println();
		System.err.println("Lookup criteria...");
		List<Criterion> crits = rsp.listCriteria();
		for (int i = 0; i < crits.size(); i++ ) {
			System.err.println("Found: "+crits.get(i));
		}
		
		System.err.println();
		System.err.println("Lookup rules...");
		int nr = 0;
		List<Rule> rules = rsp.listRules();
		for (int i = 0; i < rules.size(); i++ ) {
			System.err.println("Found: "+rules.get(i));
			nr++;
		}
		System.err.println("Found "+nr+" rules");
		
		
		System.err.println();
		System.err.println("Lookup mappings, filters -> criteria ...");
		Map<String, List<Criterion>> f2c = rsp.getFilterCriterionMapping();
		Iterator<String> it = f2c.keySet().iterator();
		while (it.hasNext()) {
			String fname = it.next();
			List<Criterion> clist = f2c.get(fname);
			Iterator<Criterion> ic = clist.iterator();
			while (ic.hasNext()) {
				Criterion crit = ic.next();
				System.err.println("Link Filter to Crit: "+fname+" -> "+crit.getCriterionName());
			}
		}
		
		System.err.println();
		System.err.println("Lookup mappings, criteria -> rules ...");
		Map<String, Rule> c2r = rsp.getCriterionRuleMapping();
		Iterator<String> ic = c2r.keySet().iterator();
		while (ic.hasNext()) {
			String cname = ic.next();
			Rule rule = c2r.get(cname);
			System.err.println("Link Crit to Rule: "+cname+" -> "+rule.getRuleName());				
		}
		
		System.err.println();
		System.err.println("Lookup hierarchic rule mapping...");
		Map<String, Rule> r2r = rsp.getRuleRuleMapping();
		Iterator<String> ir = r2r.keySet().iterator();
		while (ir.hasNext()) {
			String rname = ir.next();
			Rule rule = r2r.get(rname);
			System.err.println("Link Rule to Rule: "+rname+" -> "+rule.getRuleName());				
		}
		
		// Now lets register for updates
		ReactiveSystemMonitor rsm = (ReactiveSystemMonitor)Naming.lookup("rmi://ltsim1/ReactiveSystem");
		
		rsm.addReactiveSystemUpdateListener(this);
		
		while (true) {
			try {Thread.sleep(60000L);} catch (Exception e) {}
		}
		
	}

	@Override
	public void criterionUpdated(String name, long time, boolean sat)
			throws RemoteException {
		System.err.printf("Criterion update: %15.15s %10.10s \n", name, (sat ? "[SAT]" : "[UNSAT]"));
	}

	@Override
	public void filterUpdated(String name, long time, Number sensorValue, Number filteredValue)
			throws RemoteException {
		
		System.err.printf("Filter update:    %15.15s %10.2f %10.2f \n", name, sensorValue.doubleValue(), filteredValue.doubleValue());
	}

	@Override
	public void ruleUpdated(String name, long time, boolean trigger)
			throws RemoteException {
		System.err.printf("Rule update:      %15.15s %10.10s \n", name, (trigger ? "[TRIG]" : "[NOTRIG]"));
	}

}
