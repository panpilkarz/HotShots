package Hot;

import java.awt.*;
import java.util.*;
import java.awt.Graphics.*;

// object of flying point
public class Parabol                                           
{
    private int X, Y, X0, Y0, V0;   // physical parametres of fly
    private int radius;             // radius of flying point
    private boolean active;
    private double Vx, Vy, t;       // physical parametres of fly
    private final double GRW = -10, DT = 0.05;    // GRW - gravitation
    private Color color;                            // DT - time

    public Parabol(int r, Color c)
    {
        radius = r;                                         
        color = c;
        active = false;
    }
        
    public void start(int x0, int y0, int p, int a)         // sets initial values
    {
        X = Y = 0;
        X0 = x0;
        Y0 = y0;
        V0 = p;
        Vx = (double)V0/10*Math.cos(a*Math.PI/180)*15;      // Vx - horizontal speed
        Vy = (double)V0/10*Math.sin(a*Math.PI/180)*15;      // Vy - vertical speed
        t = 0;
        active = true;
    }

    public void start(Tank t)
    {
        start(t.cannon.getX(), t.cannon.getY(), t.getPower(), t.getAngle());
    }

    public void stop()              
    {
        active = false;       
    }

    public void draw(Graphics2D g)          // this is working when gamePnale is repainted
    {                                       // position of point is changed by time t
        if (active)
        {
            X = (int)(Vx * t);
            Y = (int)(Vy * t + GRW*t*t/2);
            g.setColor(color);
            if (Y0-Y-radius < 1) 
                g.fillOval(X0-X-radius, 0, radius*2, radius*2);            
            else
                g.fillOval(X0-X-radius, Y0-Y-radius, radius*2, radius*2);            
            
            t += DT;
        }
    }
        
    public int isOutside(Mountain m)        // out of the game area
    { 
        if (X0-X <= 0 || X0-X > m.getWidth() || Y0-Y > m.getHeight())
        {
            X = Y = X0 = Y0 = 0;
            return 1;
        } 
        return 0;
    }
    
    public boolean isActive() 
    {
        return active; 
    }
    
    public Color getColor() 
    {
        return color; 
    }
    
    public int getRadius() 
    {
        return radius; 
    }
    
    public int getX() 
    {
        return X; 
    }
    
    public int getY() 
    {
        return Y; 
    }
    
    public int getX0() 
    {
        return X0; 
    }
    
    public int getY0() 
    {
        return Y0; 
    }
}
