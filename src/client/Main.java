package client;

import client.gui.Gui;
import client.logic.Connector;
import client.logic.ElaboratorData;
import client.logic.Hypervisor;
import client.logic.ReadURL;


public class Main {
	
	public static void main(String[] args){

		Connector connect= new Connector();
		Client client = new Client( connect );
		ReadURL reader = ReadURL.getInstance(connect);
		Hypervisor hyper = Hypervisor.getInstance( connect );
		ElaboratorData elaborator = ElaboratorData.getInstance(connect);
		Gui mf= new Gui(connect, "Simulation" );
		
		connect.setParameters( hyper, mf, reader, elaborator, client );
	}
}
