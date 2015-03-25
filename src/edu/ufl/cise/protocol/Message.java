package edu.ufl.cise.protocol;

public abstract class Message {
	
	public enum MessageType {
		CHOKE(0),
		UNCHOKE(1),
		INTERESTED(2), 
		NOT_INTERESTED(3),
		HAVE(4),
		BITFIELD(5),
		REQUEST(6),
		PIECE(7);
		
		private int val;
		
		private MessageType(int val){
			this.val = val;
		}
		
		public int getValue(){
			return this.val;
		}
	}
	
	
	
		
}
