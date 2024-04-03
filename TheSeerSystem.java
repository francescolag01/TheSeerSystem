

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
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

public class TheSeerSystem {
	public static JFrame f;
	public static Image planeImage;
   
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
                	int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                	System.out.println("Read " + numRead + " bytes.");
                		
             	   //determine the current conditions of the runway based on read data
             	 
             	   //TODO:What is the main functionality of the software?
	                	//Popups stating arrivals, departures, near misses, and collisions
	                	//the runways changing color based on if there is a plane or not
	                	//draw a plane icon on the screen, relative to the physical plane's location on the runway
                		//near the plane icon, draw its speed to the screen
                		
                	
                	//TODO: What features can we add for polish?
                		//draw text to label useful information
             	   		//change direction of plane if it goes backwards
                		//add a logo
                		//settings
             	   //redraw the screen

                  
                }
             } catch (Exception e) {
            	 e.printStackTrace();
            	 }
             comPort.closePort();
            	  
                
               
            
            }});
    }
    

public void CloseCallPopUp() {
	
	JOptionPane.showMessageDialog(TheSeerSystem.f,"DANGER: AIRCRAFT ARE LESS THAN 90000 FEET APART.");
	
}
public void CrashPopUp() {
	
	JOptionPane.showMessageDialog(TheSeerSystem.f,"OH NO: AIRCRAFT HAVE COLLIDED ON RUNWAY 20 AND 23.");
}

public void ArrivalPopUp() {
	
	JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has landed on runway 12");
}

public void DeparturePopUp() {
	
	JOptionPane.showMessageDialog(TheSeerSystem.f,"Plane has taken off from runway 13");
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

    
public Color getColor(String sensorData) {
	   
	   Color C = Color.GREEN;
	   
	   if(true) {
		   //this will trigger if there is a plane on the runway. 
		   C = Color.RED;
		  
	   }
	   
	   return C;
   }
}

class drawPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public drawPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        //this is where the intersection will be drawn and updated every frame.
        //we make the graphics object change colors based on current positioning
        //this is also where the image of the plane will be drawn, as well as speed and distance
        
        //draw text
        g.setColor(Color.BLACK);
        
        // Draw Runways
        g.setColor(Color.GREEN);
        g.fillRect(100,450,800,50); //sensor 1
        g.setColor(Color.GREEN);
        g.fillRect(475,100,50,800); //sensor 2
        
        //draw planes based on location
        /*if(sensor 1 or 2 detected)}
    
        g.drawImage(getPlaneImg(), string sensordata)
    	}
    */
    }
}



/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
	

	