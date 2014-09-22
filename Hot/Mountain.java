package Hot;

import java.awt.*;
import java.util.*;

// object of mountain terrain, terrain is build by vertical lines
public class Mountain                                      
{   
    private static int width, height;               // width and height of mountain area
    private static int[] top = new int[2000];       // length of each vertical line
    private static int[] color = new int[2000];     // color of each vertical line
                                                    // 2000 - maximal horizontal screen resolution
    public Mountain(int w, int h)
    {
       width = w;
       height = h;
    }

    public void generate(Tank[] t)               // genrates new mountains,
    {                                            // sets new position for tanks
       setColor();
       int i = 0, pl = 0;
       int[] b = new int[10];                    // take played tanks
       while(true)
       {
            try
            {
                if (t[i].isPlayed())
                {
                    b[pl] = i;
                    pl++;
                }
            }
            catch(NullPointerException e)
            {
                break;
            }
            i++;
       }
       int a = 0, x = 0, j = 0;
       int k = (width/pl), minH, maxH;             
       i = 0;
       top[0] = (int)(Math.random() * height/4) + height/2;     
       while (true)
       {
          maxH = (int)(Math.random() * height/2) + 50;  // maximum of mountain height
          minH = (int)(Math.random() * height/4) + 100; // maximum of mountain width
          if (a != 0  && (x > j*k + Math.random()*k || x > width - t[0].getWidth() && j < pl))
          {
                a = 0;                                  // sets new tank's location
                t[b[j]].setLocation(x, top[i] - t[0].getHeight());
                j++;
                x += t[0].getWidth();
          } 
          else
          {
              do  
                a = (int)(Math.random()*4) - 2; 
              while (a == 0);
              x += (int)(Math.random() * width/4/pl) + 30;
          }
          while (true)
          {
              i++;
              top[i] = top[i-1] - a;
              if (i > x) break;
              if ((top[i] < maxH && a > 0) || 
                  (top[i] > height-minH && a < 0) || 
                  (x > width - t[0].getWidth() && j < pl))
                  {   
                      x = i;
                      break;
                  }
          }
          if (x > width) break;
       }
    }
    
    public void setColor()                              // colors are changed to make a shadow
    {
        int d = 1;
        color[0] = (int)(Math.random()*100) + 120;
        for (int i=1; i<width; i+=3)
        {           
            color[i] = color[i-1];
            color[i+1] = color[i-1];
            color[i+2] = color[i-1] + d;
            if (color[i+2] < 120 || color[i+2] > 220) d*=-1; 
        }
    }
    
    public void refresh(int x0, int y0, int r)           // refresh mountains after explosion (hole)
    {
        int i, k;
        double x, y;
        for (i = x0-r; i <= x0+r; i++)
        {
            if (i < 0) continue;                        // out of the table
            x = Math.abs(x0 - i);
            y = r*Math.sqrt(1 - (x*x)/(r*r));
            if (Double.isNaN(y)) y = 0;
            k = (int)(y0 + y) - top[i];
            if (k < 0) continue;
            if (k > 2*y) top[i]+=(int)(2*y); else top[i]+=k;
        }        
    }

    public int isHit(int x0, int y0)                    // mountains are hit by parabol
    {
        if (x0 >= 0 && y0 < top[x0]) return 0;
        return 1;
    }

    public static int getTop(int i)                     // returns a height of piece of mountain
    {
        return top[i]; 
    }

    public static int getColor(int i)                   // returns a color of piece of mountain
    {
        return color[i]; 
    }
            
    public static int getWidth() 
    {
        return width; 
    }    
    
    public static int getHeight() 
    {
        return height; 
    }
    
    public static void setHeight(int h) 
    {
        height = h; 
    }
}
