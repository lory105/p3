
//import gui.*;

public class Main {
	
	public static void main(String[] args){
		Connector connect= new Connector();

		Client client = new Client( connect );
		
		ReadURL reader = new ReadURL(connect );
		Hypervisor hyper = new Hypervisor( connect );
		ElaboratorData elaborator = new ElaboratorData( connect);
		MyFrame mf= new MyFrame(connect, "Simulation" );
		
		connect.setParameters( hyper, mf, reader, elaborator, client );
	
	}
}
