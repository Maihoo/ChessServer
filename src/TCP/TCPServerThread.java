package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Timestamp;

public class TCPServerThread implements Runnable{
	
	public boolean iAmWhite, wantsToRedo, running;
	public int nr;
    ConnectionManager cm;
    Socket socket;
    Thread thread;
    Timestamp ts;
    
    public TCPServerThread(ConnectionManager cm, Socket clientSocket, boolean iAmWhite, int nr) {
    	this.cm = cm;
    	this.socket = clientSocket;
    	this.iAmWhite = iAmWhite;
    	this.nr = nr;
    }
    
    public void run() {
    	
        InputStream is = null;
        BufferedReader br = null;
        DataOutputStream os = null;
        
        try {
        	is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        while (running) {
            try {
                line = br.readLine();
                
                switch(line) {
                case "QUIT" :
                	cm.terminate(nr);
                    return;
                case "eRedo?":
                	System.out.println("client nr " + nr  + " asks if redo neccessarry");
                	if(cm.checkRedo(nr)) {
                		os.writeBytes("1" + "\n\r");
                		os.flush();
                	}
                	else {
                		os.writeBytes("0" + "\n\r");
                		os.flush();
                	}
                	break;
                case "redo" :
                	System.out.println("client " + nr + "now want's redo");
                	this.wantsToRedo = true;
                	if(cm.redoCheck()) {
                		cm.redo();
                		System.out.println("redo will happen");
                		os.writeBytes("do it" + "\n\r");
                	}
                	else {
                		System.out.println("Only this client wants redo so far");
                		os.writeBytes("got it" + "\n\r");
                	}
                	os.flush();
                	break;
                case "notReady":
                	System.out.print("client " + nr + " said he is not ready anymore.");
                	os.writeBytes("affirmative" + "\n\r");
                	cm.terminate(nr);
                	os.flush();
                	break;
                case "patt":
                	cm.turn = "patt";
                	System.out.println("Game is noch patt");
                	os.writeBytes("success" + "\n\r");
                	os.flush();
                	break;
                case "setField":
                	System.out.println("client " + nr + " wants to set Field. Answer: waiting");
                	os.writeBytes("waiting" + "\n\r");
                	os.flush();
                	line = br.readLine();
                	cm.setField(line);
                	System.out.println("client " + nr + " set Field: " + line);
                	os.writeBytes("success" + "\n\r");
                	os.flush();
                	wantsToRedo = false;
                	break;
                case "giveUpTurn":
                	cm.switchTurn();
                	System.out.println("client " + nr + " asked to give up turn. Answer: success");
                	os.writeBytes("success" + "\n\r");
                	os.flush();
                	break;
                case "field": 
                	String tempField = cm.getField();
                	System.out.println("client " + nr + " asked for a field update. Answer: " + tempField);
                	os.writeBytes(tempField + "\n\r");
                	os.flush();
                	break;
                case "sp" :
                	System.out.print("client " + nr + " asked if second Player is ready. ");
                	if(cm.checkReady()) {
                		System.out.println("Answer: 1");
                		os.writeBytes("1" + "\n\r");
                	}
                	else {
                		System.out.println("Answer: 0");
                		os.writeBytes("0" + "\n\r");
                	}
                	os.flush();
                    break;
                case "turn":
                	String reply = cm.getTurn();
                	System.out.println("client " + nr + " asked for turn. Answer: " + reply);
                	os.writeBytes(reply + "\n\r");
                	os.flush();
                	break;
                case "color":
                	System.out.print("client " + nr + " asked for its color. Answer was: ");
                	if(iAmWhite) {os.writeBytes("youAreWhite"  + "\n\r"); System.out.println("youAreWhite");}
                	else {os.writeBytes("youAreBlack"  + "\n\r"); System.out.println("youAreBlack");}
                	os.flush();
                	break;
                case "connection running":
                	System.out.println("confirmation connection is running to client " + nr);
                	os.writeBytes("connection running. " + nr + "\n\r");
                	os.flush();
                	break;
                default: 
                    os.writeBytes("Echo reply from client " + nr + ": " + line + "\n\r");
                    os.flush();
                    break;
                }

            } catch (IOException e) {
            	System.out.println("1");
            	try {
            		cm.terminate(nr);
            	}catch(Exception a) { System.out.println("2");}
                System.out.println("Client " + nr + " crashed.");
                return;
            }
        }
    }
    
	public synchronized void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}
}
