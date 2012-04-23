// class used to activate the nodes for each simulation
package client.logic;

import java.util.*; 


public class ActivatorNodes extends Thread{
	
	static ActivatorNodes instance=null;
	
	Vector<Vector<Node>> bufferListNode= new Vector<Vector<Node>>();
	
	private ActivatorNodes(){}

	static ActivatorNodes getInstance(){
		if( instance== null )
			return new ActivatorNodes();
		return instance;
	}
	
	public void run(){
		Vector<Node> vn=null;
		try{
		while(true){
			if( isInterrupted() ) throw new SecurityException();
			synchronized (bufferListNode) {
				while( bufferListNode.isEmpty() ){
					bufferListNode.wait();
				}
				vn= bufferListNode.remove(0);
			}
			for( int x=0; x<vn.size(); x++ )
				vn.get(x).start();
		}
		}
		
		catch( SecurityException e){}
		catch( InterruptedException e){}
	}

	
	
	public void pullVectorNodesToActive( Vector<Node> vn ){
		synchronized(bufferListNode){
			bufferListNode.add( vn );
			bufferListNode.notify();
		}
	}
		
}