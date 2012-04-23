// message sent from a node when it ends its energy to all its neighbors 
package client.logic;


public class MessageDeath implements Message{
	int idSender;
	
	MessageDeath( int idSender){
		this.idSender=idSender;
	}
	
	public int getIdSender(){ return idSender; }
}
