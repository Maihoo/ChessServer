package Main;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class CustomFrame extends JFrame {
	  public void createCustomFrame() {
	    Console console = new Console();
	    console.init();
	 
	    /*CustomFrame launcher = new CustomFrame();
	    launcher.setVisible(true);
	    console.getFrame().setLocation(
	        launcher.getX() + launcher.getWidth() + launcher.getInsets().right,
	        launcher.getY());*/
	  }

	  CustomFrame() {
	    super();
	    setSize(1000, 600);
	    
	    setResizable(true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	  }
	}

	class Console {
	  final JFrame frame = new JFrame("SchachServer");
	  public Console() {
	    JTextArea textArea = new JTextArea(24, 80);
	    textArea.setBackground(Color.BLACK);
	    textArea.setForeground(Color.LIGHT_GRAY);
	    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
	    
	    JScrollPane scrollPane = new JScrollPane( textArea );
	    scrollPane.setAutoscrolls(true);
	    
	    new SmartScroller( scrollPane );
	    
	    System.setOut(new PrintStream(new OutputStream() {
	      @Override
	      public void write(int b) throws IOException {
	        textArea.append(String.valueOf((char) b));
	      }
	    }));
	    
	    frame.add(scrollPane);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	  }
	  public void init() {
	    frame.pack();
	    frame.setVisible(true);
	  }
	  public JFrame getFrame() {
	    return frame;
	  }
	}