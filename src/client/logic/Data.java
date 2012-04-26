// class to hold the information to be processed of each simulation 
package client.logic;

import java.util.Vector;


class Data {
	private Vector<Node> listNode=null;
	private int detection;
	
	Data( Vector<Node> ln, int d){
		listNode= ln;
		detection=d;
	}
	
	// function to return listNode
	public Vector<Node> getListNode(){ return listNode; }
	
	// function to return detection parameter (it indicates if in the simulation, at least a node does the detection )
	public int getDetection(){ return detection; }
		
	
}
