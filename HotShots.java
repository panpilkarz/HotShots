import Hot.*;                               // package with classes:
                                            //      - Tank
import java.awt.*;                          //      - Mountain
import java.applet.*;
import java.awt.event.*;                    //      - Weapon
import java.util.*;                         //      - Cannon
import java.awt.Graphics.*;                 //      - Parabol
import javax.swing.*;                       //      - Hole
import javax.swing.event.*;
import javax.swing.Timer;

public class HotShots extends JFrame
{
    public static void main(String[] arg)               // here program is starting
    {
        Introduction intro = new Introduction();        // initialize "introduction layer"    
    }
}

// this class is an "introduction layer"
class Introduction
{
    public final int maxPlayers = 4;                    // how many available players in the game, u can decrease it to 2
    public final int maxWeapons = 3;                    // three types of weapon are implemented in the game, don't change
    public final int maxLevels = 100;                   // how many available levels in the game, u can change that        
    private MainFrame mf;                               // "visulaization layer"
    private Timer tm = new Timer(10, new mainTimer());  // main timer
    private Timer tk = new Timer(30, new keyTimer());   // key timer
    private int level = 0;                              // current level
    private int player = 0;                             // current player
    private int playerCount = 0;                        // how many players in the game
    private int spc = -1;                               // flag to count pressed time
    private int pressTank = 0;                          // if tank is pressed
    private int pressCannon = 0;                        // if cannon is pressed
    private int xDragged = 0, yDragged = 0;             // dragged point
   
    public Introduction()				
    {                                   
        mf = new MainFrame(this);	                // introduction must have access to visualization
        newGame();					// default game is started for two players
        //mf.show();       
        mf.setVisible(true);
    }
    
// this timer is started when player presses SPACE or LEFT MOUSE, and is stoped when he releases
    private class keyTimer implements ActionListener 	
    {	
        public void actionPerformed(ActionEvent e)
        {
            spc++;                              // power value is growing until released
            mf.status.setPowerLabel(spc);  
            if (spc == 100)                     // maximum of power = 100, despite not release - shot is started
            {
                tk.stop();
                mf.game.tank[player].setPower(spc);
                startShot();
                spc = -1;                       // set flag
            }
        }
    }
//*****************************************************************************    
//*****************************************************************************
// change power using SPACE or LEFT MOUSE
    public void mainFrameSpacePressed()     // player is changing power
    {
        startPower();
    }

    public void mainFrameSpaceReleased()    // player stopped changing power and is starting a shot 
    {
        stopPower();
    }
 
 // this method is called from GamePanel, argument is point of mouse cursor
    public void gamePanelClicked(int x, int y)  
    {
        // check if mouse cursor is above tank image
        if (x > mf.game.tank[player].getX() - mf.game.tank[player].getWidth() && 
            x < mf.game.tank[player].getX() + mf.game.tank[player].getWidth() && 
            y > mf.game.tank[player].getY() && 
            y < mf.game.tank[player].getY() + mf.game.tank[player].getHeight())
            {
                pressTank = 1;              // set flag
                startPower();               // player is changing power
            }
        // check if mouse cursor is above tank cannon
        else
        if (x > mf.game.tank[player].cannon.aim.getX() - mf.game.tank[player].cannon.aim.getLength()  &&
            x < mf.game.tank[player].cannon.aim.getX() + mf.game.tank[player].cannon.aim.getLength()  &&
            y > mf.game.tank[player].cannon.aim.getY() - mf.game.tank[player].cannon.aim.getLength()  &&
            y < mf.game.tank[player].cannon.aim.getY() + mf.game.tank[player].cannon.aim.getLength())
            {
                pressCannon = 1;            // set flag 
                xDragged = x;
                yDragged = y;
            }
    }

 // this method is called from GamePanel, mouse was released
    public void gamePanelReleased()         // player stopped changing power and is starting a shot 
    {
        if (pressTank==1)
            stopPower();        
        
        pressTank = 0;                      // set flags
        pressCannon = 0;
    }
//*****************************************************************************
//*****************************************************************************    
// change the angle using LEFT, RIGHT or DRAGGED MOUSE
    public void mainFrameRightPressed()			
    {
        mf.game.tank[player].changeAngle(1);            // increase value of angle
        mf.status.setAngleLabel(mf.game.tank[player].getAngle());
        mf.game.repaintCannon(player);                  // repaint piece of screen
    }

    public void mainFrameLeftPressed()                  
    {
        mf.game.tank[player].changeAngle(-1);           // decrease value of angle
        mf.status.setAngleLabel(mf.game.tank[player].getAngle());
        mf.game.repaintCannon(player);                  // repaint piece of screen
    }
     
    public void gamePanelCannonDragged(int x, int y)    // dragged mouse above the tank cannon
    {
         if (pressCannon == 1) 
            {
                int n = x - xDragged;                   // n = amplitude of change
                if (mf.game.tank[player].getAngle() < 90) 
                    n += yDragged - y;
                else
                    n += y - yDragged;
                if (n != 0) 
                {
                    mf.game.tank[player].changeAngle(n);    // add n to value of angle
                    mf.status.setAngleLabel(mf.game.tank[player].getAngle());
                    mf.game.repaintCannon(player);          // repaint piece of screen
                }
                xDragged = x;                               // mouse is still pressed
                yDragged = y;
            }        
    }
//*****************************************************************************    
//*****************************************************************************    
// change type of weapon using POPUP MENU or BACKSPACE
    public void gamePanelPopupSelected(int n)
    {
       if (!mf.game.tank[player].isShoting())
       {
           mf.game.tank[player].setWeapon(n);           // set active tank weapon
           mf.status.setWeaponLabel(mf.game.tank[player].getWeaponLabel());
       }                                                // refresh label on status bar
    }

    public void mainFrameBackspacePressed()		// player is changing weapon
    {                                                   // sctive tank weapon is next in queue
        if (!mf.game.tank[player].isShoting())
            nextWeapon();
    }
//*****************************************************************************    
//*****************************************************************************    
// menu bar items are clicked    
    public void menuNewClicked()			// menu Game|New was clicked
    {
        //mf.ngf.show();
		mf.ngf.setVisible(true);
    }

    public void menuGenerateClicked()			// menu Game|Generate was clicked
    {
        newMountains();
    }

    public void menuScoreClicked()			// menu Game|Score board was clicked
    {
        mf.message.showWon(level, mf.ngf.getLevel(), mf.game.tank);     
    }

    public void menuQuitClicked()			// menu Game|Quit was clicked
    {
        System.exit(0);
    }

    public void newGameOkClicked()			// start the new game because NewGameFrame|OK was clicked
    {                                              
         if (mf.ngf.getPlayed()>1)                      // new game can be started if
         {                                              // user chooses more than 1 player
            mf.ngf.dispose();                                 
            mf.toFront();
            newGame();                                  
         }
    }

    public void newGameCancelClicked()			// do nothing if NewGameFrame|Cancel was clicked
    {
         //mf.ngf.hide();
         mf.ngf.setVisible(false);
    }

    public void newGameDefaultClicked()			// set default settings because NewGameFrame|Default was clicked
    {
         mf.ngf.setDefault();
    }
//*****************************************************************************
//*****************************************************************************
// Timer is working from the start of shot to then end of shot
    private class mainTimer implements ActionListener 	// Timer - most important part of program
    {							// Timer is running when player is shoting,
	public void actionPerformed(ActionEvent e)	// and is stoped when the shot is over
        { 
            mf.game.repaintParabol(player);		// repaint piece of screen where shot is displayed

            for (int i=0; i<maxPlayers; i++)		// check if any tank is hit
            if (mf.game.tank[i].isPlayed() && !mf.game.tank[i].isAlive())
            {
                mf.game.repaint();
                if (i == player)                        // player is hit by himself
                {
                    mf.message.showSuit(mf.game.tank[i].getName());   
                    mf.game.tank[player].decScore();                    
                }
                else					// player is hit by other player
                {
                    mf.message.showLost(mf.game.tank[i].getName(), mf.game.tank[player].getName());   
                    mf.game.tank[player].incScore();
                }
                mf.game.tank[i].setPlayed(false);
                playerCount--;                          // decrease value of played players
            }

            if (playerCount == 0)                       // no player on the game - next round (level), no score
            {	
                    tm.stop();                          // stop timer
                    mf.message.showWon(level, mf.ngf.getLevel(), mf.game.tank);     // display MessageBox 
                    nextLevel();
            }
            
            if (!mf.game.tank[player].isShoting())      // player has finished his shot
            {
                tm.stop();                              // stop timer
                nextPlayer();
            }    
            
            if (playerCount == 1)                       // only one player on the game - next round (level)
            {
                 tm.stop();
                 nextPl(); 
                 mf.game.tank[player].incScore();       // inc winner's score
                 mf.message.showWon(level, mf.ngf.getLevel(), mf.game.tank);     // display MessageBox   
                 nextLevel();
            } 
            
            if (level == mf.ngf.getLevel()+1)			// last round, the end of the game
            {
                    tm.stop();
                    level = 1;
                    //mf.ngf.show();
                    mf.ngf.setVisible(true);
                    mf.message.showOver();
            } 
        }
    }
//*****************************************************************************
//*****************************************************************************
// various methods - useful to control game
    private void startPower()
    {
        if (spc == -1 && !mf.game.tank[player].isShoting())      
        {
            spc = 0;                                            // set flag
            tk.start();                                         // start shot = start keyTimer        
        }
    }

    private void stopPower()
    {
       if (spc != -1 && !mf.game.tank[player].isShoting())     // player must be not shoting to start a shot 
        {
            tk.stop();                                          // end shot = stop keyTimer
            mf.game.tank[player].setPower(spc);
            startShot();
            spc = -1;                                           // set flag
        }        
    }
    
    private void newGame()					// method sets new game
    {
        level = 0;
        mf.ngf.getValue(mf.game.tank);
        nextLevel();
    }

    private void newMountains()					// method generates new mountains
    {		
        mf.game.mountain.generate(mf.game.tank);   
        mf.game.repaint();
    }

    public void nextPl()                                        // find new alive player
    {
        while (true)
        {
            player++;
            if (player >= maxPlayers) player = 0;
            if (mf.game.tank[player].isPlayed() && mf.game.tank[player].isAlive()) 
               break;
        }        
    }

    private void nextPlayer()					// function changes player while the round
    {
        mf.game.tank[player].cannon.setActive(false);
        nextPl();
        mf.game.tank[player].cannon.setActive(true);
        if (mf.game.tank[player].getWeaponCounter() == 0)
            nextWeapon();
        mf.status.setValues(mf.game.tank[player].getName(),
                         mf.game.tank[player].getPower(),
                         mf.game.tank[player].getAngle(),
                         mf.game.tank[player].getWeaponLabel());
        mf.game.pop.build(mf.game.tank[player]);
        mf.game.repaint();
    }

    private void nextLevel()					// method sets new level (round)
    {	
        mf.ngf.setPlayed(mf.game.tank);
        playerCount = 0;
        for (int i=0; i<maxPlayers; i++)
            if (mf.game.tank[i].isPlayed())
                playerCount++;
 
        nextPlayer();
        newMountains();
        mf.menu.menuGameGenerate.setEnabled(true);
        level++;
    }
		
    private void nextWeapon()					// method changes player's weapon
    {								// must also refresh status bar
        mf.game.tank[player].nextWeapon();
        mf.status.setWeaponLabel(mf.game.tank[player].getWeaponLabel());
    }
    
    private void startShot()					// method sets new shot
    {
        mf.menu.menuGameGenerate.setEnabled(false);
        mf.game.tank[player].cannon.setActive(false);
        mf.game.tank[player].setPower(mf.status.powerBar.getValue());
        mf.game.tank[player].decWeapon();
        mf.game.tank[player].setShoting(true);
        mf.game.repaint();
        tm.start();                                             // start timer
    }
}

// class mainFrame is a "visualization layer"
// ----------------------------------------------------------------------------
// |                         MENU  BAR                                        |
// ----------------------------------------------------------------------------
// |                                                                          |
// |                         GAME PANEL                                       |
// |                                                                          |
// ----------------------------------------------------------------------------
// |                         STATUS BAR                                       |
// ----------------------------------------------------------------------------
class MainFrame extends JFrame
{   
    //private final int width = 800, height = 600;        // screen resolution
    private Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
    private int width = (int)d.getWidth();
    private int height = (int)d.getHeight();
    public Menu menu;                                   // menu bar - Game, Help
    public StatusBar status;                            // status bar - displays tank's settings
    public NewGameFrame ngf;                            // new game frame - frame with tank's settings
    public GamePanel game;                              // game panel - graphic
    public Message message;                             // messages - show who won/lost
    private Introduction intro;                     	// reference to Introduction
	private int space_cnt = 0;
	//private long now = System.currentTimeMillis();
	
    public MainFrame(Introduction in)           	 
    {                                                    
        intro = in;                                      
        setTitle("Hot Shots");                           
        setResizable(false);           
        setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Hot.gif")));
        setBackground(Color.black);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
        Container con = getContentPane();
        
        menu = new Menu(in);
        setJMenuBar(menu.getMenu());                    // add menu

        game = new GamePanel(width, height, in);
        con.add(game, BorderLayout.CENTER);             // add panel with game
         
        status = new StatusBar(in);
        con.add(status.box, BorderLayout.NORTH);        // add status bar
        
        ngf = new NewGameFrame(in);                     // initialize new game frame
        
        message = new Message(in);                      // initialize messages

        addKeyListener(new anyKey_Press());             // we need SPACE, BACKSPACE and ARROWS
    }

    private class anyKey_Press implements KeyListener 	// key listener must be in this place, because           
    {							// this component is available every time
        public void keyPressed(KeyEvent e)
        {                                               // evemts which are send to "introduction layer"
            int ch = e.getKeyCode();

            if (ch == KeyEvent.VK_SPACE) {
				space_cnt += 1;
				//System.out.println(now + " pressed space = " + space_pressed);
				if (space_cnt == 1) {
					intro.mainFrameSpacePressed();
				}
				if (space_cnt == 2) {
					space_cnt = 0;
					intro.mainFrameSpaceReleased();
				}
			}
            else 
            if (ch == KeyEvent.VK_RIGHT)
                intro.mainFrameRightPressed();
            else
            if (ch == KeyEvent.VK_LEFT)
                intro.mainFrameLeftPressed();
       }      

        public void keyReleased(KeyEvent e) 
        {
            int ch = e.getKeyCode();

            if (ch == KeyEvent.VK_SPACE) {
			}
            else
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                intro.mainFrameBackspacePressed(); 
        }
        
        public void keyTyped(KeyEvent e) { }
    }
}   

// class Menu is declared in MainFrame
// ----------------------------------------------------------------------------
// | GAME     |                                                               |
// ----------------------------------------------------------------------------
// | New      |                                                
// | Generate |                                                              
// | Quit     |                                                               
// ------------
class Menu extends JMenuBar                     	// menu bar
{
    private JMenuBar menuBar = new JMenuBar();          // this is returned to MainFrame
    private JMenu menuGame = new JMenu("Game");     
    private JMenuItem menuGameNew = new JMenuItem("New");
    public JMenuItem menuGameGenerate = new JMenuItem("Generate");
    public JMenuItem menuGameScore = new JMenuItem("Score board");    
    private JMenuItem menuGameQuit = new JMenuItem("Quit");
    private Introduction intro;                         // reference to Introduction
    
    public Menu(Introduction in)
    {
        intro = in;
        menuBar.add(menuGame);                          // build menu, add items
        menuGame.add(menuGameNew);
        menuGame.add(menuGameGenerate);
        menuGame.add(menuGameScore);
        menuGame.addSeparator();
        menuGame.add(menuGameQuit);

        menuGameNew.addActionListener(new menuGameNew_Click());     // add listeners for manu items
        menuGameGenerate.addActionListener(new menuGameGenerate_Click());
        menuGameScore.addActionListener(new menuGameScore_Click());
        menuGameQuit.addActionListener(new menuGameQuit_Click());
    }    
   
    private class menuGameNew_Click implements ActionListener	// "ok" was clicked
    {
        public void actionPerformed(ActionEvent e)      
        {
            intro.menuNewClicked();
        }
    }

    private class menuGameGenerate_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)   		// "generate" was clicked   
        {
            intro.menuGenerateClicked();
        }
    }

    private class menuGameScore_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)   		// "generate" was clicked   
        {
            intro.menuScoreClicked();
        }
    }

    private class menuGameQuit_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)		// "quit" was clicked   
        {
            intro.menuQuitClicked(); 
        }
    }

    public JMenuBar getMenu() 					// return menu to MainFrame
    {
        return menuBar; 
    }
}

// class GamePanel in in the middle of MainFrame
// =---------------------------------------------
// |                                            |
// |                     _______                |
// |                   /        \       /\      |
// |     /\          /           |     /  \     |
// |   /   \/\     /              \   /    \    |
// | /        \__/                 |_/      \___|
// ----------------------------------------------
class GamePanel extends JPanel          		    
{   
    private int width, height;                              // width and height of area
    public Tank[] tank = new Tank[10];                      // table of tanks 
    public PopupMenu pop;                                   // initialize popup menu
    public Mountain mountain;                               // mountain terrain
    private Introduction intro;                             // reference to Introduction

    public GamePanel(int w, int h, Introduction in)         
    {
        intro = in;
        width = w;
        height = h;
        setSize(width, height);
        setBackground(Color.gray);  
        
        MediaTracker mt = new MediaTracker(this);
        for (int i=0; i<intro.maxPlayers; i++) 
        {
            tank[i] = new Tank("tank" + i +".jpg");         // initialize tanks
       
            mt.addImage(tank[i].getImage(), i);                                // load images
            try 
            {
                mt.waitForID(i); 
            }
            catch (InterruptedException e) {}
        }
        
        mountain = new Mountain(width, height);             // initialize mountains
    
        pop = new PopupMenu();                              // initialize popup menu,
        for (int i=0; i<intro.maxWeapons; i++)              // each item in popup menu has a different listener (it is a table)
            pop.weapon[i].addActionListener(new popup_Click(i));      
        
        addMouseListener(new mouse_Click());                // listeners for mouse
        addMouseMotionListener(new mouse_Moved());  
    }
    
    public void drawMountains(Graphics2D g)
    {
        g.setColor(Color.black);                                             // fill all area with black
        g.fillRect(0, 0, mountain.getWidth(), mountain.getHeight());   
        for (int i = 0; i < mountain.getWidth(); i++)
        {
            g.setColor(new Color(mountain.getColor(i), 0, 0));                // each line has different color - it makes shadow
            g.drawLine(i, mountain.getTop(i), i, mountain.getHeight());       // draw mountains by vertical lines
        }
    }
        
    public void drawTanks(Graphics2D g)
    {
    	Color white = new Color(255, 255, 255);
        for (int i=0; i<intro.maxPlayers; i++) 
            if (tank[i].isPlayed() && tank[i].isAlive())                                // tank must be palyed and alive to draw it
            {
                g.drawImage(tank[i].getImage(), tank[i].getX(), tank[i].getY(), null);  // draw tank's picture
                
                int x0 = tank[i].getX()+tank[i].getWidth()/2;                           // draw cannon
                
                g.setColor(white);                                  // cannon is drawed by double line
                g.drawString(tank[i].getName(), tank[i].getX() - (tank[i].getName().length()/2)*3, tank[i].getY() - 20);	// write the name of player
                
                
                g.setColor(tank[i].cannon.getColor());                                  // cannon is drawed by double line
                g.drawLine(x0, tank[i].getY()-1, tank[i].cannon.getX(), tank[i].cannon.getY());
                g.drawLine(x0-1, tank[i].getY()-1, tank[i].cannon.getX()-1, tank[i].cannon.getY()); 
                
                if (tank[i].cannon.getActive())                                         // here an aim is drawed (+)
                {
                  g.setColor(tank[i].cannon.aim.getColor());
                  g.drawLine(tank[i].cannon.aim.getX()-tank[i].cannon.getLength(),       // draw horizontal line
                             tank[i].cannon.aim.getY(), 
                             tank[i].cannon.aim.getX()+tank[i].cannon.getLength(),
                             tank[i].cannon.aim.getY());
                  g.drawLine(tank[i].cannon.aim.getX(),                                  // draw vertical line
                             tank[i].cannon.aim.getY()-tank[i].cannon.getLength(), 
                             tank[i].cannon.aim.getX(), 
                             tank[i].cannon.aim.getY()+tank[i].cannon.getLength());
                }       
                if (tank[i].isShoting())                                                 // if tank is shoting draw flying weapon
                    tank[i].setShoting(tank[i].weapon[tank[i].getWeapon()].draw(g, mountain, tank, tank[i]));
            }
    }
    
    public void paint(Graphics g)                           // this is called when repaint() is called
    {
        super.paint(g);
        Graphics2D figure = (Graphics2D)g;  
                             
        drawMountains(figure);                              // draw mountain
        drawTanks(figure);                                  // draw tanks
   }
    
    private class mouse_Click implements MouseListener      // mouse events
    {
        public void mouseReleased(MouseEvent e) 
        { 
            if (e.getModifiers() == e.BUTTON3_MASK)        // show popup manu
                pop.show(e.getComponent(), e.getX(), e.getY());
            
            if (e.getModifiers() == e.BUTTON1_MASK)                     
                intro.gamePanelReleased();                  // returns information about MOUSE RELEASED to Introduction
        }
        
        public void mousePressed(MouseEvent e) 
        {
            if (e.getModifiers() == e.BUTTON1_MASK)
            {
                if (e.getClickCount() == 1)                  // returns information about MOUSE PRESSED to Introduction
                    intro.gamePanelClicked(e.getX(), e.getY());
            }
        }
        
        public void mouseExited(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
    }

    private class mouse_Moved implements MouseMotionListener
    {
        public void mouseDragged(MouseEvent e)              // returns information about MOUSE DRAGGED to Introduction 
        {
            intro.gamePanelCannonDragged(e.getX(), e.getY());
        }
        
        public void mouseMoved(MouseEvent e) { }
    }
        
    private class popup_Click implements ActionListener
    {
        public popup_Click(int n)
        {
            number = n;                                     // identification for mouse listener
        }

        public void actionPerformed(ActionEvent e)
        {
            intro.gamePanelPopupSelected(number);           // returns information about SELECTED POPUP MENU to Introduction
        }
         
        private int number;
    }
    
    public void repaintCannon(int a)                        // repaint a piece of screen where cannon is displayed
    {
        int r = tank[a].cannon.aim.getLength() + tank[a].cannon.getLength();
        
        tank[a].updateCannon();                             // sets new position of cannon
        
        repaint(tank[a].getX()+tank[a].getWidth()/2-r, 
                tank[a].getY()-r, 
                2*r, 
                r+tank[a].getHeight());
    }

    public void repaintParabol(int a)                       // repaint piece of screen where shot is displayed
    {
        int r = tank[a].weapon[tank[a].getWeapon()].getRadius()+15; 
        int x = tank[a].weapon[tank[a].getWeapon()].getX()-r;
        int y = tank[a].weapon[tank[a].getWeapon()].getY()-r; 
        if (y < 0) y = 0;
        
        repaint(x, y, 2*r, 2*r);
    }
}

// class StatusBar is on the bottom of MainFrame
// ----------------------------------------------------------------------------
// |  Henry         55         |#####        |          112           missile |
// ----------------------------------------------------------------------------
class StatusBar extends JPanel
{
    private Color color = new Color(100, 100, 100); // font color
    public JLabel playerName = new JLabel("");      
    public JLabel powerLabel = new JLabel("");
    public JLabel angleLabel = new JLabel("");
    public JLabel weaponLabel = new JLabel("");
    public JProgressBar powerBar = new JProgressBar(0, 100);
    public Box box;                                 
    private Introduction intro;                     // reference to Introduction
    
    public StatusBar(Introduction in)       
    {
       intro = in;
       playerName.setForeground(color);
       powerLabel.setForeground(color);
       angleLabel.setForeground(color);
       weaponLabel.setForeground(color);
       Box layout = Box.createHorizontalBox();      // box layout is used    
       layout.add(Box.createHorizontalStrut(5));
       layout.add(playerName);
       layout.add(Box.createGlue());
       layout.add(powerLabel);
       layout.add(Box.createGlue());
       layout.add(powerBar);
       layout.add(Box.createGlue());
       layout.add(angleLabel);
       layout.add(Box.createGlue());        
       layout.add(weaponLabel);
       layout.add(Box.createHorizontalStrut(5)); 
       box = Box.createVerticalBox();
       box.add(layout);
    }
    
    public void setPowerLabel(int i)                  // value of i is added to power value
    {
        powerBar.setValue(i);
        powerLabel.setText("" + i);
    }

    public void setAngleLabel(int i)                  // value of i is added to angle value
    {
        angleLabel.setText("" + i);
    }
    
    public void setValues(String n, int p, int a, String w)
    {                                               // values of status bar are set here
        playerName.setText(n);
        powerLabel.setText("" + p);
        angleLabel.setText("" + a);        
        powerBar.setValue(p);
        weaponLabel.setText(w);
    }
    
    public void setWeaponLabel(String s)            // refresh weapon label
    {
        weaponLabel.setText(s); 
    }
}

// PopupMenu is used to changing type of weapon and is declared in GamePanel
// --------------
// | missile 20 |
// | nuke 5     |
// | roller 3   |
// --------------
class PopupMenu extends JPopupMenu
{
    public JMenuItem[] weapon = new JMenuItem[3];

    public PopupMenu()                          // listeners for menu items are in GamePanel
    {
        weapon[0] = new JMenuItem("");
        weapon[1] = new JMenuItem("");
        weapon[2] = new JMenuItem("");   
        add(weapon[0]);
        add(weapon[1]);
        add(weapon[2]);
    }
        
    public void build(Tank t)                   // if player has not any weapon, 
    {                                           // this item is disabled
        for (int i=0; i<3; i++)
        {
            weapon[i].setEnabled(true);
            weapon[i].setText(t.weapon[i].getName() + " " +t.weapon[i].getCounter());
            if (t.weapon[i].getCounter() == 0)
                weapon[i].setEnabled(false);
        }
    }
}

// class Message is declared in MainFrame, is used to display messages
class Message 
{
    private Random random = new Random();
    private String[] lost = new String[10];
    private String[] suit = new String[10];   
    private Introduction intro;                 // reference to Introduction

    public Message(Introduction in)             // messages are displayed when:
    {                             
        intro = in;                             //          - any tank is hit
        lost[0] = " has gone to the heaven.";   //          - end of the round (level)
        lost[1] = " is dead.";                  //          - end of the game
        lost[2] = " said 'Bye Bye'.";
        
        suit[0] = " commited suitside.";        // one of this text is randomed
        suit[1] = " is a moron of the year.";
        suit[2] = " is a kamikaze.";
    }
    
    public void showLost(String p1, String p2)
    {
        JOptionPane.showMessageDialog(null,
                                      p1 + lost[random.nextInt(3)] + "\n1 point for " + p2 + ".",
                                      "HotShots",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    public void showSuit(String p)
    {
        JOptionPane.showMessageDialog(null, 
                                      p + suit[random.nextInt(3)] + "\nLost 1 point.",
                                      "HotShots",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showWon(int l, int lm, Tank[] t)                // display score board
    {                                                           // l - current level
        String[] names = new String[10];                        // lm - maximum level
        int[] scores = new int[10];                             // t - table of tanks      
        String s;
        int i, j, k = 0, m = 0;
        
        for (i=0; i<intro.maxPlayers; i++)
            if (t[i].getScore() != -100)
            {
               names[k]=t[i].getName();
               scores[k]=t[i].getScore();
               k++;
           } 
        
        for (j=0; j<k; j++)                             // sorts players by score, bubble sorting
            for (i=0; i<k-1-j; i++)
                if (scores[i] < scores[i+1])
                {
                    m = scores[i];
                    s = names[i];
                    scores[i] = scores[i+1];
                    names[i] = names[i+1];
                    scores[i+1] = m;
                    names[i+1] = s;
                }
        
        s = "\nLevel  -  " + l + "/" + lm + "\n\n";
        for (i=0; i<k; i++)
            s += names[i] + " got " + scores[i] + " points.\n";
        
        JOptionPane.showMessageDialog(null, s, "HotShots", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showOver()
    {
        JOptionPane.showMessageDialog(null,
                                      "It was last round.\nPress OK to start new game.\nPress CANCEL to continue previous game.", 
                                      "HotShots", JOptionPane.INFORMATION_MESSAGE);
    }
}

// class NewGameFrame is used to set new game
// ----------------------------------------
// |  description 1   |   weapon 1        |
// ----------------------------------------
// |  description 2   |   weapon 2        |
// ----------------------------------------
// |     - || -       |     - || -        |   
// ----------------------------------------
// |   levels         |   OK, CANCEL      |
// ----------------------------------------
class NewGameFrame extends JFrame
{
    private DescriptionPanel[] description = new DescriptionPanel[10];  // panels with players
    private SliderPanel[] slider = new SliderPanel[4];                 // panels with weapon
    public ButtonPanel button;                                          // panel with buttons
    private int activeDescription;                                      // current panel
    private final int miss = 50, nuke = 5, roll = 10;                    // default value of weapon
    private Introduction intro;                                         // reference to Introduction

    public NewGameFrame(Introduction in)    
    {
        intro = in;
        activeDescription = 0;                                          
        setSize(500, 75*((in.maxPlayers > in.maxWeapons ? in.maxPlayers : in.maxWeapons)+1));
        setTitle("New game");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Hot.gif")));
        Container con = getContentPane();
        int i;
        for (i=0; i<intro.maxPlayers; i++) 
        {
            description[i] = new DescriptionPanel(i, i<2);                  // initialize new panels
            description[i].setCount(miss, 0);               
            description[i].setCount(nuke, 1);                               // set default amounts of weapon
            description[i].setCount(roll, 2);
            description[i].addMouseListener(new description_Click(i));   
        }

        button = new ButtonPanel(in);                                       // initialize button panel
        button.addMouseListener(new button_Click());   

        slider[3] = new SliderPanel("Levels", 1, in.maxLevels, 10);
        slider[0] = new SliderPanel("Player 0 - missiles: ", 0, 100, miss); // initialize slider panels
        slider[1] = new SliderPanel("Player 0 - nukes: ", 0, 100, nuke);
        slider[2] = new SliderPanel("Player 0 - rollers: ", 0, 100, roll);
        
        con.setLayout(new GridLayout((in.maxPlayers > in.maxWeapons ? in.maxPlayers : in.maxWeapons)+1, 2));      // grid layout is used
        
        for (i=0; i<(in.maxPlayers < in.maxWeapons ? in.maxPlayers : in.maxWeapons); i++) 
        {                                                                    // on the left side put tank panel
            con.add(description[i]);                                        // on the right side put weapon panel
            con.add(slider[i]);
        }
        
        for (i=(in.maxPlayers < in.maxWeapons ? in.maxPlayers : in.maxWeapons); i<in.maxPlayers; i++)
        {
            con.add(description[i]);         // if player > weapon put empty panel on the right
            con.add(new JPanel());
        }

        for (i=(in.maxPlayers < in.maxWeapons ? in.maxPlayers : in.maxWeapons); i<in.maxWeapons; i++)
        {
            con.add(new JPanel());          // if player < weapon put empty panel on the left
            con.add(slider[i]);
        }
        
        con.add(slider[in.maxWeapons]);     // add panel with levels
        con.add(button);                    // add button panel
    }
    
    public void refresh()                   // when active description is changed,
    {                                       // weapon sliders must be refreshed
        slider[0].setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), 
                            description[activeDescription].getName() + " - missiles: "));
        slider[1].setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), 
                            description[activeDescription].getName() + " - nukes: "));
        slider[2].setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), 
                            description[activeDescription].getName() + " - rollers: "));
        for (int i=0; i<intro.maxWeapons; i++)       
            slider[i].setValue(description[activeDescription].getCount(i));
    }
    
    public void setDefault()                 // sets values of weapon default (miss, nuke, roll)
    {
        for (int i=0; i<intro.maxPlayers; i++) 
        {
            description[i].setCount(miss, 0);
            description[i].setCount(nuke, 1);
            description[i].setCount(roll, 2);          
        }
        slider[0].setValue(miss);            // refresh slider values 
        slider[1].setValue(nuke);        
        slider[2].setValue(roll);        
   }
    
    public void getValue(Tank[] t)           // sets tank after menu Game|New|Ok was clicked
    {
        for (int i=0; i<intro.maxPlayers; i++) 
        {
            setPlayed(t);
            t[i].setShoting(false);
            t[i].setName(description[i].getName());
            for (int k=0; k<intro.maxWeapons; k++)
                t[i].weapon[k].setCounter(description[i].getCount(k));
            
            if (t[i].isPlayed())
                t[i].setScore(0);
            else
                t[i].setScore(-100);
        }        
    }

    public void setPlayed(Tank[] t)          // sets tank played or not played
    {
        for (int i=0; i<intro.maxPlayers; i++) 
        {
            t[i].setPlayed(description[i].isSelected());
            t[i].setAlive(description[i].isSelected());
        }
    }

    public int getPlayed()                  // returns how many tanks are selected
    {
	int a=0;
	for (int i=0; i<intro.maxPlayers; i++)
	    if (description[i].isSelected()) a++;
        return a;
    }
    
    public int getLevel()                   // returns how many levels are chosen
    {
        return slider[3].getValue(); 
    }
    
    private class description_Click implements MouseListener
    {
        public description_Click(int n)     // number is an identify for listener
        {
            number = n;
        }
        
        public void mouseEntered(MouseEvent e)  // when mouse is entering panel,
        {                                       // new values of sliders are set
            if (number != activeDescription)
            {
                for (int i=0; i<intro.maxWeapons; i++)
                    description[activeDescription].setCount(slider[i].getValue(), i);
                activeDescription = number;
                refresh();
            }                       
        }
        public void mouseClicked(MouseEvent e) { }
        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
        
        private int number;
    }

    private class button_Click implements MouseListener // the same in description_Click
    {        
        public void mouseEntered(MouseEvent e) 
        {       
                for (int i=0; i<intro.maxWeapons; i++)
                    description[activeDescription].setCount(slider[i].getValue(), i);
                refresh();
        }
        public void mouseExited(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
    }
}

// this class reprezents tank's settings
class DescriptionPanel extends JPanel              
{
    private JTextField name = new JTextField("", 10);
    private JCheckBox check = new JCheckBox("play!");
    private Image img; 
    private int count[] = new int[3];               // table of weapon's walue

    public DescriptionPanel(int i, boolean b)
    {
        check.setSelected(b);
        setBorder(BorderFactory.createTitledBorder(
                  BorderFactory.createEtchedBorder(),
                  "Player" + i));
        add(check);
        add(name);
        name.setText("Player" + i);
        
        img = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Hot/tank"+ i +".jpg"));
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img, 0);                                // load images
        try 
        { 
            mt.waitForID(0); 
        }
        catch (InterruptedException e) {}
        
        name.addKeyListener(new name_Change());
    }

    private class name_Change implements KeyListener        // name tank is changed       
    {       
        public void keyReleased(KeyEvent e) 
        {
            setBorder(BorderFactory.createTitledBorder(
                      BorderFactory.createEtchedBorder(),
                      " " + name.getText()));
        }
        public void keyPressed(KeyEvent e) {}
        public void keyTyped(KeyEvent e) {}
    }

    public void paint(Graphics g)       // paint tank's picture
    {
       super.paint(g);
       g.drawImage(img, 5, check.getY() + 5 , null);
    }
    
    public String getName()             // gets tank's name
    {
        return name.getText(); 
    }
    
    public void setCount(int n, int i)  // count[] is a table with value of weapons
    {
        count[i] = n;
    }
      
    public int getCount(int i) 
    {
        return count[i]; 
    }

    public boolean isSelected()         // player is played or not played
    {
        return check.isSelected(); 
    }
}

// this class reprezents value of weapon and is declared in NewGameFrame
class SliderPanel extends JPanel        
{
    private JSlider slider;                     // slider - analog reprezentation of value
    private JLabel label;                       // label - digital reprezentation of value

    public SliderPanel(String name, int min, int max, int v)
    {
        setBorder(BorderFactory.createTitledBorder(
                  BorderFactory.createEtchedBorder(),
                  name));
        slider = new JSlider(min, max, v);      
        label = new JLabel("" + v);
        add(slider);
        add(label);

        slider.addChangeListener(new slider_Change());     
    }

    private class slider_Change implements ChangeListener
    {
        public void stateChanged(ChangeEvent e)
        {  
            label.setText("" + slider.getValue());            
        }
    }
    
    public void setValue(int v) 
    {
        slider.setValue(v); 
    }

    public int getValue() 
    {
        return slider.getValue(); 
    }
}

// this class reprezents three buttons and is declared in NewGameFrame
class ButtonPanel extends JPanel                    
{                                           
    public JButton ok = new JButton("Ok");          // - OK
    public JButton cancel = new JButton("Cancel");  // - CANNCEL
    public JButton def = new JButton("Default");    // - DEFAULT
    private Introduction intro;                     // reference to Introduction

    public ButtonPanel(Introduction in)     
    {                                       
        intro = in;
        add(def);
        add(ok);
        add(cancel);

        ok.addActionListener(new newGameButtonOk_Click());
        cancel.addActionListener(new newGameButtonCancel_Click());
        def.addActionListener(new newGameButtonDefault_Click());
    }
    
    private class newGameButtonOk_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            intro.newGameOkClicked();
        }
    }

    private class newGameButtonCancel_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            intro.newGameCancelClicked();
        }
    }
   
    private class newGameButtonDefault_Click implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            intro.newGameDefaultClicked();
        }
    }
}


