// Class represents the position (x,y) of nodes
package client.logic;


class Position {
   private float x;
   private float y;
   
   Position( float x, float y){ 
	   this.x=x; this.y=y;
   }
   
   // function to compare two position
   public boolean equals(Position p){
	   if( x==p.x && y==p.y ) return true;
	   return false;
	   
   }
   
   // function that returns the field x-axis
   public float getX(){ return x; }
   
   // function that returns the field y-axis
   public float getY(){ return y; }
   
}
