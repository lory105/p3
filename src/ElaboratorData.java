


import java.util.*;

import javax.xml.soap.Detail;

//import exception.*;

// classe che riceve i vettori (vc) dei nodi di ogni simulazione alla loro terminazione,
// li salva in un bufferData, e per ogni simulazione ne elabora i dati leggendoli nei nodi 

public class ElaboratorData extends Thread {
	Connector connect=null;

	Vector<Data> bufferData=new Vector<Data>(); // dati ricevuti da elaborare
	Vector<Stats> stats= new Vector<Stats>();   // statistiche pronte per essere inviate
	int detectionGlobal=0;
	
	public ElaboratorData(Connector c){ connect=c; }
	
	public void run(){
		System.out.println("elaboratorData parte run");
		try{
			Data data;
			Vector<Node> vn=null;
			int detection;
			
			while(true){
				detection=0;
				if( isInterrupted() ) throw new SecurityException();
				synchronized (bufferData){
					
					while( bufferData.isEmpty() ){ bufferData.wait(); }
					//if( finish ) throw new ExcFinish();
					data= bufferData.remove(0);
					vn = data.getListNode();
					detection = data.getDetection();
					if( detection ==1 ) detectionGlobal=detection;
				}
			
				if( detection == -1 ){ // flag di fine simulazione, il client può inviare le statistiche della simulazione
					System.out.println("elaborator passa stats al client");
					

					connect.pushStats( stats );
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
	
	// leggo dalla lista dei nodi di una simulazione i valori contenuti nei nodi
	private void readDate( Vector<Node> vc){
		int sentMessagesTot=0,      sentMessagesMin=Integer.MAX_VALUE,      sentMessagesMax=0,      sentMessagesAvg=0,      sentMessagesSD=0;
		int receivedMessagesTot=0,  receivedMessagesMin=Integer.MAX_VALUE,  receivedMessagesMax=0,  receivedMessagesAvg=0,  receivedMessagesSD=0;
		int	signatureVerifiedTot=0, signatureVerifiedMin=Integer.MAX_VALUE, signatureVerifiedMax=0, signatureVerifiedAvg=0, signatureVerifiedSD=0; 
		int energyUsedTot=0,        energyUsedMin=Integer.MAX_VALUE,        energyUsedMax=0,        energyUsedAvg=0,        energyUsedSD=0;
		int memoryMsgTot=0,         memoryMsgMin=Integer.MAX_VALUE,         memoryMsgMax=0,         memoryMsgAvg=0,         memoryMsgSD=0;
		

		for( int x=0; x< vc.size(); x++){
			int[]datas = (vc.get(x).getDatas() );
			
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
		stats.add( new Stats( sentMessages, receivedMessages, signatureVerified, energyUsed, memoryMsg, detectionGlobal ) );
		
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
