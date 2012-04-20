package client.logic;


public class MessageControl implements MessageDetection{

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
	
	
	public int getIdSender(){ return idSender; }
	public Position getPosSender(){ return posSender; }
	public Position getPosReceiver(){ return posReceiver; }
}
