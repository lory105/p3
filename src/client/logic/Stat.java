package client.logic;


import java.util.*;
import java.io.Serializable;

public class Stat implements Serializable {
	
	private Vector<Object[]> info = new Vector<Object[]>();
	private Integer detection;
	
	Stat( Object[] sent, Object[] received, Object[] signature, Object[] energy, Object[] memory, Integer d ){
		info.add(sent);
		info.add(received);
		info.add(signature);
		info.add(energy);
		info.add(memory);
		detection=d;
	}
	
	
	public String printValues(){
		String output=new String("");
		
		// stampo tutti i valori [MIN MAX AVG SD] in questo ordine, per ogni variabile richiesta [ SENT MSM, RECEIVED MSG, SIGNATIRE .. ]

			for(int x=0; x<info.size(); x++){
				for( int y=0; y< info.get(x).length; y++)
					output=( output + ( (info.get(x))[y] ).toString() + " " ) ;
				
				// da tolgiere !!!!!!!!!!!!!!
				output= (output + "	");
			}
			output= (output + detection.toString() );
			
			return output;
	}
	

}
