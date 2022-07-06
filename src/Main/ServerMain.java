package Main;


import java.awt.Dimension;

import javax.swing.JFrame;

import TCP.ConnectionManager;

public class ServerMain {

	public static final String StopCode = "STOP";
	public static final int PORT = 8080;
	
	public static void main(String[] args) {
		createWindow();
		
		CustomFrame cF = new CustomFrame();
		
		
		ConnectionManager cm = new ConnectionManager(StopCode, PORT);
		cm.init();
		
		while(true) {
			if(!cm.running) {System.out.println("NEW CONNECTIONMANAGER CREATED"); cm = new ConnectionManager(StopCode, PORT); cm.init();}
			System.out.println("Loopcheck");
		}
	}
	
	public static void createWindow() {
		CustomFrame cF = new CustomFrame();
		cF.createCustomFrame();
		/*JFrame frame = new JFrame("SchachServer");
		//frame.add(cF);
		frame.setPreferredSize(new Dimension(300, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);*/
		
		
		
		
	}	
}

