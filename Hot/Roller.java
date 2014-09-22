package Hot;

import java.awt.*;
import java.util.*;
import java.awt.Graphics.*;

// class for Roller
// when it touches the mountain, starts to roll on mountain untill it meets top, then it explodes
public class Roller extends Weapon 
{                                  
    
    private boolean startFly, startExp, startRol;   // three stage: flying, rolling, explosion
    private int toward;                     // rolling toward
    private Parabol parabol;                // flying point
    private Hole hole;                      // hole after explosion

    public Roller(int rf, int re, Color cl, String n) // rf - radius of flying point 
    {                                                 // re - radius of explosion
        super(re, n);                                 // n - name
        hole = new Hole(re, cl);
        parabol = new Parabol(rf, cl);
        startFly = true;
        startExp = false;
        startRol = false;
        toward = 0;                                     // toward of roller (right or left)
    }
    
    public boolean draw(Graphics2D g, Mountain m, Tank[] t, Tank me)   // returns false if the end of shot 
    {                                                                   
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
            int i = 0;
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
                toward = m.getTop(X) - m.getTop(X+1);
                Y = m.getTop(Y);
                if (toward != 0)
                {
                    startRol = true;
                    toward/=Math.abs(toward);
                }
                else
                {
                    startExp = true;
                    hole.start(X, Y);
                }
            } 
        }

        if (startRol)
        {
            int r = parabol.getRadius();
            g.setColor(parabol.getColor());
            g.fillOval(X-r, Y-r, r*2, r*2); 
            X -= toward*1;
            if (X <= 0 || X >= m.getWidth())
            {
                startFly = true;
                startRol = false;
                return false;
            }
            Y = m.getTop(X);

            if (m.getTop(X) < m.getTop(X+toward))
            {
                    startExp = true;
                    startRol = false;
                    hole.start(X, Y);                
            }
            int i = 0;
            while(true)
            {
                try
                {
                    if (t[i].isAlive() && t[i].isPlayed() && t[i].isHit(X, Y - 10) == 1)
                    {
                        startExp = true;
                        startRol = false;
                        hole.start(X, Y);                                    
                    }   
                }
                catch(NullPointerException e)
                {
                    break;
                }
                i++;
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
                        if (t[i].isHit(X, Y, hole.getRadius()) == 1 || t[i].isHit(m) == 1)
                            t[i].setAlive(false);
                    }
                    catch (NullPointerException e)
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
