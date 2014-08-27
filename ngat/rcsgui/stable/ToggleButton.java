package ngat.rcsgui.stable;

import javax.swing.*;
import java.awt.*;

public class ToggleButton extends JButton {

  public ToggleButton(Action action) {
    super(action);
 
    // These statements enlarge the button so that it 
    // becomes a circle rather than an oval.
    Dimension size = getPreferredSize();
    size.width = size.height = Math.max(size.width/2, 
      size.height/2);
    setPreferredSize(size);

    // This call causes the JButton not to paint 
    // the background.
    // This allows us to paint a round background.
    setContentAreaFilled(false);
  }

  // Paint the round background and label.
  @Override
protected void paintComponent(Graphics g) {
    if (getModel().isArmed()) {
      // You might want to make the highlight color 
      // a property of the RoundButton class.
      g.setColor(Color.lightGray);
    } else {
      g.setColor(getBackground());
    }
    
    int w = getSize().width;
    int h = getSize().height;

    int x3Points[] = {0, w/4, 3*w/4, w, 3*w/4, w/4};
    int y3Points[] = {h/2, 0, 0, h/2, h, h};

    g.fillPolygon(x3Points, y3Points, x3Points.length); 

    // This call will paint the label and the 
    // focus rectangle.
    super.paintComponent(g);
  }

  // Paint the border of the button using a simple stroke.
  @Override
protected void paintBorder(Graphics g) {
    g.setColor(getForeground());
 
    int w = getSize().width;
    int h = getSize().height;
    
    int x3Points[] = {0, w/4, 3*w/4, w, 3*w/4, w/4};
    int y3Points[] = {h/2, 0, 0, h/2, h, h};
    g.drawPolygon(x3Points, y3Points, x3Points.length); 
  }

  // Hit detection.
  Polygon polygon;
  @Override
public boolean contains(int x, int y) {
    // If the button has changed size, 
    // make a new shape object.
    if (polygon == null || 
      !polygon.getBounds().equals(getBounds())) {
    
	int w = getSize().width;
	int h = getSize().height;
	
	int x3Points[] = {0, w/4, 3*w/4, w, 3*w/4, w/4};
	int y3Points[] = {h/2, 0, 0, h/2, h, h};
	
      polygon = new Polygon(x3Points,y3Points,3);
    }
    return polygon.contains(x, y);
  }


}