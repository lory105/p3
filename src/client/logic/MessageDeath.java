// message sent from a node when it ends its energy to all its neighbors 
package client.logic;


class MessageDeath implements Message{
	private int idSender;
	
	MessageDeath( int idSender){
		this.idSender=idSender;
	}
	
	// function to return identification number of sender node
	public int getIdSender(){ return idSender; }
}
