package Client;
// object used to activate the nodes for each simulation

import java.util.*; 

public class ActivateNode extends Thread{
	
	Vector<Node> listNode;
	
	ActivateNode( Vector<Node> ls){
		listNode=ls;
	}
	
	public void run(){
		for( int x=0; x<listNode.size(); x++ )
			listNode.get(x).start();
	}
}