// gui for users
package client.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;

import client.logic.Connector;


public class Gui extends JFrame{
	private static final long serialVersionUID = 5L;  // unique id
	private Connector connect=null;
	
	private JLabel labelUrlRead=new JLabel("URL read value:");
	private JLabel labelUrlSent=new JLabel("URL sent value:");
	private JLabel labelInformationSimulation=new JLabel("Simulation progress:");
	private JLabel labelOutputSent=new JLabel("Value to sent:");
	
	// link mio dropbox !!!
	private JTextField urlToRead = new JTextField("http://dl.dropbox.com/u/24729735/project_configLSM.txt");
	// link prof !!!!
	//JTextField urlToRead = new JTextField("http://www.math.unipd.it/~conti/teaching/PCD1112/project_config.txt");
	private JTextField urlToSend = new JTextField("localhost");
	
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JButton setUrl = new JButton("Default URL");

	private JTextArea textAreaInformation = new JTextArea(35,40);
	private JTextArea textAreaOutput = new JTextArea(35,40);
	
	
	// ActionListener for start button
	class startClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
		    textAreaInformation.setText(""); 
			textAreaInformation.append("Start searching files..\n");
			
			if( connect.readFile( urlToRead.getText() ) && connect.connectToServer( urlToSend.getText() ) ){
				start.setEnabled(false);
				textAreaInformation.append("File read correctly..\n" + "Start simulation..\n");
				connect.start();
			}
		}
	}
	
	// ActionListener for stop button
	class stopClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			closeAll();
			System.exit(0);
		}
	}
	
	// ActionListener for setUrl button
	class setUrlClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			urlToRead.setText("http://www.math.unipd.it/~conti/teaching/PCD1112/project_config.txt");
			urlToSend.setText("localhost");
		}
	}
	
	// class to manage gui event
	class GuiEvent extends AWTEvent{
		private static final long serialVersionUID = 6L;  // unique id
		public static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;
		private String str;

		GuiEvent( Object target, String str){
			super( target, EVENT_ID);
			this.str = str;
		}
		
		public String getStr() { return( str ); }
	}
	
	
	public Gui(Connector c, String title){
		super( title );
		connect=c;
		
		setSize(900, 630);
		
		// buttons are connected to their listeners
		start.addActionListener(new startClick());
		stop.addActionListener(new stopClick());
		setUrl.addActionListener( new setUrlClick());
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		 
		add(panel);
		  		   
		JPanel panelForButton= new JPanel();
		panelForButton.setSize( 50, 40);
		
		panelForButton.add(start);
		panelForButton.add(stop);
		panelForButton.add(setUrl);

		JScrollPane scrollPaneInformation = new JScrollPane(textAreaInformation);
		JScrollPane scrollPaneOutput = new JScrollPane(textAreaOutput);
		   
		// turn on automatically adding gaps between components (crea in automatico lo spazio tra un componente e l'altro)
		layout.setAutoCreateGaps(true);
		 
		// Turn on automatically creating gaps between components that touch the edge of the container and the container
		layout.setAutoCreateContainerGaps(true);
		 
		// create a sequential group for the horizontal axis	 
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		 		hGroup.addGroup(layout.createParallelGroup().
			addComponent(labelUrlRead).addComponent(labelUrlSent).addComponent(labelInformationSimulation).addComponent(labelOutputSent) );
		hGroup.addGroup(layout.createParallelGroup().
			addComponent(urlToRead).addComponent(urlToSend).addComponent(panelForButton).addComponent(scrollPaneInformation).addComponent(scrollPaneOutput) );

		layout.setHorizontalGroup(hGroup);
		   
		// create a sequential group for the vertical axis.
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		   addComponent(labelUrlRead).addComponent(urlToRead));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		   addComponent(labelUrlSent).addComponent(urlToSend));
		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).
				addComponent(panelForButton, Alignment.CENTER) );
		vGroup.addGroup( layout.createParallelGroup(Alignment.CENTER).
				addComponent(labelInformationSimulation, Alignment.LEADING).addComponent(scrollPaneInformation) );
		vGroup.addGroup( layout.createParallelGroup(Alignment.CENTER).
				addComponent(labelOutputSent, Alignment.LEADING).addComponent(scrollPaneOutput) );

		layout.setVerticalGroup(vGroup);

		
		addWindowListener( new WindowAdapter() {
			   public void windowClosing(WindowEvent we) {
			      System.out.println("E' stato premuto il pulsante X rosso");
			      if( ! start.isEnabled() )
			    	  connect.closeAll();
			      System.exit(0);
			   }
			   public void windowClosed(WindowEvent we) {
				  System.out.println("E' stato premuto il pulsante X");
				  if( ! start.isEnabled() )
					  connect.closeAll();
				  System.exit(0);
			   }
		});
   
		   
		setVisible(true);

		enableEvents( GuiEvent.EVENT_ID);
	}
	
	
	// function to print some text in the Gui's text areas
	public void printDisplay( String s, int position){
		if(position == 0)
			synchronized( textAreaInformation ){
				textAreaInformation.append( s + "\n");
			}
		else
			synchronized( textAreaOutput ){			
				textAreaOutput.append( s );
			}
	}
	
	// function to clear both text area in GUI
	public void clearDisplay(){
		textAreaInformation.setText("");
		textAreaOutput.setText("");
	}
	
	// function which terminates all active threads 
	private void closeAll(){
		connect.closeAll();
	}
	
	
	protected void processEvent( AWTEvent event){
		if ( event instanceof GuiEvent ){
			GuiEvent ev = (GuiEvent) event;
			textAreaInformation.setText( ev.getStr() );         // access GUI component
		}
		else{ // other events go to the system default process event handler
			super.processEvent( event );
		}
	}


}
