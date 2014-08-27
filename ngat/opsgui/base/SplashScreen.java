/**
 * 
 */
package ngat.opsgui.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/** A wrapper for the splash screen during startup.
 * @author eng
 *
 */
public class SplashScreen {

	private java.awt.SplashScreen mySplash;
	
	private Graphics2D splashGraphics;
	private Rectangle2D splashTextArea; 
	//private Rectangle2D splashHeaderArea; 
	private Rectangle2D splashProgressArea;
	
	/**
     * Prepare the global variables for the other splash functions
     */
    public void splashInit()
    {
        mySplash = java.awt.SplashScreen.getSplashScreen();
        if (mySplash != null)
        {   // if there are any problems displaying the splash this will be null
            Dimension ssDim = mySplash.getSize();
            int height = ssDim.height;
            int width = ssDim.width;
            // stake out some area for our status information
            splashTextArea = new Rectangle2D.Double(15.0, height*0.88, width * 0.45, 32.0);
            splashProgressArea = new Rectangle2D.Double(width * 0.55, height*0.92, width*0.4, 12 );

            // stake out an area for the headers
            //splashHeaderArea = new Rectangle2D.Double(35.0, height*0.1, width * 0.95, 62.0);
            
            // create the Graphics environment for drawing status info
            splashGraphics = mySplash.createGraphics();
            
            // Header font
            Font headerFont = new Font("Palatino", Font.BOLD | Font.ITALIC, 36);
            splashGraphics.setFont(headerFont);
            
            splashHeaderText("LT Operations Interface");
            
            Font font = new Font("Palatino", Font.BOLD, 16);
            splashGraphics.setFont(font);
            
            // initialize the status info
            splashText("Starting");
            splashProgress(0);
        }
    }
    
    private void splashHeaderText(String text) {
    	   if (mySplash != null && mySplash.isVisible())
           {   // important to check here so no other methods need to know if there
               // really is a Splash being displayed

               // erase the last status text
           	
               // draw the text
               splashGraphics.setPaint(Color.BLUE);
               splashGraphics.drawString(text, 65, 65);

               // make sure it's displayed
               mySplash.update();
           }
    }
    
    /**
     * Display text in status area of Splash.  Note: no validation it will fit.
     * @param str - text to be displayed
     */
    public void splashText(String str)
    {
        if (mySplash != null && mySplash.isVisible())
        {   // important to check here so no other methods need to know if there
            // really is a Splash being displayed

            // erase the last status text
        	
            splashGraphics.setPaint(Color.BLACK);
            splashGraphics.fill(splashTextArea);

            // draw the text
            splashGraphics.setPaint(Color.GREEN);
            splashGraphics.drawString(str, (int)(splashTextArea.getX() + 10),(int)(splashTextArea.getY() + 15));

            // make sure it's displayed
            mySplash.update();
        }
    }

    /**
     * Display a (very) basic progress bar
     * @param pct how much of the progress bar to display 0-100
     */
    public void splashProgress(int pct)
    {
        if (mySplash != null && mySplash.isVisible())
        {

            // Note: 3 colors are used here to demonstrate steps
            // erase the old one
            //splashGraphics.setPaint(Color.LIGHT_GRAY);
            //splashGraphics.fill(splashProgressArea);

            // draw an outline
            splashGraphics.setPaint(Color.BLUE);
            splashGraphics.draw(splashProgressArea);

            // Calculate the width corresponding to the correct percentage
            int x = (int) splashProgressArea.getMinX();
            int y = (int) splashProgressArea.getMinY();
            int wid = (int) splashProgressArea.getWidth();
            int hgt = (int) splashProgressArea.getHeight();

            int doneWidth = Math.round(pct*wid/100.f);
            doneWidth = Math.max(0, Math.min(doneWidth, wid-1));  // limit 0-width

            // fill the done part one pixel smaller than the outline
            splashGraphics.setPaint(Color.GREEN);
            splashGraphics.fillRect(x, y+1, doneWidth, hgt-1);

            // make sure it's displayed
            mySplash.update();
        }
    }
    
    public void close() {
    	 if (mySplash != null)   // check if we really had a spash screen
	            mySplash.close();   // if so we're now done with it
    }

}
