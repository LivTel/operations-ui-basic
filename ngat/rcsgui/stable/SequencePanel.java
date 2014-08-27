/**
 * 
 */
package ngat.rcsgui.stable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import ngat.phase2.ISequenceComponent;

/**
 * @author eng
 *
 */
public class SequencePanel extends JPanel {

	JTextArea seqText;
	
	public SequencePanel() {
		super(true);
		seqText = new JTextArea(20,45);
		JScrollPane jsp = new JScrollPane(seqText,
						   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp);

	}
	
	public void update(ISequenceComponent root){		
		seqText.setText(ngat.rcs.sciops.DisplaySeq.display(0,root));
	}
	
}
