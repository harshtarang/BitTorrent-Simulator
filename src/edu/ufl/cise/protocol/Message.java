package edu.ufl.cise.protocol;

public abstract class Message {

	public static final String CHOKE          = "choke";
	public static final String UNCHOKE        = "unchoke";
	public static final String INTERESTED     = "interested";
	public static final String NOT_INTERESTED = "not interested";
	public static final String HAVE           = "have";
	public static final String BITFIELD       = "bitfield";
	public static final String REQUEST        = "request";
	public static final String PIECE          = "piece";
	
	enum MessageType {CHOKE, UNCHOKE, INTERESTED, NOT_INTERESTED, HAVE,
		BITFIELD, REQUEST, PIECE};
	
		
}
