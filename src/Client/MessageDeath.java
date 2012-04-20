package Client;


public class MessageDeath implements Message{
	Node died=null;
	
	MessageDeath( Node n){
		died=n;
	}
	
	public Node getDied(){ return died; }
}
