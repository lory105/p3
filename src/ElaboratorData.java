


import java.util.*;

import javax.xml.soap.Detail;

//import exception.*;

// classe che riceve i vettori (vc) dei nodi di ogni simulazione alla loro terminazione,
// li salva in un bufferData, e per ogni simulazione ne elabora i dati leggendoli nei nodi 

public class ElaboratorData extends Thread {
	Connector connect=null;

	Vector<Data> bufferData=new Vector<Data>(); // dati ricevuti da elaborare
	Vector<Stats> stats= new Vector<Stats>();   // statistiche pronte per essere inviate
	
	public ElaboratorData(Connector c){ connect=c; }
	
	public void run(){
		System.out.println("elaborator parte run");
		try{
			Data data;
			Vector<Node> vn=null;
			int detection;

			while(true){
				if( isInterrupted() ) throw new SecurityException();
				synchronized (bufferData){
					
					while( bufferData.isEmpty() ){ bufferData.wait(); }
					//if( finish ) throw new ExcFinish();
					data= bufferData.remove(0);
					vn = data.getListNode();
					detection = data.getDetection();
				}
			
				if( detection == -1 ){ // flag di fine simulazione, il client può inviare le statistiche della simulazione
					System.out.println("elaborator passa stats al client");
					
					// evito di inviare i dati al client x ora xk non va client server
					//connect.pushStats( stats );
					throw new SecurityException();  // sollevo eccezione di interruzione così libero memoria
				}
				if( isInterrupted() ) throw new SecurityException();
				else readDate( vn );
				
			}
		}
		
		catch( InterruptedException e){ bufferData.clear(); stats.clear(); }
		// se il thread è in wait e viene interrotto posso librare tutta la menoria
		catch( SecurityException e ){ bufferData.clear(); stats.clear(); }
		
	}
	
	
	private void readDate( Vector<Node> vc){
		int sentMessagesTot=0,      sentMessagesMin=0,      sentMessagesMax=Integer.MAX_VALUE,      sentMessagesAvg=0,      sentMessagesSD=0;
		int receivedMessagesTot=0,  receivedMessagesMin=0,  receivedMessagesMax=Integer.MAX_VALUE,  receivedMessagesAvg=0,  receivedMessagesSD=0;
		int	signatureVerifiedTot=0, signatureVerifiedMin=0, signatureVerifiedMax=Integer.MAX_VALUE, signatureVerifiedAvg=0, signatureVerifiedSD=0; 
		int energyUsedTot=0,        energyUsedMin=0,        energyUsedMax=Integer.MAX_VALUE,        energyUsedAvg=0,        energyUsedSD=0;
		int memoryMsgTot=0,         memoryMsgMin=0,         memoryMsgMax=Integer.MAX_VALUE,         memoryMsgAvg=0,         memoryMsgSD=0;
		

		for( int j=0; j< vc.size(); j++){
			int[]datas = (vc.get(j).getDatas() );
			
			sentMessagesTot+=datas[0];
			if( datas[0] < sentMessagesMin ) sentMessagesMin=datas[0];
			if( datas[0] > sentMessagesMax ) sentMessagesMax=datas[0];
				
			receivedMessagesTot+=datas[0];
			if( datas[1] < receivedMessagesMin ) receivedMessagesMin=datas[1];
			if( datas[1] > receivedMessagesMax ) receivedMessagesMax=datas[1];
			
			signatureVerifiedTot+=datas[0];
			if( datas[2] < signatureVerifiedMin ) signatureVerifiedMin=datas[2];
			if( datas[2] > signatureVerifiedMax ) signatureVerifiedMax=datas[2];
			
			energyUsedTot+=datas[0];
			if( datas[3] < energyUsedMin ) energyUsedMin=datas[3];
			if( datas[3] > energyUsedMax ) energyUsedMax=datas[3];
			
			memoryMsgTot+=datas[0];
			if( datas[4] < memoryMsgMin ) memoryMsgMin=datas[4];
			if( datas[4] > memoryMsgMax ) memoryMsgMax=datas[4];
			
		}
		
		sentMessagesAvg= sentMessagesTot/vc.size();
		receivedMessagesAvg= receivedMessagesTot/vc.size();
		signatureVerifiedAvg= signatureVerifiedTot/vc.size();
		energyUsedAvg= energyUsedTot/vc.size();
		memoryMsgAvg= memoryMsgTot/vc.size();
		
		// calcolare le medie ponderate!!!!!!!!!!!!!!!!!!!!!
		
		int[] sentMessages= { sentMessagesMin, sentMessagesMax, sentMessagesAvg, sentMessagesSD };
		int[] receivedMessages= { receivedMessagesMin, receivedMessagesMax, receivedMessagesAvg, receivedMessagesSD };
		int[] signatureVerified= { signatureVerifiedMin, signatureVerifiedMax, signatureVerifiedAvg, signatureVerifiedSD };
		int[] energyUsed= { energyUsedMin, energyUsedMax, energyUsedAvg, energyUsedSD };
		int[] memoryMsg= { memoryMsgMin, memoryMsgMax, memoryMsgAvg, memoryMsgSD };
		
		System.out.println("Elaborator elabora dati");
		stats.add( new Stats(sentMessages, receivedMessages, signatureVerified, energyUsed, memoryMsg ) );
		
	}
	
	
	public void push( Data d ){
		synchronized (bufferData) {
			System.out.println("Elaborator sta x ricevere dati. Size bufferDataData elaborator=" + bufferData.size());
			bufferData.add( d );
			
			System.out.println("Elaborator ha ricevuto dati. Size bufferDataData elaborator=" + bufferData.size());
			if( d == null ) System.out.println("si");
			bufferData.notify();
		}
	}
	
	
}
