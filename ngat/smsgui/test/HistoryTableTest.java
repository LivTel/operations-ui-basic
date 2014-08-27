/**
 * 
 */
package ngat.smsgui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import ngat.phase2.IProposal;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XGroup;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XProposal;
import ngat.phase2.XTag;
import ngat.phase2.XUser;
import ngat.sms.GroupItem;
import ngat.smsgui.HistoryRow;
import ngat.smsgui.HistoryTable;
import ngat.smsgui.HistoryTableModel;

/**
 * @author eng
 * 
 */
public class HistoryTableTest {

	static int ii = 0;

	/**
	 * @author eng
	 * 
	 */
	public class TestActionListener implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			HistoryRow trow = gendata();
			htm.addRow(trow);
			// update...
		}

	}

	HistoryTableModel htm;

	public HistoryTableTest() {

		htm = new HistoryTableModel();

		HistoryTable ctable = new HistoryTable(htm);

		JScrollPane jsp = new JScrollPane(ctable);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JFrame f = new JFrame("Test Sweep History");
		Container cf = f.getContentPane();

		cf.setLayout(new BorderLayout());
		cf.add(jsp, BorderLayout.CENTER);

		JButton b = new JButton("Test");
		ActionListener al = new TestActionListener();
		b.addActionListener(al);

		cf.add(b, BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		HistoryTableTest ht  = new HistoryTableTest();
		
	}

	private HistoryRow gendata() {
	
		int i = ++ii;
		XGroup g = new XGroup();
		g.setID(1000 * i);
		g.setName("testgroup-" + i);
		g.setUrgent(Math.random() < 0.2);

		ITimingConstraint tt = null;
		double dt = Math.random();
		if (dt < 0.1)
			tt = new XFlexibleTimingConstraint();
		else if (dt < 0.25)
			tt = new XMonitorTimingConstraint();
		else if (dt < 0.4)
			tt = new XEphemerisTimingConstraint();
		else if (dt < 0.85)
			tt = new XMinimumIntervalTimingConstraint();
		else
			tt = new XFixedTimingConstraint();

		g.setTimingConstraint(tt);
		GroupItem gi = new GroupItem(g, null);
		XProposal p = new XProposal("PP10A0" + i);
		double d = Math.random();
		int pp = IProposal.PRIORITY_Z;
		if (d < 0.2)
			pp = IProposal.PRIORITY_C;
		else if (d < 0.4)
			pp = IProposal.PRIORITY_B;
		else if (d < 0.9)
			pp = IProposal.PRIORITY_A;
		else
			pp = IProposal.PRIORITY_Z;

		p.setPriority(pp);
		double po = (Math.random() - 0.5);
		p.setPriorityOffset(po);
		gi.setProposal(p);
		gi.setUser(new XUser("Bloggs.Josephine"));
		XTag t = new XTag();
		t.setName("TT" + i);
		gi.setTag(t);

		HistoryRow hr = new HistoryRow();
		hr.group = gi;
		hr.sweep = i;
		hr.time = System.currentTimeMillis() - i * 55 * 60 * 1000L;
		hr.score = Math.random();
		return hr;
	}
}
