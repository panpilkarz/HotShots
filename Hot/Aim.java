package Hot;

import java.util.*;
import java.awt.*;

// class Aim is declared in Cannon

//   |
//-------
//   |

public class Aim
{ 
    private final int length = 50;  // how big is an aim
    private int X = 0, Y = 0;
    private Color color;    

    public Aim(Color c)
    {
        color = c;
    }
    
    public int getX()           // returns aim horizontal position
    {
        return X; 
    }
    
    public int getY()           // returns aim vertical position 
    {
        return Y;
    }
    
    public void setX(int n)     // sets aim horizontal position 
    {
        X = n; 
    }

    public void setY(int n)     // sets aim vertical position 
    {
        Y = n; 
    }
    
    public int getLength()      
    {
        return length; 
    }

    public Color getColor()         
    {
        return color; 
    }
}
