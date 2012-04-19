


import java.util.*;

import javax.xml.soap.Detail;

//import exception.*;

// classe che riceve i vettori (vc) dei nodi di ogni simulazione alla loro terminazione,
// li salva in un bufferData, e per ogni simulazione ne elabora i dati leggendoli nei nodi 

public class ElaboratorData extends Thread {
	Connector connect=null;

	Vector<Data> bufferData=new Vector<Data>(); // dati ricevuti da elaborare
	Vector<Stats> stats= new Vector<Stats>();   // statistiche pronte per essere inviate
	Integer detectionGlobal=0;
	
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
					if( detection ==1 ) detectionGlobal=new Integer( detection );
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
		Integer sentMessagesTot=0,      sentMessagesMin=Integer.MAX_VALUE,      sentMessagesMax=0;      Float sentMessagesAvg=0F,		sentMessagesSD=0F;
		Integer receivedMessagesTot=0,  receivedMessagesMin=Integer.MAX_VALUE,  receivedMessagesMax=0;  Float receivedMessagesAvg=0F,	receivedMessagesSD=0F;
		Integer	signatureVerifiedTot=0, signatureVerifiedMin=Integer.MAX_VALUE, signatureVerifiedMax=0; Float signatureVerifiedAvg=0F,	signatureVerifiedSD=0F; 
		Integer energyUsedTot=0,        energyUsedMin=Integer.MAX_VALUE,        energyUsedMax=0;        Float energyUsedAvg=0F,			energyUsedSD=0F;
		Integer memoryMsgTot=0,         memoryMsgMin=Integer.MAX_VALUE,         memoryMsgMax=0;         Float memoryMsgAvg=0F,			memoryMsgSD=0F;
		

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
		
		// calcolo le medie dei valori, arrotondando il valore a due cifre dopo la virgola
//		sentMessagesAvg= 	  (float)( Math.round( ( (float)sentMessagesTot/(float)vc.size()      ) *100.0 ) /100f  );
//		receivedMessagesAvg=  (float)( Math.round( ( (float)receivedMessagesTot/(float)vc.size()  ) *100.0 ) /100f  );
//		signatureVerifiedAvg= (float)( Math.round( ( (float)signatureVerifiedTot/(float)vc.size() ) *100.0 ) /100f  );
//		energyUsedAvg= 		  (float)( Math.round( ( (float)energyUsedTot/(float)vc.size()        ) *100.0 ) /100f  );
//		memoryMsgAvg=  		  (float)( Math.round( ( (float)memoryMsgTot/(float)vc.size()         ) *100.0 ) /100f  );
//		

		sentMessagesAvg= 	  roundValues( (float)sentMessagesTot/(float)vc.size() );
		receivedMessagesAvg=  roundValues ( (float)receivedMessagesTot/(float)vc.size() );
		signatureVerifiedAvg= roundValues( (float)signatureVerifiedTot/(float)vc.size() );
		energyUsedAvg= 		  roundValues( (float)energyUsedTot/(float)vc.size() );
		memoryMsgAvg=  		  roundValues( (float)memoryMsgTot/(float)vc.size() );
		 
		
		// calcolare le medie ponderate!!!!!!!!!!!!!!!!!!!!!		
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

		
		
		Object[] sentMessages= { sentMessagesMin, sentMessagesMax, sentMessagesAvg, sentMessagesSD };
		Object[] receivedMessages= { receivedMessagesMin, receivedMessagesMax, receivedMessagesAvg, receivedMessagesSD };
		Object[] signatureVerified= { signatureVerifiedMin, signatureVerifiedMax, signatureVerifiedAvg, signatureVerifiedSD };
		Object[] energyUsed= { energyUsedMin, energyUsedMax, energyUsedAvg, energyUsedSD };
		Object[] memoryMsg= { memoryMsgMin, memoryMsgMax, memoryMsgAvg, memoryMsgSD };
		
		
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
	
	
	private float roundValues( float x ){
		return (float)( Math.round( x  *100.0 ) /100f  );
	}
	
}
