package Hot;

import java.awt.*;
import java.util.*;

// class reprezents player, is declared in GamePanel
public class Tank                                          
{                                                   
    private static final int width = 30, height = 15;   // dimension of tank, picture should have the same
    private int power = 40, angle = 60;                 // settings
    private int wI = 0;                                 // wI - weapon index
    private int score = 0;                              // how many score has player
    private int X = 0, Y = 0;                           // position of tank
    private boolean play = false;                       // if tank was setected to game
    private boolean shot = false;                       // if tank is shoting
    private boolean alive = false;                      // if time is still in game
    public Weapon[] weapon = new Weapon[3];             // table of weapon object
    public Cannon cannon = new Cannon(Color.gray);      // object cannon
    private Image image;                                // picture of tank                       
    private String name;                                // name of tank
    
    public Tank(String fileName)                    
    {                                               
        image = Toolkit.getDefaultToolkit().createImage(getClass().getResource(fileName));      
        name = fileName;
        weapon[0] = new Bomb(2, 10, Color.lightGray, "missile");     // missile; 2-parabolradius, 10-holeradius
        weapon[1] = new Bomb(3, 30, Color.red, "nuke");              // nuke; 3-parabol radius, 50-hole radius
        weapon[2] = new Roller(2, 10, Color.orange, "roller");       // roller; 3-parabol radius, 50-hole radius
    }
 
    public int isHit(int x0, int y0)                    // tank is hit by parabol (flight)
    {
        if (x0 > X && x0 < X+width && y0 > Y && y0 < Y+height) return 1;
        return 0;
    }

    public int isHit(int x0, int y0, int r)             // tank is hit by hole (explosion),
    {                                                   // because is within the hole
        int i, k;
        double x, y;
        for (i = x0-r; i <= x0+r; i++)
        {
            if (i < 0) continue;                        // out of the table
            x = Math.abs(x0 - i);
            y = r*Math.sqrt(1 - (x*x)/(r*r));
            if (Double.isNaN(y)) y = 0;
            if (i > X && i < X+width)
            if ((y0+y < Y+height && y0+y > Y) || (y0-y < Y+height && y0-y > Y)) return 1;
        }    
        return 0;
    }

    public int isHit(Mountain m)                        // no mountain under the tank, so
    {                                                   // tank is falling and must die
        int i, k = 0;
        for (i = X; i <= X+width; i++)
        {
            if (m.getTop(i) > Y+height) k++;
            if (k > width/2) return 1;
        }
        return 0;
    }
    
 //****************************************************************************   
 // methods for weapon   
    public void nextWeapon()                            // returns next available weapon
    {
        while (true)
        {   
            wI++;
            if (wI%3 == 0) wI = 0;                      // 3 - amount of implemented weapon
            if (weapon[wI].getCounter() > 0) break;
        }
    }

    public void decWeapon()                             // decreases amount of weapon
    {
        weapon[wI].decCounter(); 
    }

    public int getWeapon()                              // returns selected weapon
    {
        return wI; 
    }

    public void setWeapon(int n)                        // sets which weapon is selected
    {
        wI = n; 
    }

    public int getWeaponCounter()                       // returns amount of weapon
    {
        return weapon[wI].getCounter(); 
    }
    
    public String getWeaponLabel()                      // returns weapon name + weapon counter
    {
        return weapon[wI].getName() + ": " + weapon[wI].getCounter();
    }

 //****************************************************************************
 // methods for score   
    public void incScore()                              // increase score
    {
        score++; 
    }
    
    public void decScore()                              // decrease score
    {
        score--; 
    }
    
    public int getScore()                               // returns score
    {
        return score; 
    }
    
    public void setScore(int s)                         // sets scroe
    {
        score = s; 
    }

//*****************************************************************************
// logical methods
    public boolean isShoting() 
    {
        return shot; 
    }
    
    public boolean isAlive() 
    {
        return alive; 
    }
    
    public boolean isPlayed() 
    {
        return play; 
    }
    
    public void setShoting(boolean b)           // makes tank alive
    {
        shot = b; 
    }
    
    public void setAlive(boolean b)             // makes tank alive
    {
        alive = b; 
    }
    
    public void setPlayed(boolean b)             // makes tank played 
    {
        play = b; 
    }

//*****************************************************************************
// methods for position    
    public int getX()                           // returns horizontal position of tank                                       
    {
        return X; 
    }
    
    public int getY()                           // returns vertical position of tank
    {
        return Y; 
    }
    
    public void setLocation(int x, int y)       // sets tank's location 
    {
        X = x; 
        Y = y;      
        updateCannon();
    }   
    
    public static int getWidth()                // returns tank's width
    {
        return width; 
    }
    
    public static int getHeight()               // returns tank' height
    {
        return height; 
    }
//*****************************************************************************
// other methods
    public void updateCannon()                  // sets cannon and aim values
    {
        int x0 = getX()+getWidth()/2;                           

        cannon.setX(x0 - (int)(cannon.getLength()*Math.cos(getAngle()*Math.PI/180)));
        cannon.setY(getY()-1 - (int)(cannon.getLength()*Math.sin(getAngle()*Math.PI/180)));
        
        cannon.aim.setX(x0 - (int)(cannon.aim.getLength()*Math.cos(getAngle()*Math.PI/180)));
        cannon.aim.setY(getY() - (int)(cannon.aim.getLength()*Math.sin(getAngle()*Math.PI/180)));
    }
    
    public int getPower()                       // returns shot's power
    {
        return power; 
    }
    
    public int getAngle()                       // returns shot's angle
    {
        return angle; 
    }
            
    public void setPower(int p) 
    {
        power = p; 
    }

    public void setAngle(int a) 
    {
        angle = a; 
    }

    public void changeAngle(int a)              // add a to angle value
    {
        if (angle+a <= 180 && angle+a >= 0)
            angle += a; 
    }
    
    public void setName(String n)               // sets tank's name    
    {                                 
        name = n; 
    }
   
    public String getName()                     // returns tank's name
    {
        return name; 
    }

    public Image getImage()                     // returns tank's name
    {
        return image; 
    }
}
