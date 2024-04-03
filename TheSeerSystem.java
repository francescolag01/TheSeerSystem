

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.security.auth.x500.X500Principal;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//I downloaded this library from https://fazecast.github.io/jSerialComm/ , to use the Arduinos serial interface
import com.fazecast.jSerialComm.SerialPort;

public class TheSeerSystem {
	public static JFrame f; // the frame displaying the application
	public static Image planeImage; // plane icon drawn on screen
	//TODO: DEFINE THE FOLLOWING FINAL VARIBLES
	static final int delay = 50; //The delay of the Arduino, in milliseconds. used to calculate the speed
	static final int closeCallDist = 300; // the threshold to trigger a close call popup
	static final int collisionDist = 20; // the threshhold to trigger a collision popup
   
	public static int prevRunwayOneDist, prevRunwayTwoDist = 0; // the last distances detected by sensors one and two, used to calculate speed
	public static int currRunwayOneDist, currRunwayTwoDist = 0; // the current distances detected by sensors one and two
	public static int runwayOneSpeed, runwayTwoSpeed; // the current speed of planes on the runway
	public static boolean prevRunwayOneOcc,prevRunwayTwoOcc = false; // True if a plane is on the runway last timestep
	public static boolean currRunwayOneOcc,currRunwayTwoOcc = false; // True if a plane is on the runway currently
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	//initializes the GUI
                createAndShowGUI();
                //load and store the plane image
                planeImage = loadPlane();
                
                //establish serial connection to the Arudino
                //we create the comport obj and open the arudino port
                SerialPort comPort = SerialPort.getCommPort("COM5"); // or whatever port we end up using
                //TODO: I get an error here. Is it because I am not hooked up to the Aruidno>
                comPort.openPort();
         
                try {
                while (true)
                {
                	 //wait for the serial interrupt
                	while(comPort.bytesAvailable() == 0) {

                	     
                		
                	}
                	//read the serial port
                	//TODO: Determine how we want to send the message from the arduino, and how to process it in this application 
             	    byte[] readBuffer = new byte[comPort.bytesAvailable()];
             	    //we convert the byte buffer into one big string displaying the data of the two sensors
                	String msg = new String(readBuffer);
                	
                	
             	   
                	//messages will be in the form of XiiiXiii, where
                	//X is either f for 'free' or o for 'occupied'
                	//iii are 3 digits displaying its position relative to the start of the sensor.
                	
                	//this program tracks occupancy and distance, and it also tracks the occupancy and distance of the last sensor reading.
                	
                	prevRunwayOneOcc = currRunwayOneOcc;
                	currRunwayOneOcc = msg.charAt(0) == 'f' ? false : true;
                	prevRunwayOneDist = currRunwayOneDist;
                	currRunwayOneDist = Integer.parseInt(msg.substring(1,3));
                	
                	prevRunwayTwoOcc = currRunwayTwoOcc;
                	currRunwayOneOcc = msg.charAt(0) == 'f' ? false : true;
                	prevRunwayTwoDist = currRunwayTwoDist;
                	currRunwayTwoDist = Integer.parseInt(msg.substring(5,7));
                	
                	//We use this to calculate the speed of each aircraft, and also to track arrivals and departures.
                	
                	//if the runway was previously free, and a plane lands, an arrival popup will display
                	//if the runway was previously occupied, and a plane leaves, a departure popup will display
                	
                	//first we handle popups
                	//runway one 
                	if(prevRunwayOneOcc == false && currRunwayOneOcc == true) {
                		// arrival
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has landed on runway 1");
                	}else if(prevRunwayOneOcc == true && currRunwayOneOcc == false) {
                		//departure
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has taken off from runway 1");
                	}else {
                		//no popup, do nothing
                	}
                	
                	//runway two
                	if(prevRunwayTwoOcc == false && currRunwayTwoOcc == true) {
                		// arrival
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has landed on runway 2");
                	}else if(prevRunwayTwoOcc == true && currRunwayTwoOcc == false) {
                		//departure
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has taken off from runway 2");
                	}else {
                		//no popup, do nothing
                	}
                	
                	if(computeDistance(currRunwayOneDist,currRunwayTwoDist) >= collisionDist) {
                		//collision (oh no)
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"OH NO: AIRCRAFT HAVE COLLIDED ON RUNWAY 1 AND 2.");
                	}else if(computeDistance(currRunwayOneDist,currRunwayTwoDist) >= closeCallDist) {
                		//close call popup
                		JOptionPane.showMessageDialog(TheSeerSystem.f,"DANGER: AIRCRAFT ARE LESS THAN 300 FEET APART.");
                	}else {
                		//no danger of collision, do nothing
                		
                	}
                	//now we check their distances, and see if they are close enough to be a close call or a collision
                	
                	
             	   //TODO:What is the main functionality of the software?
	                	//Popups stating arrivals, departures, near misses, and collisions DONE, NEEDS TESTING
	                	//the runways changing color based on if there is a plane or not DONE, NEEDS TESTING
	                	//draw a plane icon on the screen, relative to the physical plane's location on the runway. DONE, NEEDS TESTING
                		//On a panel, display runway number, plane occupancy, and speed
                		
                	
                	//TODO: What features can we add for polish?
                		//make the popups not appear in the middle of the screen
                		//draw text to label useful information
             	   		//change direction of plane if it goes backwards
                		//add a logo
                		//settings
             	   //redraw the screen
                		f.update(f.getGraphics());
                  
                }
             } catch (Exception e) {
            	 e.printStackTrace();
            	 }
             comPort.closePort();
            	  
                
               
            
            }});
    }
public static int calculateSpeed(int prev, int curr) {
	//calculate speed using the prev and current distance
	//USE THE REAL LIFE SENSOR READINGS!
	return (curr - prev) / delay;
}
    
    
public static int scaleDistance(int distance) {
	//this function takes in the real life distance of the plane,
	//and scales it so the icon on the screen is proportional to the real life distance.
	//TODO: DETERMINE THE FACTOR THAT SCALES IT THE MOST APPROPRITATELY
	int factor = 1;
	return distance / factor;
}

public static int computeDistance(int d1, int d2) {
	//computes the pythagorean theroem to determine how close the two planes are from eachother
	//USE THE REAL LIFE SENSOR READINGS FOR THIS FUNCTION!!
	
	
	
	//TODO: i dont actually think this works.
	//have to figure out how to calculate the distance, or at the very least if they occupy the same 
	//piece of runway in the middle of the screen.
	return (int) Math.sqrt((d1 * d1) + (d2 * d2)); // parenthesis incase the computer forgets pemdas
}



    private static void createAndShowGUI() {
    
        f = new JFrame("The Seer System");
        f.setSize(new Dimension(1000,1000));
        f.setPreferredSize(new Dimension(1000,1000));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new drawPanel());
        f.pack();
        f.setVisible(true);
       
    }
    
    
public static Image loadPlane() {
//loads the plane image and puts it in an Image object, to display on the screen
Image img = null;

//loads the png
URL url = TheSeerSystem.class.getResource("imgbin_airplane-png.png");
String path = url.toString().substring(5);
//the above code first searches locally for the png. When it finds it, it has the prefix "file:"
//but the below .read() fails if that text is included. Simply, we create a new substring starting on the characters we want.
    	try {
    	img = ImageIO.read(new File(path));
    	}catch( Exception e) {
    		System.out.println("Could not load airplane image.");
    		e.printStackTrace();
    		
    	}
    	
return img;
    }
    
}

class drawPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	

	public drawPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        //this is where the intersection will be drawn and updated every frame.
        //we make the graphics object change colors based on current positioning
        //this is also where the image of the plane will be drawn, as well as speed
        
        //draw text
        
      
       /* g.setColor(Color.BLACK);
        g.drawString("Runway 1", x, y);
        g.drawString("Status:", x, y);
        g.drawString("Speed:", x, y);
        g.drawString("Runway 2", x, y);
        g.drawString("Status:", x, y);
        g.drawString("Speed:", x, y); */
        
  
       
       
        //draw runway and planes based on location
        if(TheSeerSystem.currRunwayOneOcc == true) {
        	//plane on runway 1
        	g.setColor(Color.RED);
            g.fillRect(100,450,800,50); //sensor 1 is the horizontal 
        	int x = TheSeerSystem.smoothDistance(TheSeerSystem.currRunwayOneDist);
        	g.drawImage(TheSeerSystem.planeImage, x, 450, 60,60,null);
        }else {
        	//no plane on runway 1
        	g.setColor(Color.GREEN);
            g.fillRect(100,450,800,50); //sensor 1 is the horizontal 
        	//draw alternative text
        }
        
        if(TheSeerSystem.currRunwayTwoOcc == true) {
        	//plane on runway 2

            g.setColor(Color.RED);
            g.fillRect(475,100,50,800); //sensor 2 is the vertical
        	int y = TheSeerSystem.smoothDistance(TheSeerSystem.currRunwayTwoDist);
        	g.drawImage(TheSeerSystem.planeImage, 475, y, 60,60,null);
        }else {
        	//no plane on runway 2
            g.setColor(Color.GREEN);
            g.fillRect(475,100,50,800); //sensor 2 is the vertical
        }
        
    
    
    }
}



	

	