package client.logic;


public class MessageClaim implements MessageDetection {

	private int idSender;
	private Position posSender=null;
	
	MessageClaim( int id, Position pos){
		idSender = id;
		posSender=pos;
	}

	
	public int getIdSender(){ return idSender; }
	public Position getPosSender(){ return posSender; }
	
}
