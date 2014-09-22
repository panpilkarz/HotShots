package Hot;

import java.awt.*;
import java.util.*;

// cannon is displayed above the tank
public class Cannon                    
{        
    private final int length = 10;  // how long is the cannmon
    private int X = 0, Y = 0;
    private boolean active;         // has tank a round ?
    private Color color;
    public Aim aim;                 

    public Cannon(Color c)
    {
        color = c;
        active = false;
        aim = new Aim(c);       
    }
    
    public int getX()           // returns connon's horizontal position
    {
        return X; 
    }
    
    public int getY()           // returns connon's vertical position 
    {
        return Y;
    }
    
    public void setX(int n)     // sets connon's horizontal position 
    {
        X = n; 
    }

    public void setY(int n)     // sets connon's vertical position 
    {
        Y = n; 
    }
    
    public int getLength()      
    {
        return length; 
    }
        
    public void setActive(boolean b)
    {
        active = b; 
    }

    public boolean getActive()
    {
        return active; 
    }

    public Color getColor()         
    {
        return color; 
    }
}
