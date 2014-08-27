/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

/**
 * @author eng
 *
 */
public class SplashTest {
	
	static SplashScreen mySplash;
	static Graphics2D splashGraphics;
	static Rectangle2D splashTextArea; 
	static Rectangle2D splashProgressArea;
	
	static String[] tasks = new String[] {
		"Configuring...", 
		"Loading user settings...",
		"Contacting telemetry providers...",
		"Setting up services...",
		"...Telescope...",
		"...Instruments...",
		"...Sky Model...",
		"...Meteo...",
		"...Task management...",
		"...Operations...",
		"...Reactive subsystem...",
		"...Scheduler..."
		};
	
	
    public static void main(String[] args)
    {
        splashInit();           // initialize splash overlay drawing parameters
        appInit();              // simulate what an application would do 
                                // before starting
        if (mySplash != null)   // check if we really had a spash screen
            mySplash.close();   // if so we're now done with it
        
        // begin with the interactive portion of the program
    }



    /**
     * Prepare the global variables for the other splash functions
     */
    private static void splashInit()
    {
        mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null)
        {   // if there are any problems displaying the splash this will be null
            Dimension ssDim = mySplash.getSize();
            int height = ssDim.height;
            int width = ssDim.width;
            // stake out some area for our status information
            splashTextArea = new Rectangle2D.Double(15., height*0.88, width * .45, 32.);
            splashProgressArea = new Rectangle2D.Double(width * .55, height*.92, width*.4, 12 );

            // create the Graphics environment for drawing status info
            splashGraphics = mySplash.createGraphics();
            Font font = new Font("Dialog", Font.PLAIN, 14);
            splashGraphics.setFont(font);
            
            // initialize the status info
            splashText("Starting");
            splashProgress(0);
        }
    }


    /**
     * Display text in status area of Splash.  Note: no validation it will fit.
     * @param str - text to be displayed
     */
    public static void splashText(String str)
    {
        if (mySplash != null && mySplash.isVisible())
        {   // important to check here so no other methods need to know if there
            // really is a Splash being displayed

            // erase the last status text
        	
            splashGraphics.setPaint(Color.BLACK);
            splashGraphics.fill(splashTextArea);

            // draw the text
            splashGraphics.setPaint(Color.YELLOW);
            splashGraphics.drawString(str, (int)(splashTextArea.getX() + 10),(int)(splashTextArea.getY() + 15));

            // make sure it's displayed
            mySplash.update();
        }
    }

    /**
     * Display a (very) basic progress bar
     * @param pct how much of the progress bar to display 0-100
     */
    public static void splashProgress(int pct)
    {
        if (mySplash != null && mySplash.isVisible())
        {

            // Note: 3 colors are used here to demonstrate steps
            // erase the old one
            //splashGraphics.setPaint(Color.LIGHT_GRAY);
            //splashGraphics.fill(splashProgressArea);

            // draw an outline
            splashGraphics.setPaint(Color.ORANGE);
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

    /**
     * just a stub to simulate a long initialization task that updates
     * the text and progress parts of the status in the Splash
     */
    private static void appInit()
    {
        for(int i=0;i<=tasks.length;i++)
        {
            int pctDone = i * 10;
            splashText(tasks[i]);
            splashProgress(pctDone);
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                // ignore it
            }
        }
    }

}
