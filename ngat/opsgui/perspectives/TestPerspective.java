package ngat.opsgui.perspectives;

import ngat.opsgui.base.Perspective;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFrame;

public class TestPerspective extends Perspective {
    
    static final String[] labels = new String[] 
	{"Astro", "Test", "Mechs", "Log","Meteo","Sched", "Other"};
    
    public  TestPerspective(JFrame f, int n) {
	super(f);
	System.err.println("Create TestPerspective with: "+n+" menus");
	int s = 0;
	int e = 0;
	switch (n) {
	case 1:
	    s = 0;
	    e = 0;
	    break;
	case 2:
	    s = 1;
	    e = 2;
	    break;
	case 3:
	    s = 3;
	    e = 5;
	    break;
	}
	for (int i = s; i <= e; i++) {
	    System.err.println("Add menu: "+i+" "+labels[i]);
	    JMenu m = new JMenu(labels[i]); 
	    m.add(new JMenuItem(labels[i]+".1"));
	    m.add(new JMenuItem(labels[i]+".2"));
	    m.add(new JMenuItem(labels[i]+".3"));
	    m.add(new JMenuItem(labels[i]+".4"));
	    m.add(new JMenuItem(labels[i]+".5"));
	    m.add(new JMenuItem(labels[i]+".6"));
	    menus.add(m);
	}
	
    }

}