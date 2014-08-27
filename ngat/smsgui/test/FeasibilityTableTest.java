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

import ngat.phase2.XGroup;
import ngat.sms.GroupItem;
import ngat.sms.genetic.test.GenomeMapper;
import ngat.sms.util.PrescanEntry;
import ngat.smsgui.FeasibilityTable;
import ngat.smsgui.FeasibilityTableModel;

/**
 * @author eng
 *
 */
public class FeasibilityTableTest {

	static int ii = 0;
	
	FeasibilityTableModel ftm;
	
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
			PrescanEntry frow = gendata();
			ftm.addEntry(frow);
			// update...
		}

	}

	
	/**
	 * 
	 */
	public FeasibilityTableTest() {
		ftm = new FeasibilityTableModel();

		FeasibilityTable ftable = new FeasibilityTable(ftm);

		JScrollPane jsp = new JScrollPane(ftable);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JFrame f = new JFrame("Test Prescan");
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
		
		FeasibilityTableTest ft = new FeasibilityTableTest();
		
	}

	private PrescanEntry gendata() {
	
		long start = System.currentTimeMillis();
		long end = start + 12*3600*1000L;
		
		PrescanEntry pse = new PrescanEntry(start, end, 60000L);
		pse.execTime = Math.random()*30*60*1000.0+120000.0;
		pse.gname = GenomeMapper.newName();
		XGroup g = new XGroup();
		g.setID((long)(Math.random()*10000.0));
		g.setName("Grp-"+(++ii));
		pse.group = new GroupItem(g, null);
		
		// make up some feasibility things...
		boolean f = false;
		long t = start;
		while (t < end) {
			if (f) {
				if (Math.random() > 0.6)
					f = false;				
			} else {
				if (Math.random() > 0.97)
					f = true;				
			}
			if (f)
			pse.setFeasible(t);
			t += 60*1000L;
		}
		
		return pse;
	}
	
}
