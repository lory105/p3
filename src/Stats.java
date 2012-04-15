

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Stats implements Serializable {
	// memorizzo i valori delle simulazioni cioè i valori riguardanti le simulazioni
	static private Object[] values=null;
	
	private Vector<int[]> info = new Vector<int[]>();
	
	Stats( int[] sent, int[] received, int[] signature, int[] energy, int[] memory ){
		info.add(sent);
		info.add(received);
		info.add(signature);
		info.add(energy);
		info.add(memory);
	}
	
	
	static public void setValues( Object[] v){
		values=v;
	}
	
	public static void getValue( FileWriter out2 ){

		try{
			
			out2.append("Start sim:\n \\ \n");


//			proto= (String) values[0];
//			nSim= (Integer) values[1];
//			numberNode =(Integer) values[2];
//			radius =(Float) values[3];
//			probAcceptLocation=(Float) values[4];
//			numLocationDestination= (Integer) values[5];
//			energyTot =(Integer) values[6];
//			energyToSend=(Integer) values[7];
//			energyToReceive=(Integer) values[8];
//			energyToSignature=(Integer) values[9];
			

//			out2.append( "PROTO = " + ( (String) values[0]).toString() + "\n");
//			System.out.println( "prova stampa " + values[0].toString() );
//			out.append( "NSIM = " + values[1].toString() + "\n");
//			out.append( "p = " + values[2].toString() + "\n");
//			out.append( "g = " + values[3].toString() + "\n");
//			out.append( "n = " + values[4].toString() + "\n");
		
		}
		catch(FileNotFoundException e){ System.out.println("file not found"); }
		catch(IOException ioe){	System.out.println("I/O errore"); }
		
	}
	
	public void getInfo( FileWriter out ){

		try{
			out.append( "PROTO = " + values[0].toString() + "\n");
			out.append( "NSIM = " + values[1].toString() + "\n");
			out.append( "p = " + values[2].toString() + "\n");
			out.append( "g = " + values[3].toString() + "\n");
			out.append( "n = " + values[4].toString() + "\n");
			
		}
		catch(FileNotFoundException e){ System.out.println("file not found"); }
		catch(IOException ioe){	System.out.println("I/O errore"); }
		
	}

}
