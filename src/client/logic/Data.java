// class to hold the information to be processed of each simulation 
package client.logic;

import java.util.Vector;


public class Data {
	private Vector<Node> listNode=null;
	private int detection;
	
	Data( Vector<Node> ln, int d){
		listNode= ln;
		detection=d;
	}
	
	public Vector<Node> getListNode(){ return listNode; }
	public int getDetection(){ return detection; }
		
	
}
