package Hot;

import java.awt.*;
import java.util.*;
import java.awt.Graphics.*;

// class for Missile and Nuke 
// when it touches the mountain, it explodes
public class Bomb extends Weapon
{                                                   
    private boolean startFly, startExp; // two stage: flying, explosion
    private Parabol parabol;            // flying point
    private Hole hole;                  // hole after explosion

    public Bomb(int rf, int re, Color cl, String n) // rf - radius of flying point 
    {                                               // re - radius of explosion
        super(re, n);                               // n - name
        hole = new Hole(re, cl);
        parabol = new Parabol(rf, cl);
        startFly = true;
        startExp = false;
    }
    
    public boolean draw(Graphics2D g, Mountain m, Tank[] t, Tank me)   
    {                                     // returns false if the end of shot                                
        if (startFly)
        {
            parabol.start(me);
            startFly = false;
        }
        
        if (parabol.isActive()) 
        {
            parabol.draw(g);
            X = parabol.getX0()-parabol.getX();
            Y = parabol.getY0()-parabol.getY();                    
            int i=0;
            while(true)
            {
                try
                {
                    if (t[i].isAlive() && t[i].isPlayed() && t[i].isHit(X, Y) == 1)
                    {
                        parabol.stop();
                        startExp = true;
                        hole.start(X, Y);
                    }
                }
                catch(NullPointerException e)
                {
                    break;
                }
                i++;
            }    
            if (parabol.isOutside(m) == 1)
            {
                parabol.stop();
                startFly = true;
                return false;
            } 
            else
            if (m.isHit(X, Y) == 1)
            {
                parabol.stop();
                startExp = true;
                Y = m.getTop(X);
                hole.start(X, Y);
            } 
        }
        
        if (startExp)
        {            
            hole.draw(g);
            if (!hole.isActive())
            {
                startFly = true;
                startExp = false;
                m.refresh(X, Y, hole.getRadius());
                int i = 0;
                while(true)
                {
                    try
                    {
                        if (t[i].isAlive() && t[i].isPlayed())
                        if (t[i].isHit(X, Y, hole.getRadius()) == 1 ||t[i].isHit(m) == 1)
                            t[i].setAlive(false);
                    }
                    catch(NullPointerException e)
                    {
                        break;
                    }
                    i++;
                }
                return false;
            }
        }
        return true;
    }
}
