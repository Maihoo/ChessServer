package TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {
	public boolean normal 	= true;
	public boolean running 	= true;
	public boolean firstReady, secondReady;
	public int PORT = 0;
	public int amount = 0;
	public String turn 	= "white";
	public String StopCode = "STOP";
	public String fieldData, tempSafeForRedo;
	public Socket socket;
	public TCPServerThread tcp1;
	public TCPServerThread tcp2;
	public ServerSocket serverSocket;
	
	
	public ConnectionManager(String StopCode, int PORT) {
		this.StopCode = StopCode;
		this.PORT = PORT;
	}
	
	public void run() {
		running = true;
		
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
			System.out.println("started at: " + ip + ":" + PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (running) {
			
			System.out.println("------------------");
			try {
				System.out.println("| Client1 | " + tcp1.running + " |");
			} catch (Exception e) {
				System.out.println("| Client1 | " + "false" + "|");
			}
			System.out.println("------------------");
			try {
				System.out.println("| Client2 | " + tcp2.running + " |");
			} catch (Exception e) {
				System.out.println("| Client2 | " + "false" + "|");
			}
			System.out.println("------------------");
			
			
			try {
				this.socket = serverSocket.accept();
				amount ++;
				System.out.println("socket Nr " + amount + " accepted");
            
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			}
			
			// new thread for a client
			if(tcp1 == null) {
				tcp1 = new TCPServerThread(this, socket, normal, 1);
				tcp1.start();
				System.out.println("TCP1 created");
			}
			else if(tcp2 == null) {
				tcp2 = new TCPServerThread(this, socket, !normal, 2);
				tcp2.start();
				System.out.println("TCP2 created");
			}
			else {
				System.out.println("NEW VIEWER");
				new TCPServerThreadViewer(this, socket, amount).start();
			}
		}
	}
	
	public void init() {
		reset();
		
		serverSocket 	= null;
		
		//create Server Socket
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("SOCKET CREATED");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		run();
	}
	
	public void reset() {
		fieldData = 
				"turm.0.1.0.0:"  + "springer.0.1.0.0:" + "laeufer.0.1.0.0:" + "dame.0.1.0.0:" 	+ "koenig.0.1.0.0:"+ "laeufer.0.1.0.0:" + "springer.0.1.0.0:" + "turm.0.1.0.0:"  +
			 	"bauer.0.1.0.0:" + "bauer.0.1.0.0:"    + "bauer.0.1.0.0:"   + "bauer.0.1.0.0:"  + "bauer.0.1.0.0:" + "bauer.0.1.0.0:"   + "bauer.0.1.0.0:"    + "bauer.0.1.0.0:" + 
			 	"0.0.0.0.0:"     + "0.0.0.0.0:"        + "0.0.0.0.0:"       + "0.0.0.0.0:"      + "0.0.0.0.0:"     + "0.0.0.0.0:"       + "0.0.0.0.0:"        + "0.0.0.0.0:"     + 
			 	"0.0.0.0.0:"     + "0.0.0.0.0:"        + "0.0.0.0.0:"       + "0.0.0.0.0:"      + "0.0.0.0.0:"     + "0.0.0.0.0:"       + "0.0.0.0.0:"        + "0.0.0.0.0:"     + 
			 	"0.0.0.0.0:"     + "0.0.0.0.0:"        + "0.0.0.0.0:"       + "0.0.0.0.0:"      + "0.0.0.0.0:"     + "0.0.0.0.0:"       + "0.0.0.0.0:"        + "0.0.0.0.0:"     + 
			 	"0.0.0.0.0:"     + "0.0.0.0.0:"        + "0.0.0.0.0:"       + "0.0.0.0.0:"      + "0.0.0.0.0:"     + "0.0.0.0.0:"       + "0.0.0.0.0:"        + "0.0.0.0.0:"     + 
			 	"bauer.1.0.0.0:" + "bauer.1.0.0.0:"    + "bauer.1.0.0.0:"   + "bauer.1.0.0.0:"  + "bauer.1.0.0.0:" + "bauer.1.0.0.0:"   + "bauer.1.0.0.0:"    + "bauer.1.0.0.0:" + 
			 	"turm.1.0.0.0:"  + "springer.1.0.0.0:" + "laeufer.1.0.0.0:" + "dame.1.0.0.0:"   + "koenig.1.0.0.0:"+ "laeufer.1.0.0.0:" + "springer.1.0.0.0:" + "turm.1.0.0.0:"  +
			 	"1.0.0.0" + ":00:00";
		amount = 0;
		turn = "white";
		if(Math.random()<0.5) normal = false;
		else normal = true;
		
		socket 			= null;
		tcp1 			= null;
		tcp2 			= null;
	}
	
	public void terminate(int nr) throws IOException {
		System.out.println("Termination for " + nr);
		
		if(nr == 1) {
			tcp1.running = false;
			tcp1.socket.close();
			tcp1 = null;
		}
		if(nr == 2) {
			tcp2.running = false;
			tcp2.socket.close();
			tcp2 = null;
		}
		
		System.out.println("REEEEEEEE");
		
		if(tcp1 == null && tcp2 == null) reset();
	}
	
	public boolean redoCheck() throws IOException {
		if(tcp1.wantsToRedo && tcp2.wantsToRedo) return true; 
		else return false;
	}
	
	public void redo() throws IOException {
		tcp1.wantsToRedo = false;
		tcp2.wantsToRedo = false;
		fieldData = tempSafeForRedo;
	}
	
	public boolean checkRedo(int nr) throws IOException {
		if(nr == 1 && tcp2 != null) return tcp2.wantsToRedo;
		if(nr == 2 && tcp1 != null) return tcp1.wantsToRedo;
		return false;
	}
	
	public void switchTurn() {
		System.out.print("Turn was " + this.turn);
		if(this.turn.equals("white")) this.turn = "schwarz";
		else this.turn = "white";
		System.out.println(", new turn is " + this.turn);
	}
	
	public boolean checkReady() {
		try {
			if(tcp1.running && tcp2.running) return true;
		} catch (Exception e) {}
		return false;
	}
	
	public void setField(String tempField) {
		tempSafeForRedo = fieldData;
		fieldData = tempField;
	}
	
	public String getTurn() {
		System.out.println("returned turn = " + turn);
		return turn;
	}
	
	public String getSomethingIDK() {
		return "IDK";
	}
	
	public int getAmount() {
		return amount;
	}
	
	public String getField() {
		return fieldData;
	}
	
	public void breakConnection(int nr) throws IOException {
		if(nr == 1 || nr == 2) reset(); 
	}
}
