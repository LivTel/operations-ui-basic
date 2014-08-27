package ngat.rcsgui.test;

import javax.swing.*;
import java.awt.*;

public class MollweidePlot extends JPanel {

    public static Dimension SIZE = new Dimension(500,250);
    public static double MIN_DIFF = Math.toRadians(0.1);

    double phi0=Math.toRadians(53.0);
    double l0=Math.toRadians(-20.0);

    public static void main(String args[]) {

	MollweidePlot molly = new MollweidePlot();
	
	JFrame f = new JFrame("Mollweide");
	f.getContentPane().add(molly);
	f.pack();
	f.setVisible(true);

    }
	

    public MollweidePlot() {
	super(true);
    }

    @Override
	public Dimension getPreferredSize() {
	return SIZE;
    }

    @Override
	public void paint(Graphics g) {
	super.paint(g);
	
	// plot the lat lines
	double phi = Math.toRadians(-90.0);
	while (phi <= Math.toRadians(90.0)) {
	    // run along the long line
	    double l = Math.toRadians(-180.0);
	    //double x0 = aitoffx(phi, l);
	    //double y0 = aitoffy(phi, l);
	    double x0 = mollx(phi, l);
	    double y0 = molly(phi, l);
	    while (l <= Math.toRadians(180.0)) {
		//double x = aitoffx(phi, l);
		//double y = aitoffy(phi, l);
		double x = mollx(phi, l);
		double y = molly(phi, l);
		if (! Double.isNaN(x) && !Double.isNaN(y)) {
		    // draw from old to new position
		    plot(g, x0, y0, x, y);
		    x0 = x;
		    y0 = y;
		}
		l += Math.toRadians(1.0);
	    }
	    
	    phi += Math.toRadians(22.5);
	}
	
	// plot the longitude lines
	double l = Math.toRadians(-180.0);
	while (l <= Math.toRadians(180.0)) {
	    
	    // run along the lat line
	    phi = Math.toRadians(-90.0);
	    //double x0 = aitoffx(phi, l);
	    //double y0 = aitoffy(phi, l);
	    double x0 = mollx(phi, l);
	    double y0 = molly(phi, l);
	    while (phi <= Math.toRadians(90.0)) {
		//double x = aitoffx(phi, l);
		//double y = aitoffy(phi, l);
		double x = mollx(phi, l);
		double y = molly(phi, l);
		if (! Double.isNaN(x) && !Double.isNaN(y)) {
		    // draw from old to new position
		    plot(g, x0, y0, x, y);
		    x0 = x;
		    y0 = y;
		}
		phi += Math.toRadians(1.0);
	    }
	    
	    l += Math.toRadians(30.0);
	}

	plotPoint(g, phi0,l0);

    }

    private int screenx(double x) {
	double w = (getSize().width);
	double sx = 0.5*w*(1.0 + x/Math.PI);
	return (int)sx;
    }
    
    private int screeny(double y) {
	double h = (getSize().height);
	double sy = 0.5*h*(1.0 - 2.0*y/Math.PI);
	return (int)sy;
    }

    private void plot(Graphics g, double x0, double y0, double x, double y) {
	
	g.setColor(Color.blue);
	int sx0 = screenx(x0);
	int sy0 = screeny(y0);
	int sx  = screenx(x);
	int sy  = screeny(y);
	g.drawLine(sx0, sy0, sx, sy);

    }
    
    double aitoffx(double phi, double l) {
		double a = Math.acos(Math.cos(phi)*Math.cos(l/2.0));
		double sa = Math.sin(a)/a;
		double x = (2.0*Math.cos(phi)*Math.sin(l/2.0))/sa;
		return x;
    }

     double aitoffy(double phi, double l) {
		double a = Math.acos(Math.cos(phi)*Math.cos(l/2.0));
		double sa = Math.sin(a)/a;
		double y = Math.sin(phi)/sa;
		return y;
    }

    double mollx(double phi, double l) {
	double theta = theta(phi);
	double x = 2.0*Math.sqrt(2.0)*l*Math.cos(theta)/Math.PI;
	return x;
    }
    double molly(double phi, double l) {
	double theta = theta(phi);
	double y = Math.sqrt(2.0)*Math.sin(theta);
	return y;
    }

    double theta(double phi) {
	if (phi == 0.5*Math.PI)
	    return 0.5*Math.PI;
	if (phi == -0.5*Math.PI)
	    return -0.5*Math.PI;

	//System.err.println("Compute theta for: "+Math.toDegrees(phi));
	int  i = 0;
	double thetan = phi;
	double thetanp = thetan;
	do {
	    thetanp = thetan -  (2*thetan+Math.sin(2.0*thetan)-Math.PI*Math.sin(phi))/(2.0 + 2.0*Math.cos(2.0*thetan));	    
	    //System.err.println("Iterate: "+i+" "+thetan+" "+thetanp);
	    i++;
	    thetan = thetanp;
	} while (Math.abs(thetanp - thetan) > MIN_DIFF);
	//System.err.println("ok; "+thetanp);
	return thetanp;
    }

    private void plotPoint(Graphics g, double phi, double l) {

	double x = mollx(phi,l);
	double y = molly(phi, l);

	int sx = screenx(x);
	int sy = screeny(y);

	g.setColor(Color.red);
	g.fillOval(sx-5,sy-5, 10,10);

    }
    

}
