/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.ScrollPaneConstants;

import ngat.sms.FeasibilityPrescanController;
import ngat.sms.util.PrescanEntry;
import ngat.smsgui.FeasibilityTable;
import ngat.smsgui.FeasibilityTableModel;

/**
 * @author eng
 * 
 */
public class FeasibilityTablePanel extends JPanel {

	private FeasibilityTableModel ftm;

	private JButton prescanBtn;

	private ProgressMonitor progress;

	private JTextField totalGroupsField;

	private JTextField totalField;

	public FeasibilityTablePanel() {
		super(true);

		ftm = new FeasibilityTableModel();

		FeasibilityTable ftab = new FeasibilityTable(ftm);

		JScrollPane jspFeas = new JScrollPane(ftab);
		jspFeas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel top = new JPanel(true);
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		prescanBtn = new JButton("Prescan");
		prescanBtn.setBackground(Color.red);
		prescanBtn.setForeground(Color.cyan);
		prescanBtn.addActionListener(new PrescanListener(this));
		top.add(prescanBtn);

		top.add(new JLabel("Groups"));
		totalGroupsField = new JTextField(8);
		top.add(totalGroupsField);

		top.add(new JLabel("Predicted total"));
		totalField = new JTextField(8);
		top.add(totalField);

		setLayout(new BorderLayout());
		add(jspFeas, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
	}

	public void clearTable(int ngc) {
		ftm.clear();
		clearExecTotal();
		setTotalGroups(ngc);
		if (progress != null) {
			progress.setMaximum(ngc);
		}
	}

	public void addEntry(PrescanEntry pse) {
		ftm.addEntry(pse);
		if (progress != null) {
			progress.setProgress(ftm.getRowCount());
			progress.setNote("Processed " + ftm.getRowCount() + " groups");
		}
	}

	public void clearExecTotal() {
		totalField.setText("");
	}

	public void clearGroupTotal() {
		totalGroupsField.setText("");
	}

	public void setExecTotal(double total) {
		totalField.setText(String.format("%6.2f h", (total / 3600000.0)));
	}

	public void setTotalGroups(int total) {
		totalGroupsField.setText(String.format("%6d", total));
	}

	private class PrescanListener implements ActionListener {

		private FeasibilityTablePanel ftp;

		PrescanListener(FeasibilityTablePanel ftp) {
			this.ftp = ftp;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {

			String password = null;
			JPasswordField passwordField = new JPasswordField(10);
			passwordField.setEchoChar('*');
			Object[] obj = { "Please enter the password:\n\n", passwordField };
			Object stringArray[] = { "OK", "Cancel" };
			
			int reply = JOptionPane.showOptionDialog(ftp, 
													obj, 
													"Prescan Authorization", 
													JOptionPane.YES_NO_OPTION,
													JOptionPane.QUESTION_MESSAGE,
													null, 
													stringArray, 
													obj);
				password = new String(passwordField.getPassword());
			if (!password.equals("flaming%golah")) {
				JOptionPane.showMessageDialog(ftp, "You are not authorized to run prescan");
				return;
			}

			Runnable r = new Runnable() {

				@Override
				public void run() {
					try {

						FeasibilityPrescanController fpc = (FeasibilityPrescanController) Naming
								.lookup("rmi://localhost/FeasibilityPrescanner");

						progress = new ProgressMonitor(ftp, "Scanning", "Feasibility Prescan", 1, 100);

						List candidates = fpc.prescan(System.currentTimeMillis(), 60000);

						progress.close();

						long start = 0L;
						long end = 0L;

						double exectot = 0.0;
						Iterator ig = candidates.iterator();
						while (ig.hasNext()) {
							PrescanEntry pse = (PrescanEntry) ig.next();
							start = pse.start;
							end = pse.end;
							exectot += pse.execTime;
						}

						// System.err.printf("Total available exectime: %tF : %4.2f H\n",
						// time, (exectot / 3600000.0));

						// the results are now back,
						// now lets do a contention scan
						int nn = (int) ((end - start) / 60000) + 1;
						double[] cc = new double[nn];
						Iterator ic = candidates.iterator();
						while (ic.hasNext()) {
							PrescanEntry pse = (PrescanEntry) ic.next();
							double xt = pse.execTime;
							double gw = pse.feasibleTotal() / pse.nx;
							double cg = xt / (xt + gw);
							for (int it = 0; it < nn; it++) {
								long t = start + it * 60000L;
								if (pse.isFeasible(t))
									cc[it]++;
								// cc[it] += cg;
							}
						}

						ftp.setExecTotal(exectot);

						// plot contention
						// for (int it = 0; it < nn; it++) {
						// long t = start + it * 60000L;
						// System.err.printf("CC %tF %tT %4.2f \n", t, t,
						// cc[it]);
						// tsContentionPredict.add(new Second(new Date(t)),
						// cc[it]);
						// }

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			(new Thread(r)).start();
		}

	}
}
