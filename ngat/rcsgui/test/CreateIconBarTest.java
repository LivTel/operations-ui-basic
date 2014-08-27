/**
 * 
 */
package ngat.rcsgui.test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/** Create an iconbar and populate it.
 * @author eng
 *
 */
public class CreateIconBarTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Icon icon1 = new ImageIcon("/home/eng/testgui/arrow-move-icon.png");
		Icon icon2 = new ImageIcon("/home/eng/testgui/Newsfeed-RSS-icon2.png");
		Icon icon3 = new ImageIcon("/home/eng/testgui/shortcut-icon.png");
		
		IconBar bar = new IconBar(8);
		
		JFrame f = new JFrame("Icon bar test");
		f.getContentPane().add(bar);
		f.pack();
		f.setVisible(true);
		
		for (int i = 0; i < 50; i++) {
			int m = i % 3;
			Icon icon = null;
			switch (m) {
			case 0:
				icon = icon1;
				break;
			case 1:
				icon = icon2;
				break;
			case 2:
				icon = icon3;
				break;
			}
			int q = i % 16;
			IconButton btn = new IconButton("BTN"+q, icon, "", "short", "long");
			
			bar.addIcon(btn);
			System.err.println("Add btn.."+q);
			bar.revalidate();
			try {Thread.sleep(2000L);} catch (Exception e) {}
			
			if (Math.random() > 0.7) {
				int p = (int)(Math.random()*q);
				System.err.println("Rem btn.."+p);
				bar.removeIcon("BTN"+p);
				bar.revalidate();
			}
			
		}
		
		
	}

}
