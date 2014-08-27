package ngat.rcsgui.stable;

import ngat.rcs.telemetry.GroupOperationsListener;
import ngat.sms.GroupItem;
import ngat.opsgui.base.TopPanel;
import ngat.opsgui.xcomp.GroupDisplayPanel;
import ngat.phase2.*;
import java.rmi.*;
import java.rmi.server.*;

public class ObservationUpdateListener extends UnicastRemoteObject implements GroupOperationsListener {

	private ObservationPanel obsPanel;
	private SequencePanel seqPanel;
	private OperationsPanel opsPanel;
	private EfficiencyPanel effPanel;
	 private GroupDisplayPanel gdp;
	 
	private TopPanel topPanel;
	
	public ObservationUpdateListener(ObservationPanel obsPanel, SequencePanel seqPanel, OperationsPanel opsPanel, EfficiencyPanel effPanel, TopPanel topPanel, GroupDisplayPanel gdp)
			throws RemoteException {
		super();
		this.obsPanel = obsPanel;
		this.seqPanel = seqPanel;
		this.opsPanel = opsPanel;
		this.effPanel = effPanel;
		this.topPanel = topPanel;
		this.gdp = gdp;
	}

	@Override
	public void groupSelected(GroupItem group) throws RemoteException {
		System.err.println("OBSUPDATELISTENER: Group selected: " + group);

		ITag tag = group.getTag();
		IUser user = group.getUser();
		IProgram prog = group.getProgram();
		IProposal prop = group.getProposal();

		String tname = (tag != null ? tag.getName() : "UNK");
		String uname = (user != null ? user.getName() : "UNK");
		String pname = (prop != null ? prop.getName() : "UNK");
		
		if (obsPanel != null)
		    obsPanel.update(group);
	
		if (seqPanel != null)
		    seqPanel.update(group.getSequence());
	
		if (opsPanel != null)
		    opsPanel.update(group);
	
		if (topPanel != null)
			topPanel.groupSelected(group);
		
		if (gdp != null)
			gdp.notifyGroupStarted(group);
		
	}

	@Override
	public void groupCompleted(GroupItem group, IExecutionFailureContext error) throws RemoteException {

		System.err.println("OBSUPDATELISTNER: Group hasCompleted: " + group + " Error: " + error);

		if (error == null) {
		    if (obsPanel != null)
		    	obsPanel.completed(group);
		    if (opsPanel != null)
		    	opsPanel.completed(group);
		   if (gdp != null)
		    	gdp.notifyGroupCompleted(group);
		} else {
		    if (obsPanel != null)
		    	obsPanel.failed(group, error);
		    if (opsPanel != null)
		    	opsPanel.failed(group, error);
		    if (gdp != null)
		    	gdp.notifyGroupFailed(group, error);
		}
	
	}
}