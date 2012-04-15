


//import logic.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.awt.event.*;


public class MyFrame extends JFrame{
	Connector connect=null;
	
	JLabel label1=new JLabel("URL read value:");
	JLabel label2=new JLabel("URL sent value:");
	
	// link mio dropbox
	JTextField tf1 = new JTextField("http://dl.dropbox.com/u/24729735/project_configLSM.txt");
	// link prof
	//JTextField tf1 = new JTextField("http://www.math.unipd.it/~conti/teaching/PCD1112/project_config.txt");
	JTextField tf2 = new JTextField("localhost");
	
	JButton start = new JButton("Start");
	JButton stop = new JButton("Stop");
	JButton setUrl = new JButton("Default URL");

	JTextArea ta = new JTextArea(35,40);
	
	
	// classe che gestisce la pressione del bottone start
	class startClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
		    ta.setText(""); // ripulisco tutto il testo in ta 
			ta.append("Inizio ricerva file..\n");
			
			if( connect.readFile( tf1.getText() ) ){
				ta.append("File letto correttamente..\n" + "Inizio simulazioni..\n");
				start.setEnabled(false);
			}
		}
	}
	
	// classe che gestisce la pressione del bottone stop
	class stopClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//ta.setText("");
			closeAll();
			System.exit(0);
			//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // associamo l'evento di chiusura al solito bottone di chiusura
		}
	}
	
	// classe che gestisce la pressione del bottone setUrl
	class setUrlClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			ta.setText("");
			tf1.setText("http://www.math.unipd.it/~conti/teaching/PCD1112/project_configRED.txt");
			tf2.setText("localhost");
		}
	}
	
	public class SimpleAWTEvent extends AWTEvent{
		public static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;
		private String str;
		private int percent;

		SimpleAWTEvent( Object target, String str, int percent){
			super( target, EVENT_ID);
			this.str = str;
			this.percent = percent;
		}
		
//	public class PrintEventListener implements Listener{
		
		
//	}

		public String getStr() { return( str ); }

		public int getPercent() { return( percent ); }
	}
	
	public MyFrame(Connector c, String title){  // da togliere public !!!!!!!!!!!!!
		super( title );
		connect=c;
		
		setSize(650, 600);
//		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		
		// collego i bottoni ai loro rispettivi listener
		start.addActionListener(new startClick());
		stop.addActionListener(new stopClick());
		setUrl.addActionListener( new setUrlClick());

		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		 
		add(panel);
		  
		   
		JPanel fr= new JPanel();
		fr.setSize( 50, 40);
		
		fr.add(start);
		fr.add(stop);
		fr.add(setUrl);

		JScrollPane sc = new JScrollPane(ta);
		   
		// Turn on automatically adding gaps between components: crea in automatico lo spazio tra un componente e l'altro
		layout.setAutoCreateGaps(true);
		 
		// Turn on automatically creating gaps between components that touch
		// the edge of the container and the container.
		layout.setAutoCreateContainerGaps(true);
		 
		// Create a sequential group for the horizontal axis.
		 
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		 
		// The sequential group in turn contains two parallel groups.
		// One parallel group contains the labels, the other the text fields.
		// Putting the labels in a parallel group along the horizontal axis
		// positions them at the same x location.
		//
		// Variable indentation is used to reinforce the level of grouping.
		hGroup.addGroup(layout.createParallelGroup().
			addComponent(label1).addComponent(label2));
		hGroup.addGroup(layout.createParallelGroup().
			addComponent(tf1).addComponent(tf2).addComponent(fr).addComponent(sc) );

		
		layout.setHorizontalGroup(hGroup);
		   
		// Create a sequential group for the vertical axis.
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		 
		// The sequential group contains two parallel groups that align
		// the contents along the baseline. The first parallel group contains
		// the first label and text field, and the second parallel group contains
		// the second label and text field. By using a sequential group
		// the labels and text fields are positioned vertically after one another.
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		   addComponent(label1).addComponent(tf1));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		   addComponent(label2).addComponent(tf2));
		   
		// meglio fare array di parallel gruop per salvare tutte le colonne e le righe!
		GroupLayout.ParallelGroup pg2 = layout.createParallelGroup(Alignment.CENTER);
		vGroup.addGroup(pg2);
		pg2.addComponent(fr, Alignment.CENTER);
		  
		GroupLayout.ParallelGroup pg3 = layout.createParallelGroup(Alignment.CENTER);
		vGroup.addGroup(pg3);
		pg3.addComponent(sc);


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

		enableEvents( SimpleAWTEvent.EVENT_ID);
		
	}
	
	
	public void printDisplay( String s ){
		synchronized( ta ){
			//Object target = "Instance of the main thread";
			//EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
			//eventQueue.postEvent( new SimpleAWTEvent( target, s, 1 ));
			
			ta.append( s + "\n");
		}
	}
	
	public void cancelDisplay(){
		ta.setText("");
	}
	
	private void closeAll(){
		connect.closeAll();
	}
	
	protected void processEvent( AWTEvent event){
		if ( event instanceof SimpleAWTEvent ){
			SimpleAWTEvent ev = (SimpleAWTEvent) event;
			ta.setText( ev.getStr() );         // access GUI component
			//progressBar.setValue( ev.getPercent() );  // access GUI component
		}
		else{ // other events go to the system default process event handler
			super.processEvent( event );
		}
	}


}
