

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Stats implements Serializable {
	
	private Vector<int[]> info = new Vector<int[]>();
	private int detection;
	
	Stats( int[] sent, int[] received, int[] signature, int[] energy, int[] memory, int d ){
		info.add(sent);
		info.add(received);
		info.add(signature);
		info.add(energy);
		info.add(memory);
		detection=d;
	}
	
	
	public void printValues( FileWriter out ){
		
		// stampo tutti i valori [MIN MAX AVG SD] in questo ordine, per ogni variabile richiesta [ SENT MSM, RECEIVED MSG, SIGNATIRE .. ]
		try{
			for(int x=0; x<info.size(); x++){
				for( int y=0; y< info.get(x).length; y++)
					out.append(  Integer.toString( (info.get(x))[y] ) + " " );
				
				// da tolgiere !!!!!!!!!!!!!!
				out.append("	");
			}
			out.append( Integer.toString(detection));

		}
		catch(FileNotFoundException e){ System.out.println("file not found"); }
		catch(IOException ioe){	System.out.println("I/O errore"); }
		
	}
	

}
