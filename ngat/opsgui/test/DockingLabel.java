package ngat.opsgui.test;

import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.awt.Dimension;

public class DockingLabel extends JLabel {

	private DockingManager dm;

	private ComponentDescriptor componentDescriptor;

	public DockingLabel(DockingManager dm, ComponentDescriptor componentDescriptor) {
		super();
		if (componentDescriptor.getLabelIcon() != null)
			setIcon(componentDescriptor.getLabelIcon());
		else
			setText(componentDescriptor.getLabelText());

		this.dm = dm;
		this.componentDescriptor = componentDescriptor;
		setOpaque(false);
		addMouseListener(new UndockingListener());
	}

	private class UndockingListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			int nclick = e.getClickCount();

			if (nclick == 1) {
				// select the tab
				// int index = tabs.getSelectedIndex();
				dm.selectComponent(componentDescriptor);
			} else if (nclick == 2) {
				JFrame f = dm.detach(componentDescriptor);
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				int xx = (int) (0.75 * screen.width * Math.random());
				int yy = (int) (0.5 * screen.height * Math.random());
				f.setBounds(xx, yy, 600, 400);
				f.setVisible(true);
			}
		}

	}

}