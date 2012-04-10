


import java.net.*;
import java.io.*;
//import java.util.*;



public class ReadURL {
	Connector connect=null;

	public ReadURL( Connector c){ connect=c; } // da togliere public!!!!!!!!!!!!!

    public Object[] read( String url ){
    
    
       String[] variable = { "PROTO", "NSIM", "n", "r", "p", "g", "E", "esend", "ereceive", "esignature" };
       Object[] values = new Object[10];
       int read=0;
       //boolean endRead= false;

       try{
	      URL fileRead = new URL( url );

   	      // salvo il file nel buffer di lettura
	      BufferedReader in = new BufferedReader( new InputStreamReader( fileRead.openStream()) );

	      String inputLine;
	      boolean find=false;

	      while( read< 10){
	         find=false;
	         inputLine = in.readLine();
	    
	         if ( inputLine == null ){ throw new ErrReadFile(0); } // solleva eccezione xk il file è terminato prima k abbia letto tutti i valori!! null è solo se finisce il file e non se ho riga vuota!!

	         if( ! inputLine.startsWith("%")  && ! inputLine.isEmpty() ){
	       
	            inputLine= inputLine.replace( " ", "");
	            inputLine= inputLine.replace( "\t", ""); // tolgo i tab
	            inputLine= inputLine.replace( "E_", "e");
  	            //System.out.println( inputLine );

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
		              
		              connect.print( variable[x] + "=" + values[x] );
		              read++;
		           }
	            }

	            //if( !find) { return; } // solleva eccezione xk il file ha un commento che non inizia con %
	         }
	      }
	      
	 	 in.close();
	 	 
       } // fine try
       catch ( ErrReadFile errR){ connect.print("File in lettura non corretto"); return null; }
       catch ( MalformedURLException UrlErr ){ connect.print("Struttura URL non corretta."); return null; }
       catch ( IOException IOErr ){ connect.print("Errore Input Output:\nassicurarsi di essere connessi alla rete o controllare l'URL inserita."); return null; }
	 
      return values;
   }
   
   
}
