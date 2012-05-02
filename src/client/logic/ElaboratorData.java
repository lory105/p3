// class to elaborate the informations of simulations
package client.logic;

import java.util.*;


class ElaboratorData extends Thread {
	static ElaboratorData instance = null;
	Connector connect=null;

	Vector<Data> bufferData=new Vector<Data>();	// received data to be processed
	Vector<Stat> stats= new Vector<Stat>();   	// statistics ready to be sent
	Integer detection=0;
	int simulationNumber=0;
	
	private ElaboratorData(Connector c){ connect=c; }
	
	public static ElaboratorData getInstance( Connector c){
		if( instance==null)
			instance=new ElaboratorData(c);
		return instance;
	} 
	
	public void run(){
		try{
			Data data;
			Vector<Node> vectorNodeToAnalize=null;
			
			while(true){
				if( isInterrupted() ) throw new SecurityException();
				synchronized (bufferData){
					
					while( bufferData.isEmpty() ){ bufferData.wait(); }
					data= bufferData.remove(0);
					vectorNodeToAnalize = data.getListNode();
					detection = data.getDetection();
				}
			
				if( detection == -1 ){ // flag of end simulation, statistics can be sent to server
					connect.pushStats( stats );
					simulationNumber=0;
					throw new SecurityException();
				}
				if( isInterrupted() ) throw new SecurityException();
				else readDate( vectorNodeToAnalize );
				
			}
		}
		
		catch( InterruptedException e){ bufferData.clear(); stats.clear(); }
		catch( SecurityException e ){ bufferData.clear(); stats.clear(); }
		
	}
	
	// read from a list of nodes of a single simulation the values ​​contained in the nodes
	private void readDate( Vector<Node> vc){
		Integer sentMessagesTot=0,      sentMessagesMin=Integer.MAX_VALUE,      sentMessagesMax=0;      Float sentMessagesAvg=0F,		sentMessagesSD=0F;
		Integer receivedMessagesTot=0,  receivedMessagesMin=Integer.MAX_VALUE,  receivedMessagesMax=0;  Float receivedMessagesAvg=0F,	receivedMessagesSD=0F;
		Integer	signatureVerifiedTot=0, signatureVerifiedMin=Integer.MAX_VALUE, signatureVerifiedMax=0; Float signatureVerifiedAvg=0F,	signatureVerifiedSD=0F; 
		Integer energyUsedTot=0,        energyUsedMin=Integer.MAX_VALUE,        energyUsedMax=0;        Float energyUsedAvg=0F,			energyUsedSD=0F;
		Integer memoryMsgTot=0,         memoryMsgMin=Integer.MAX_VALUE,         memoryMsgMax=0;         Float memoryMsgAvg=0F,			memoryMsgSD=0F;
		
		// calculating max and min values
		for( int x=0; x< vc.size(); x++){
			int[]datas = (vc.get(x).getDatas() );
			
			sentMessagesTot+=datas[0];
			if( datas[0] < sentMessagesMin ) sentMessagesMin=datas[0];
			if( datas[0] > sentMessagesMax ) sentMessagesMax=datas[0];
				
			receivedMessagesTot+=datas[1];
			if( datas[1] < receivedMessagesMin ) receivedMessagesMin=datas[1];
			if( datas[1] > receivedMessagesMax ) receivedMessagesMax=datas[1];
			
			signatureVerifiedTot+=datas[2];
			if( datas[2] < signatureVerifiedMin ) signatureVerifiedMin=datas[2];
			if( datas[2] > signatureVerifiedMax ) signatureVerifiedMax=datas[2];
			
			energyUsedTot+=datas[3];
			if( datas[3] < energyUsedMin ) energyUsedMin=datas[3];
			if( datas[3] > energyUsedMax ) energyUsedMax=datas[3];
			
			memoryMsgTot+=datas[4];
			if( datas[4] < memoryMsgMin ) memoryMsgMin=datas[4];
			if( datas[4] > memoryMsgMax ) memoryMsgMax=datas[4];
			
		}	

		// calculating the averages
		sentMessagesAvg= 	  roundValues( (float)sentMessagesTot/(float)vc.size() );
		receivedMessagesAvg=  roundValues ( (float)receivedMessagesTot/(float)vc.size() );
		signatureVerifiedAvg= roundValues( (float)signatureVerifiedTot/(float)vc.size() );
		energyUsedAvg= 		  roundValues( (float)energyUsedTot/(float)vc.size() );
		memoryMsgAvg=  		  roundValues( (float)memoryMsgTot/(float)vc.size() );
		 
		
		// calculating the standard deviation		
		for( int x=0; x< vc.size(); x++){
			int[]datas = (vc.get(x).getDatas() );
			
			sentMessagesSD+= (float)( Math.pow( ( (float)datas[0]-sentMessagesAvg), 2 ) );
			receivedMessagesSD+= (float)( Math.pow( ( (float)datas[1]-receivedMessagesAvg), 2 ) );
			signatureVerifiedSD+= (float)( Math.pow( ( (float)datas[2]-signatureVerifiedAvg), 2 ) );
			energyUsedSD+= (float)( Math.pow( ( (float)datas[3]-energyUsedAvg), 2 ) );
			memoryMsgSD+= (float)( Math.pow( ( (float)datas[4]-memoryMsgAvg), 2 ) );
		
		}
		

		sentMessagesSD= roundValues( (float)Math.sqrt( sentMessagesSD/(float)vc.size() ) );
		receivedMessagesSD= roundValues( (float)Math.sqrt( receivedMessagesSD/(float)vc.size() ) );
		signatureVerifiedSD= roundValues( (float)Math.sqrt( signatureVerifiedSD/(float)vc.size() ) );
		energyUsedSD= roundValues( (float)Math.sqrt( energyUsedSD/(float)vc.size() ) );
		memoryMsgSD= roundValues( (float)Math.sqrt( memoryMsgSD/(float)vc.size() ) );

		
		// storage statistics
		Object[] sentMessages= { sentMessagesMin, sentMessagesMax, sentMessagesAvg, sentMessagesSD };
		Object[] receivedMessages= { receivedMessagesMin, receivedMessagesMax, receivedMessagesAvg, receivedMessagesSD };
		Object[] signatureVerified= { signatureVerifiedMin, signatureVerifiedMax, signatureVerifiedAvg, signatureVerifiedSD };
		Object[] energyUsed= { energyUsedMin, energyUsedMax, energyUsedAvg, energyUsedSD };
		Object[] memoryMsg= { memoryMsgMin, memoryMsgMax, memoryMsgAvg, memoryMsgSD };
		
		
		Stat stat = new Stat( sentMessages, receivedMessages, signatureVerified, energyUsed, memoryMsg, detection );
		stats.add( stat );
		
		simulationNumber++;
		connect.print( "\n" + simulationNumber + ") " + stat.printValues(), 1);
	}
	
	// received data to be processed
	public void push( Data d ){
		synchronized (bufferData) {		
			bufferData.add( d );
			bufferData.notify();
		}
	}
	
	// function to round values to the second digit after the decimal point
	private float roundValues( float x ){
		return (float)( Math.round( x  *100.0 ) /100f  );
	}
	
}
