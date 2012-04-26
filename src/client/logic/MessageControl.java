package client.logic;


class MessageControl implements MessageSensorNetworks{

	private int idSender;
	private Position posSender=null;
	private Position posReceiver=null;
	
	MessageControl( int id, Position pos1, Position pos2 ){
		idSender = id;
		posSender=pos1;
		posReceiver=pos2;
	}
	
	MessageControl( MessageClaim msg, Position p ){
		idSender = msg.getIdSender();
		posSender = msg.getPosSender();
		posReceiver = p;
	}
	
	// function to return identifier number of sender node
	public int getIdSender(){ return idSender; }
	
	// function to return position (x,y) of sender node
	public Position getPosSender(){ return posSender; }
	
	// function to return position (x,y) of receiver node
	public Position getPosReceiver(){ return posReceiver; }
}
