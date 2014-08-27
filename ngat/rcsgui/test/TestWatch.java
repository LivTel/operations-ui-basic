package ngat.rcsgui.test;

import javax.swing.JFrame;

import ngat.phase2.XGroup;
import ngat.sms.GroupItem;

public class TestWatch {

	private String groupName;
	private GroupWatchDisplayPanel p;
	/**
	 * @param groupName
	 */
	public TestWatch(String groupName) {
		super();
		this.groupName = groupName;
	}

	public void display(GroupItem group) {
		JFrame f = new JFrame("Watch: "+groupName);
		p = new GroupWatchDisplayPanel(group, System.currentTimeMillis(), System.currentTimeMillis()+3*3600*1000L);
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
	}
	
	public void startUpdater() {
		double s = Math.random()*5.0;
		double ws = s;
		while (true) {
			long dt = 30000 + (long)(Math.random()*80000.0);
			try  {Thread.sleep(dt);} catch (Exception e) {}
			boolean selected = (Math.random()>0.95);
			s += (Math.random()-0.5)*0.4;
			if (s > 5.0)
				s /=2.0;
			if (! selected)
				ws = s + Math.random()*0.5;
			p.addWatchEntry(System.currentTimeMillis(), selected, s, "", ws);			
		}
		
	}
	
	public static void main(String args[]) {
		
		TestWatch t = new TestWatch(args[0]);
		XGroup group = new XGroup();
		GroupItem groupItem = new GroupItem(group, null);
		t.display(groupItem);
		t.startUpdater();

	}
	
	
}
