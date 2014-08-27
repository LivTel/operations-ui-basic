/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import ngat.net.cil.CilService;
import ngat.rcsgui.stable.CilRow;
import ngat.rcsgui.stable.CilTable;
import ngat.rcsgui.stable.CilTableModel;

/**
 * @author eng
 *
 */
public class CilTableTest {

	
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
			String command = JOptionPane.showInputDialog("Enter command:");		
			// could work out timout here
			CilRow trow = gendata(command);
			ctm.addRow(trow);
			// update...
		}

	}

	CilTableModel ctm;

	public CilTableTest() {

		ctm = new CilTableModel();

		CilTable ctable = new CilTable(ctm);

		JScrollPane jsp = new JScrollPane(ctable);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JFrame f = new JFrame("TCS Command Sender");
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
		// TODO Auto-generated method stub
		CilTableTest ht  = new CilTableTest();
		
	}

	private CilRow gendata(String command) {
	
		CilRow cr = new CilRow();
		cr.message = command;
		cr.sendTime = System.currentTimeMillis();
		cr.elapsedTime = 0;
		
		try {
			// note we record the row number here
			GuiCilResponseHandler crh = new GuiCilResponseHandler(ctm, cr, ii++);		
			CilService cil = (CilService)Naming.lookup("rmi://ltsim1/TCSCilService");
			cil.sendMessage(command, crh, 60000);		
			cr.status = CilRow.SENT;
		} catch (Exception e) { 
			cr.status = CilRow.FAILED;
			e.printStackTrace();
		}
			
		return cr;
	}
}
