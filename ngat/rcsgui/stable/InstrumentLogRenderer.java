package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import ngat.rcsgui.test.TimeScale;

public class InstrumentLogRenderer extends JPanel{

	private String name;
	//private long period;
	
	private TimeScale scale;
	
	private List<InstrumentData> data;
	
	public InstrumentLogRenderer(String name, TimeScale scale) {
		super(true);
		this.name = name;
		this.scale = scale;
		data = new Vector<InstrumentData>();
	}
	
	public void addItem(InstrumentData item) {
		data.add(item);
		System.err.println("ILR: "+name+" add item: "+item.state);
		// move the scale forwards a tad.
		scale.advance(item.time);
	}
	
	public List<InstrumentData> listItems() {
		return data;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {	
		super.paint(g);
		//System.err.println("ILR: "+name+"repaint");
		
		//long now = System.currentTimeMillis();
		
		// allow 100 for name
		g.setColor(Color.black);
		g.drawString(name, 10, 15);
		
		// loop along x axis by log entry
	
		int w = getSize().width;
		int h = getSize().height;
		
		g.setColor(Color.gray.brighter());
		g.fillRect(100, 5, w-100, 25);
		
		long start = scale.getStart();
		long end = scale.getEnd();
		long period = end-start;
		
		int block1 = Math.max(1, (int)( (w-100)*60000.0 / (period)));
		
		int oldatx = 100; // first block
		
		//System.err.println("ILR: "+name+" contains: "+data.size()+" entries");
		for (int i = 0; i < data.size(); i++) {
			InstrumentData item = data.get(i);
			long time = item.time;
			int state = item.state;
			
			Color color = Color.BLACK;
			switch (state) {
			case InstrumentData.OFFLINE:
				color = Color.BLUE;
				break;
			case InstrumentData.ONLINE_OKAY:
				color = Color.GREEN;
				break;
			case InstrumentData.ONLINE_WARN:
				color = Color.ORANGE;
				break;
			case InstrumentData.ONLINE_FAIL:
				color = Color.RED;
				break;
			case InstrumentData.DISABLED:
				color = Color.pink;
			}
			g.setColor(color);
			
			// ignore data more than period old
			if (time < start)
				continue;
		
			// work out x location and width					
			int atx = 100 + (int)((double)(w-100)*(double)(time - start)/ (period));
			int block = (i == 0 ? block1 : atx - oldatx);
			g.fillRect(atx, 5, block, 25);
						
			oldatx = atx;
		}
		
		// fill the rest with black
	//	if (latest < end) {
		//	int atx = 100 + (int)((double)(w-100)*(double)(latest-start)/ (double)(end-start));
		//	g.setColor(Color.BLACK);
		//	g.fillRect(atx, 5, w-atx, 25);
		//}
			
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(500, 35);
	}

	
}
