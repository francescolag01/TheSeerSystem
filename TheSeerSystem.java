

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class TheSeerSystem {
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
               
            }
        });
    }

    private static void createAndShowGUI() {
    
        JFrame f = new JFrame("The Seer System");
        f.setSize(new Dimension(1000,1000));
        f.setPreferredSize(new Dimension(1000,1000));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new drawPanel());
        f.pack();
        f.setVisible(true);
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
        g.fillRect(475,100,50,800); //sensor 1
        
        //draw planes based on location
        
    }
}
	


	
	
