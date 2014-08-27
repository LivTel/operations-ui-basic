/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.plaf.DesktopIconUI;

/**
 * @author eng
 *
 */
public class DektopTest {


	public static class MyUi extends DesktopIconUI {

		/* (non-Javadoc)
		 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
		 */
		@Override
		public void paint(Graphics g, JComponent c) {		
			super.paint(g, c);
			g.setColor(Color.red);
			g.fillOval(0,0,5,5);
			
		}

	}

	/**
	 * @author eng
	 *
	 */
	public static class TestFrame extends JInternalFrame {
		
		String title;
		Icon icon;
	
		DesktopIconUI UI = new MyUi();
		
		/**
		 * @param title
		 * @param icon
		 */
		public TestFrame(Icon icon) {
			super("", true, true, true, true);
			this.title = title;
			this.icon = icon;
			setFrameIcon(icon);
			getDesktopIcon().setUI(UI);
			// add a jbutton
			add(new JButton("title"));
			setBounds(50, 50, 200, 100);
			setVisible(true);
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		SystemTray tray = SystemTray.getSystemTray();
		boolean hastray = SystemTray.isSupported();
		TrayIcon[] icons = tray.getTrayIcons();
		
		System.err.println("System tray supported: "+hastray);
		System.err.println("Icons are: "+icons);
		
		JFrame frame = new JFrame("Test");
		int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);


		JDesktopPane desk = new JDesktopPane();
		desk.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		JInternalFrame f1 = new TestFrame(new ImageIcon("/occ/tmp/icon1.gif"));
		JInternalFrame f2 = new TestFrame(new ImageIcon("/occ/tmp/icon2.gif"));
		JInternalFrame f3 = new TestFrame(new ImageIcon("/occ/tmp/icon3.gif"));
		
		desk.add(f1);
		desk.add(f2);
		desk.add(f3);

		frame.setContentPane(desk);
		frame.setVisible(true);
		
	}

}
