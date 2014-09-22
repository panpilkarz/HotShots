package Hot;

import java.util.*;
import java.awt.*;
import java.awt.Graphics.*;


public abstract class Weapon             // primary class for Bomb and Roller
{                                 
    public Weapon(int re, String n)
    {
        radiusExp = re;             // how big is the explosion (class Hole)
        name = n;
    }
    
    public abstract boolean draw(Graphics2D g, Mountain m, Tank[] t, Tank me);
    
    public int getCounter()         // amount of weapon
    {
        return counter; 
    }

    public void setCounter(int c) 
    {
        counter = c; 
    }
    
    public void decCounter() 
    {
        counter--; 
    }
    
    public String getName() 
    {
        return name; 
    }
    
    public int getRadius() 
    {
        return radiusExp; 
    }
    
    public int getX()           // horizontal position of flying point
    {
        return X; 
    }
    
    public int getY()           // vertical position of flying point
    {
        return Y; 
    }
        
    public int radiusExp, counter = 0;
    public String name;
    public int  X = 0, Y = 0;
}
