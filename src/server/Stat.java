// Class represents the statistics for server
package server;

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
		
		// for each variable [ SENT MSG, RECEIVED MSG, SIGNATURE VERIFIED .. ] prints all values [MIN, MAX, AVG, SD]
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
