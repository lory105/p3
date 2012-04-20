package client.logic;


import java.util.*;

import client.exception.ExcEndEnergy;
import client.exception.ExcFindClone;
import client.exception.ExcNoNeighbors;

abstract public class Node extends Thread {
	static Hypervisor hyper=null;
	static int detection = 0; 	   // tiene traccia se almeno un nodo ha effettuato la detection di un clone 0 (false)
	static Object lockEndSim = new Object();
	static boolean endSimulation = false;
	
	// da mettere tutti final
	static float probAcceptLocation;       // p
	static int locationDestination;        // g
	static int energyTot;
	static int energyToSend;
	static int energyToReceive;
	static int energyToSignature;
	
	//static Boolean endSimulation=new Boolean(false);  // i nodi prima di fare un'azione si sincronizzano qui per vedere se è stato trovato un clone
	
	int id;
	Position pos=null;
	int energy=0;
	
    Vector<Node> neighbors= new Vector<Node>();
    Vector<MessageControl> memoryMsg= new Vector<MessageControl>(); // vettore con tutti i messaggi DI CONTROLLO che sono passati per questo nodo
	Vector<Message> bufferMessage = new Vector<Message>();
    
	int sentMessages=0;
    int receivedMessages=0;
	int signatureVerified=0;

	
	protected Node( int i, Position p, int en ){
		super( "Node" + i);
		id=i; pos=p; energy= en;
	}
	
	public static void setParamiters( Hypervisor h, float p, int ld, int et, int ets, int etr, int etsg){
		hyper=h;
		probAcceptLocation=p;
		locationDestination = ld;
		
		energyTot= et;
		energyToSend=ets;
		energyToReceive=etr;
		energyToSignature=etsg;	
	}
		
	
	public void run(){
		try{
			//if( isInterrupted() ) throw new SecurityException();
			checkEndSimulazion();
			//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
			checkEndNeighbors();
			//synchronized( neighbors ){ if( neighbors.isEmpty() ) throw new ExcNoNeighbors(); }
			sendLocationClaim(); 
			Message msg=null;
			
//			hyper.connect.print( this.getName() + "ha terminato. ho inviato msgClaim" + sentMessages + "x" + neighbors.size() + "vicini --" );
 

			while(true){
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				checkEndNeighbors();
				//synchronized( neighbors ){ if( neighbors.isEmpty() ) throw new ExcNoNeighbors(); }
				synchronized( bufferMessage ){
					// se non ho messaggi nel buffer da gestire faccio wait() sul bufferMessage
					while( bufferMessage.isEmpty() ){
						checkEndSimulazion();
						//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
						hyper.nodeNotActive();
						bufferMessage.wait();	
						//if( isInterrupted() ) throw new SecurityException();
						checkEndSimulazion();
						//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
						// se un nodo si sveglia => gli è stato inviato un messaggio
						hyper.nodeActive(); 
					}
					
					msg = bufferMessage.remove(0);
				}
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				if( msg instanceof MessageControl){
					//hyper.connect.print("receiveMessControl");
					receiveMessageControl( (MessageControl)msg); }
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				if( msg instanceof MessageClaim  ){ 
					//hyper.connect.print("receiveMessClai");
					receiveMessageClaim( ((MessageClaim)msg) ); }
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				if( msg instanceof MessageDeath  ){ 
					//hyper.connect.print("receiveMessDeath"); 
					receiveMessageDeath( (MessageDeath)msg); }				
			}

		}
		// se il nodo è interrotto in uno stato attivo
		catch( SecurityException e){
   	 		hyper.connect.print( getName() + "securExc terminato " + this.getName(), 0 );

   	 		// ottimizzare la memoria: if( closeAll ) faccio delle clear sui vettori
   	 		hyper.nodeNotActive();
   	 	}

		// se il nodo è interrotto in uno stato di wait
		catch( InterruptedException e){
   	 		hyper.connect.print( getName() + "interrExc terminato " + this.getName(), 0 );
   	 		// ottimizzare la memoria: if( closeAll ) faccio delle clear sui vettori
   	 		//hyper.nodeNotActive();
   	 	}

		catch( ExcNoNeighbors e){ hyper.connect.print("NO VICINI", 0); hyper.nodeNotActive(); }

		catch( ExcFindClone e ){ 
			hyper.connect.print("CLONE EXIT FIND", 0);  hyper.findClone(); hyper.nodeNotActive();}

		catch( ExcEndEnergy e){
			// mando un mess a tutti i vicini che sono morto e avviso l'hypervisior
			hyper.connect.print( getName()+ " ExcEndEnergy, inviati" + sentMessages + "x" + neighbors.size() + "vicini --" , 0);
			sendMessageDeath();
			hyper.nodeNotActive();
		}
		
	}
	

	
	
	public void checkEndSimulazion() throws SecurityException{
		if( endSimulation ) throw new SecurityException();
	}
	
	public void checkEndNeighbors() throws ExcNoNeighbors{
		synchronized( neighbors ){ if( neighbors.isEmpty() ) throw new ExcNoNeighbors(); }
	} 
	
	// function that subtract the current energy of a node and checks if this energy is terminated
	protected void checkEnergy( int energyToSubtract) throws ExcEndEnergy{
		energy-=energyToReceive;
		if(energy <=0 ) throw new ExcEndEnergy();
	}
	
	// creo il Location Claim e lo invio in broadcast a tutti i vicini ( LSM, RED )
	public void sendLocationClaim() throws ExcEndEnergy, SecurityException {
		//hyper.connect.print("sendLocationClaim");
		// spendo energia per firmare il messaggio
		checkEnergy(energyToSignature);
		
		MessageClaim mc = new MessageClaim( id, pos);
		
		// invio il MessagClaim a tutti i vicini
		synchronized( neighbors ){
			for( int x=0; x< neighbors.size(); x++){
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				checkEnergy(energyToSend);
				sentMessages++;
				neighbors.get(x).pushMessage(mc);
			}
		}
	}
	

	public void sendMessageControl( Node receiver, MessageControl msg){
		receiver.pushMessage(msg);
	}

	public void sendMessageDeath(){
		MessageDeath msg = new MessageDeath( id );
		synchronized( neighbors ){
			for( int x=0; x< neighbors.size(); x++ ){
				neighbors.get(x).pushMessage( msg );
			}
		}
	}
	
	abstract public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone, SecurityException;
	abstract public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException;
	
	public void receiveMessageDeath( MessageDeath msg ){
		int idNodeDied= msg.getIdSender();
		synchronized( neighbors ){
			for(int x=0; x<neighbors.size(); x++){
				if( neighbors.get(x).getId() == idNodeDied){
					neighbors.remove(x);
					return;
				}
			}
		}
	}
	
	
	public boolean findClone( MessageControl msg ) throws ExcEndEnergy, SecurityException{
		//if( isInterrupted() ) throw new SecurityException();
		checkEndSimulazion();
		//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		
		checkEnergy(energyToSignature);
		
		signatureVerified++;
		
		int idSender = msg.getIdSender();
		Position posSender = msg.getPosSender();
		
		// detection about me
		if(idSender == id && ! posSender.equals(pos) ) { hyper.connect.print("TROVATO!!!!!!!!!!!\n", 0); return true;} 
		
		// detection about of my messages stored in buffer memoryMsg
		synchronized (memoryMsg) {
			for(int i=0; i < memoryMsg.size(); i++ ){
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				if( idSender == memoryMsg.get(i).getIdSender() &&  posSender != memoryMsg.get(i).getPosSender() ){
					hyper.connect.print("TROVATO!!!!!!!!!!!\n", 0); return true;
				}
			}
		}
		return false;

	}
	

	
	// inserisco il messaggio che sto inviando nel buffer del nodo ricevente 
	public void pushMessage( Message msg ){
	
		synchronized( bufferMessage ){
		// se ricevo un messaggio di un vicino che è morto, lo inserisco all'inizio del bufferMessage
			if( msg instanceof MessageDeath ) bufferMessage.add(0, msg);
			else bufferMessage.add(msg);
			
			bufferMessage.notify();
		}
	}
	
	
	
	
	// ritorna il nodo vicino più "vicino" alla posizione del messaggio ricevuto, controllando ank la posizione propria
	public Node nearestNeighbor(Position p){
		// inizialmente assegno la posizione del nodo stesso come la posizione più vicina a p (= destinazione del messaggio ) 
		Node nearestNode = this;
		float minDistance = Hypervisor.pythagora( pos, p);
		float distance=0;
		
		synchronized( neighbors ){
			for( int i=0; i < neighbors.size(); i++ ){
				distance = Hypervisor.pythagora( neighbors.get(i).getPosition(), p);
				if(  distance < minDistance ){
					nearestNode = neighbors.get(i);
					minDistance = distance;
				}
			}
		}
		return nearestNode;
	}
	
	
	static public void setNewSim( int d, boolean e){ detection = d; endSimulation=e; };
	
	public int getIdNode(){ return id; }
	public final Position getPosition(){ return pos; }
	public Vector<Node> getNeighbors(){ synchronized(neighbors){ return neighbors;}  }
	static public int getDetection(){ return detection; }
	
	public int[] getDatas(){
		int en;
		if( energy <= 0 )
			en=energyTot;
		else
			en= (energyTot-energy);
			
		int[] datas= { sentMessages, receivedMessages, signatureVerified, en, memoryMsg.size() };

		return datas;
		
	}

}

