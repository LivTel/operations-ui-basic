/**
 * 
 */
package ngat.rcsgui.stable;

import javax.swing.JOptionPane;

/** Shows a splash screen and tells the user the rcsgui is now defunct.
 * @author eng
 *
 */
public class RcsGuiDeprecatedSplash {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Object[] options = {"OPS UI",
                "Cancel",
                "RcsGui"};
		int n = JOptionPane.showOptionDialog(null,
					"The RCSGui has now become obsolete\n"+
					"...just like the old Isuzu Trooper ...\n\n"+
					"You can still use it if you\n"+
					"really desperately want to...",		
					"Start Engineering UI",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);

		
		System.exit(n);
		
	}

}
