package Client;


public class Position {
   private float x;
   private float y;
   
   Position( float x, float y){ 
	   this.x=x; this.y=y;
   }
   
   public boolean equals(Position p){
	   if( x==p.x && y==p.y ) return true;
	   return false;
	   
   }
   
   public float getX(){ return x; }
   public float getY(){ return y; }
   
}
