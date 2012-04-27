// Class represents the messages of location claim.
package client.logic;


class MessageClaim implements MessageSensorNetworks {

	private int idSender;
	private Position posSender=null;
	
	MessageClaim( int id, Position pos){
		idSender = id;
		posSender=pos;
	}

	// function to return identifier number of sender node
	public int getIdSender(){ return idSender; }
	
	// function to return position (x,y) of sender node
	public Position getPosSender(){ return posSender; }
	
}
