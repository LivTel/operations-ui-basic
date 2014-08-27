/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Dimension;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.util.DataField;
import ngat.phase2.IProposal;
import ngat.sms.GroupItem;

/**
 * @author eng
 * 
 */
public class ObservationSummaryPanel extends SummaryPane {

	//private static Dimension OBS_PANEL_SIZE = new Dimension(200, 140);
	
	private DataField tagNameField;
	private DataField proposalNameField;
	private DataField programNameField;
	private DataField userNameField;
	private DataField groupNameField;
	private DataField priorityField;

	/**
	 * @param title
	 */
	public ObservationSummaryPanel(String title) {
		super(title);
		//setPreferredSize(OBS_PANEL_SIZE);
	}

	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
		
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel("TAG"));
		tagNameField = ComponentFactory.makeDataField(6, "%9.9s");
		l.add(tagNameField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Prog"));
		programNameField = ComponentFactory.makeDataField(6, "%10.10s");
		l.add(programNameField);
		add(l);

		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Proposal"));
		proposalNameField = ComponentFactory.makeDataField(6, "%9.9s");
		l.add(proposalNameField);
		priorityField = ComponentFactory.makeDataField(4, "%4.4s");
		l.add(priorityField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("User (PI)"));
		userNameField = ComponentFactory.makeDataField(14, "%18.18s");
		l.add(userNameField);
		add(l);

		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Group"));
		groupNameField = ComponentFactory.makeDataField(14, "%20.20s");
		l.add(groupNameField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Test"));	
		DataField testField = ComponentFactory.makeDataField(14, "%20.20s");
		l.add(testField);
		add(l);
		
	}
	
	public void updateGroup(GroupItem group) {
		if (group == null)
			return;
		String groupName = group.getName();
		String propName = (group.getProposal() != null ? group.getProposal().getName() : "Null");
		String progName = (group.getProgram() != null ? group.getProgram().getName() : "Null");
		String tagName = (group.getTag() != null ? group.getTag().getName() : "Null");
		String userName = (group.getUser() != null ? group.getUser().getName() : "Null");
		
		// work out priority info: [*]<cat>[+] eg *A+3
		StringBuffer priorityDesc = new StringBuffer();
		if (group.isUrgent())
			priorityDesc.append("*");
		IProposal proposal = group.getProposal();
		int proposalCat = proposal.getPriority();
		switch (proposalCat) {
		case IProposal.PRIORITY_A:
			priorityDesc.append("A");
			break;
		case IProposal.PRIORITY_B:
			priorityDesc.append("B");
			break;
		case IProposal.PRIORITY_C:
			priorityDesc.append("C");
			break;
		case IProposal.PRIORITY_Z:
			priorityDesc.append("Z");
			break;
		}
		
		if (proposal.getPriorityOffset() > 0.0) {
			priorityDesc.append(String.format("+%2.1f", proposal.getPriorityOffset()));
		}
		
		tagNameField.setText(tagName);
		userNameField.setText(userName);
		programNameField.setText(progName);
		proposalNameField.setText(propName);
		groupNameField.setText(groupName);	
		priorityField.setText(priorityDesc.toString());
		
	}

}
