/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import ngat.sms.Disruptor;

/**
 * @author eng
 * 
 */
public class DisruptorPanel extends TimeCategoryPanel {

	private List<Disruptor> dlist;

	/**
	 * @param container
	 */
	public DisruptorPanel(TimePanel container, List<Disruptor> dlist) {
		super(container);
		this.dlist = dlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.smsgui.TimeCategoryPanel#renderGraphics(java.awt.Graphics,
	 * long, long)
	 */
	@Override
	protected void renderGraphics(Graphics g, long start, long end) {

		int ns = getSize().width;
		long step = (end - start) / ns;

		double xscale = getSize().getWidth() / (end - start);
		long t = start;
		while (t < end) {
			int ic = 0;
			for (int i = 0; i < dlist.size(); i++) {
				Disruptor d = dlist.get(i);
				if (d.getPeriod().contains(t))
					ic++;
			}

			switch (ic) {
			case 0:
				g.setColor(Color.black);
				break;
			case 1:
				g.setColor(Color.magenta);
				break;
			case 2:
				g.setColor(Color.magenta.brighter());
				break;
			case 3:
				g.setColor(Color.magenta.brighter().brighter());
				break;
			case 4:
				g.setColor(Color.red);
				break;
			case 5:
				g.setColor(Color.red.brighter());
				break;
			default:
				g.setColor(Color.red.brighter().brighter());
				break;
			}

			int x = (int) (xscale * (t - start));
			g.fillRect(x, 5, 1, getSize().height - 5);
			t += step;
		}
		
	}
}
