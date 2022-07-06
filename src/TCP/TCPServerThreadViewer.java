package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Timestamp;

public class TCPServerThreadViewer implements Runnable{
	
	public int nr;
    ConnectionManager cm;
    Socket socket;
    Thread thread;
    Timestamp ts;
    
    public TCPServerThreadViewer(ConnectionManager cm, Socket clientSocket, int nr) {
    	this.cm = cm;
    	this.socket = clientSocket;
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
        while (true) {
            try {
                line = br.readLine();
                
                switch(line) {
                case "QUIT" :
                    socket.close();
                    return;
                case "field": 
                	String tempField = cm.getField();
                	System.out.println("client " + nr + " asked for a field update. Answer: " + tempField);
                	os.writeBytes(tempField + "\n\r");
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
                e.printStackTrace();
                //resetCM();
                System.out.println("Viewer Client " + nr + "+ crashed.");
                return;
            }
        }
    }
    
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
	}
}
