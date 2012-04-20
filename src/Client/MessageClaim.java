package Client;


public class MessageClaim implements MessageDetection {

	int idSender;
	Position posSender=null;
	
	MessageClaim( int id, Position pos){
		idSender = id;
		posSender=pos;
	}

	
	public int getIdSender(){ return idSender; }
	public Position getPosSender(){ return posSender; }
	
}
