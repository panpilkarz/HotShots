package Hot;

import java.awt.*;
import java.util.*;
import java.awt.Graphics.*;

// object makes hole in the mountain, used in explosion
public class Hole                  
{   
    private Color color;
    private boolean active = false;
    private int X = 0, Y = 0;
    private int radius, progres = 0;    // radius of explosion
                                        // the end of explosion when progres = radius
    public Hole(int r, Color c)
    {
        radius = r;
        color = c;
    }

    public void start(int x, int y)     // sets initial posiotion
    {
        X = x;
        Y = y;
        progres = 0;
        active = true;
    }

    public void draw(Graphics2D g)      // draw oval on radius = progress
    {
        if (active)
        {
            progres++;
            g.setColor(color);
            g.fillOval(X-progres, Y-progres, progres*2, progres*2); 
            if (progres == radius) 
                active = false;
        }
    }
    
    public boolean isActive() 
    {
        return active; 
    }
    
    public int getRadius() 
    {
        return radius; 
    }
}
