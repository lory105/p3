// Class used to read configuration file
package client.logic;

import java.net.*;
import java.io.*;

import client.exception.ExcReadFile;


class ReadURL {
	static ReadURL instance=null;
	Connector connect=null;

	private ReadURL( Connector c){ connect=c; }
	
	// function to return the instance of Singleton class
	public static ReadURL getInstance( Connector c){
		if( instance == null )
			instance=new ReadURL(c);
		return instance;
	}

	// function to read file
    public Object[] read( String url ){
    
    
       String[] variable = { "PROTO", "NSIM", "n", "r", "p", "g", "E", "esend", "ereceive", "esignature" };
       Object[] values = new Object[10];
       int read=0;

       try{
	      URL fileRead = new URL( url );

   	      // save file in read buffer
	      BufferedReader in = new BufferedReader( new InputStreamReader( fileRead.openStream()) );

	      String inputLine;
	      boolean find=false;

	      connect.print("values read:", 0);
	      while( read< 10){
	         find=false;
	         inputLine = in.readLine();
	    
	         // raises the exception because the file has finished before it has read all the values
	         if ( inputLine == null ){ throw new ExcReadFile(); }

	         if( ! inputLine.startsWith("%")  && ! inputLine.isEmpty() ){
	       
	            inputLine= inputLine.replace( " ", "");
	            inputLine= inputLine.replace( "\t", ""); // deletes tab
	            inputLine= inputLine.replace( "E_", "e");

	            for( int x=0; x<10 && !find ; x++){
		           if( inputLine.startsWith( variable[x] ) ){
		              find=true;
		              int f = ( inputLine.indexOf("=") +1 );
		              inputLine = inputLine.substring( f );
		     
		              if( x == 0 ){ values[x]= new String( inputLine ); }

		              else{
			             if( x == 3 || x == 4 ){ values[x]= new Float( Float.parseFloat( inputLine ) ); }
			             else{ values[x]= new Integer( Integer.parseInt( inputLine ) ); }
		              }
		              
		              connect.print( variable[x] + "=" + values[x], 0 );
		              connect.print( values[x].toString() + " ", 1 );
		              read++;
		           }
	            }
	            // raises the exception because the file has a comment that does not begin with%
	            if( !find) { throw new ExcReadFile(); }
	         }
	      }
	      
	 	 in.close();
	 	 
       } // end try
       catch ( ExcReadFile e){ connect.print( "Configuration file not correct.\nEND", 0); return null; }
       catch ( MalformedURLException e ){ connect.print( e.getMessage() + "\nUrl of configuration file isn't correct.\nEND", 0); return null; }
       catch ( IOException e ){ connect.print( e.getMessage() + "\nMake sure you are connected to the network or check the URL you entered.", 0); return null; }
      return values;
   }
   
   
}
