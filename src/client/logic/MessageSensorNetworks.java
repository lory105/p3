// Common interface for message used to implements ''Wireless Sensor Networks''
package client.logic;


interface MessageSensorNetworks extends Message {

	
	// function to return identification number of sender node
	public int getIdSender();
	
	// function to return position (x,y) of sender node
	public Position getPosSender();
}