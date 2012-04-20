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
		ReadURL reader = new ReadURL(connect );
		Hypervisor hyper = new Hypervisor( connect );
		ElaboratorData elaborator = new ElaboratorData( connect);
		Gui mf= new Gui(connect, "Simulation" );
		
		connect.setParameters( hyper, mf, reader, elaborator, client );
	}
}
