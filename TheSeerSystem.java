

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//I downloaded this library from https://fazecast.github.io/jSerialComm/ , to use the Arduinos serial interface
import com.fazecast.jSerialComm.SerialPort;

public class TheSeerSystem{
	public static JFrame f; // the frame displaying the application
	public static drawPanel dp; // the panel that draws images to the screen
	public static Image planeImage; // plane icon drawn on screen
	public static SerialPort comPort;
	//TODO: DEFINE THE FOLLOWING FINAL VARIBLES
	static final double delay = 250; //The TOTAL delay between sensor readings. I think we need to factor in 
	static final int closeCallDist = 300; // the threshold to trigger a close call popup
	static final int collisionDist = 20; // the threshhold to trigger a collision popup
   //runway total length is 59 inches
	public static int prevRunwayOneDist, prevRunwayTwoDist = 0; // the last distances detected by sensors one and two, used to calculate speed
	public static int currRunwayOneDist, currRunwayTwoDist = 0; // the current distances detected by sensors one and two
	public static int runwayOneSpeed, runwayTwoSpeed; // the current speed of planes on the runway
	public static boolean prevRunwayOneOcc,prevRunwayTwoOcc = false; // True if a plane is on the runway last timestep
	public static boolean currRunwayOneOcc,currRunwayTwoOcc = false; // True if a plane is on the runway currently
	
	
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	//multithreading babay
            	//the first iteration of this all ran in one thread. Simply only putting the GUI code in its own thread increseases performance by a ton
            	
            	//initializes the GUI
                createAndShowGUI();
             
                
               
            }});
        
        		//load and store the plane image
                planeImage = loadPlane();
                
                //establish serial connection to the Arudino
                //we create the comport obj and open the arudino port
                comPort = SerialPort.getCommPort("COM5"); // or whatever port we end up using
                //If the arduino isnt hooked up on the port above, program throws an error  
                comPort.openPort();
                
                try {
              //THE INTERNET TELLS ME THIS MIGHT FIX OUR ISSUE
            	Thread.sleep(1000);// 10 second sleep to initialize the arduino
           
                if(!comPort.isOpen()) {
                	//port didnt open, uh oh	
                		
                	throw new Exception("Could not connect to the Arudino.");	
                	}
                
                comPort.setBaudRate(9600);	
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
                
                } catch(Exception e) {
                	e.printStackTrace();//haha
                }
                //  comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,0, 0); //changes the port mode but I dont think we need it.
                
                /* //testing
                Stack<String> testValues = new Stack<>();
                for(int i = 0; i < 59; i++) {
                	if (i < 10) {
                		testValues.push(String.format("o00%do00%d",i,i));
                	}else {
                		testValues.push(String.format("o0%do0%d",i,i));
                	}
                }  */ //testing
                byte[] test = new byte[comPort.bytesAvailable()];
                int clear = comPort.readBytes(test, test.length);
                
                try {
                while (true)
                {
                	
                	
                	 
                	//System.out.println(comPort.isOpen());
                	//System.out.println(comPort.bytesAvailable());
                	
                		
                	
                	
                	//wait for the serial interrupt
                	while(comPort.bytesAvailable() == 0) {
                		//there is no message to read, we wait
                	     
                		//System.out.println("waiting");
                	}
                	//read the serial port
                	//TODO: Determine how we want to send the message from the arduino, and how to process it in this application 
                		//done, I expanded on ryans idea and changed it a bit.
             	   	byte[] readBuffer = new byte[10];
             	    //we convert the byte buffer into one big string displaying the data of the two sensors
             		int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                	String msg = new String(readBuffer);
                	System.out.println(msg);
                	if(!(msg.charAt(0) =='-')) {
                		//msg.replace("\0", "");
                    	//removes the null terminator from the string. This might cause issues if we dont remove it
                    	//because of how we manipulate the strings manually below, we dont have to worry about causing an error due to the string not having it.
                    	
                    	// String msg = testValues.pop(); //testing
                    	 //String msg = "o050o050"; //testing
                    	
                    	//messages will be in the form of XiiiXiii, where
                    	//X is either f for 'free' or o for 'occupied'
                    	//iii are 3 digits displaying its position relative to the start of the sensor.
                    	
                    	//this program tracks occupancy and distance, and it also tracks the occupancy and distance of the last sensor reading.
                    	
                    	prevRunwayOneOcc = currRunwayOneOcc;
                    	currRunwayOneOcc = msg.charAt(0) == 'f' ? false : true;
                    	prevRunwayOneDist = currRunwayOneDist;
                    	currRunwayOneDist = Integer.parseInt(msg.substring(1,4));
                    	
                    	prevRunwayTwoOcc = currRunwayTwoOcc;
                    	currRunwayTwoOcc = msg.charAt(4) == 'f' ? false : true;
                    	prevRunwayTwoDist = currRunwayTwoDist;
                    	currRunwayTwoDist = Integer.parseInt(msg.substring(5,8));
                    	
                    	
                    	//We use this to calculate the speed of each aircraft, and also to track arrivals and departures.
                    	
                    	//if the runway was previously free, and a plane lands, an arrival popup will display
                    	//if the runway was previously occupied, and a plane leaves, a departure popup will display
                    	
                    	//first we handle popups
                    	//runway one 
                    	
                    	if(prevRunwayOneOcc == false && currRunwayOneOcc == true) {
                    		// arrival
                    		//JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has landed on runway 1");
                    	}else if(prevRunwayOneOcc == true && currRunwayOneOcc == false) {
                    		//departure
                    	//	JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has taken off from runway 1");
                    	}else {
                    		//no popup, do nothing
                    	}
                    	
                    	//runway two
                    	if(prevRunwayTwoOcc == false && currRunwayTwoOcc == true) {
                    		// arrival
                    		//JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has landed on runway 2");
                    	}else if(prevRunwayTwoOcc == true && currRunwayTwoOcc == false) {
                    		//departure
                    		//JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has taken off from runway 2");
                    	}else {
                    		//no popup, do nothing
                    	}
                    	
                    	if(computeDistance(currRunwayOneDist,currRunwayTwoDist) >= collisionDist) {
                    		//collision (oh no)
                    	//	JOptionPane.showMessageDialog(TheSeerSystem.f,"OH NO: AIRCRAFT HAVE COLLIDED ON RUNWAY 1 AND 2.");
                    	}else if(computeDistance(currRunwayOneDist,currRunwayTwoDist) >= closeCallDist) {
                    		//close call popup
                    	//	JOptionPane.showMessageDialog(TheSeerSystem.f,"DANGER: AIRCRAFT ARE LESS THAN 300 FEET APART.");
                    	}else {
                    		//no danger of collision, do nothing
                    		
                    	}
                    	//now we check their distances, and see if they are close enough to be a close call or a collision
                    	
                    	
                 	   //TODO:What is the main functionality of the software?
    	                	//Popups stating arrivals, departures, near misses, and collisions REMAKE THEM SO THEY APPEAR IN THE CORNER, and DONT STOP WHOLE PROGRAM
    	                	//the runways changing color based on if there is a plane or not COMPLETE
    	                	//draw a plane icon on the screen, relative to the physical plane's location on the runway. DONE, WORKS IN TESTING. TEST WITH REAL SENSOR READY
                    		//On a panel, display runway number, plane occupancy, and speed COMPLETE
                    		//error correction for sensor misreads
                    		
                    	
                    	//TODO: What features can we add for polish?
                    		//make the popups not appear in the middle of the screen
                    		//draw text to label useful information
                 	   		//change direction of plane if it goes backwards
                    		//add a logo
                    		//settings
                    		//make the runway intersection look prettier
                    	
                    	//TODO: potential problems I forsee
        				//the sensor could be jittery and cause the icon of the plane to fly around the screen, give innacurate readings, or make 10000 popups appear
        				//something crashing during a demo, and us looking stupid. Must run the program for a very long time to see if any bugs arise
        				//what happens when the program is activated and things are on the runway? or if theyre all off the runway?
                    		//I tested this by creating a string with the format (String msg = "o012f100";), and the program starts correctly in all configurations.
                    	//when I simulate the planes crashing, the program gets stuck in an infinite popup loop. 
                    	//all of the text, images, and shapes are drawn to the screen with hand drawn coordinates.
                    	//the program stops running when a popup opens (run it in another thread)
                    	
                    	//redraw the screen to update the images
                		
                    	dp.update(dp.getGraphics());
                	}else {
                		System.out.println("Error: Sensor Misread");
                	}
                	
                	 
                  
                }
             } catch (Exception e) {
            	 e.printStackTrace();
            	
            	 }
             comPort.closePort();
            	  
                
               
            
            
    }
public static double calculateSpeed(int prev, int curr) {
	//calculate speed using the prev and current distance
	//and the delay of time inbetween reads of the sensor.
	
	//USE THE REAL LIFE SENSOR READINGS!
	return ((curr- prev) / delay);
}
    
    
public static double scaleDistance(int distance) {
	//this function takes in the real life distance of the plane,
	//and scales it so the icon on the screen is proportional to the real life distance.
	//TODO: DETERMINE THE FACTOR THAT SCALES IT THE MOST APPROPRITATELY
	double factor = 14.9;
	double dist = distance * factor;
	//System.out.println(distance + " " + dist);
	return dist;
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
        dp = new drawPanel();
        f.add(dp);
        f.pack();
        f.setVisible(true);
        
        f.addWindowListener( new WindowAdapter()
        {
          public void windowClosing(WindowEvent e)
           {
        	  //closes the serialport when the window closes
        	  //because we are polite
        	  comPort.closePort();
            }
         });
       
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
      //shifts the location of the text, for easy changing later
      int a = 100;
      int b = 0;
      //TODO: make a font that makes the text look bigger and nicer
     // g.setFont();
      
       g.setColor(Color.BLACK);
        g.drawString("Runway 1", 600+a, 600+b);
        g.drawString("Status:", 600+a, 630+b);
        if(TheSeerSystem.currRunwayOneOcc == true) {
        	g.drawString("Occupied",640+a,630+b);
        	g.drawString(Double.toString(TheSeerSystem.calculateSpeed(TheSeerSystem.prevRunwayOneDist,TheSeerSystem.currRunwayOneDist)), 640+a, 660+b);
        }else {
        	g.drawString("Free",640+a,630+b);
        	g.drawString("N/A", 640+a, 660+b);
        	
        }
        
        g.drawString("Speed:", 600+a, 660+b);
        
         
        g.drawString("Runway 2", 600+a, 700+b);
        g.drawString("Status:", 600+a, 730+b);
        if(TheSeerSystem.currRunwayTwoOcc == true) {
        	g.drawString("Occupied",640+a,730+b);
        	g.drawString(Double.toString(TheSeerSystem.calculateSpeed(TheSeerSystem.prevRunwayTwoDist,TheSeerSystem.currRunwayTwoDist)), 640+a, 760+b);
        }else {
        	g.drawString("Free",640+a,730+b);
        	g.drawString("N/A", 640+a, 760+b);
        }
        	 g.drawString("Speed:", 600+a, 760+b);
        
  
       
       
        //draw runway and planes based on location
        if(TheSeerSystem.currRunwayOneOcc == true) {
        	//System.out.println("1 occupied");
        	//plane on runway 1
        	g.setColor(Color.RED);
            g.fillRect(100,450,800,50); //sensor 1 is the horizontal 
        	double x = TheSeerSystem.scaleDistance(TheSeerSystem.currRunwayOneDist);
        	
        	if (x < 90) {
         	   x = 90;
            }
        	
        	g.drawImage(TheSeerSystem.planeImage, (int)x, 445, 60,60,null);
        }else {
        	//no plane on runway 1
        	g.setColor(Color.GREEN);
            g.fillRect(100,450,800,50); //sensor 1 is the horizontal 
        	//draw alternative text
        }
        
        if(TheSeerSystem.currRunwayTwoOcc == true) {
        	//plane on runway 2
        	//System.out.println("2 occupied");
            g.setColor(Color.RED);
            g.fillRect(475,100,50,800); //sensor 2 is the vertical
            double y = TheSeerSystem.scaleDistance(TheSeerSystem.currRunwayTwoDist);
           if (y < 90) {
        	   y = 90;
           }
        	g.drawImage(TheSeerSystem.planeImage, 470,(int) y, 60,60,null);
        }else {
        	//no plane on runway 2
            g.setColor(Color.GREEN);
            g.fillRect(475,100,50,800); //sensor 2 is the vertical
        }
        
    
    
    }
}



	

	