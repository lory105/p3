

import java.util.*;
import java.io.Serializable;

public class Stats implements Serializable {
	// memorizzo i valori delle simulazioni
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
	
}
