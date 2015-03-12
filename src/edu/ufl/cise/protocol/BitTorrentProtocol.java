package edu.ufl.cise.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import edu.ufl.cise.config.MetaInfo;

public class BitTorrentProtocol 
{

	public static String receiveStream(InputStream in) throws IOException
	{
		byte[] data= new byte[4];
		String firstFourBytes=null;
		Message response=null;
		try 
		{
			in.read(data, 0, data.length);
			firstFourBytes=new String(data,"UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			
			e.printStackTrace();
		}
		if(isNumeric(firstFourBytes))
		{
			byte[] mType = new byte[1];
			in.read(mType, 4, 1);
			int messageType=Integer.parseInt(new String(mType,"UTF-8"));
			
			if(messageType == 0)
			{
				response = new Choke();
			}
			
			else if(messageType == 1)
			{
				response = new Unchoke();
			}
			else if(messageType == 2)
			{
				response = new Interested();
			}
			else if(messageType == 3)
			{
				response = new NotInterested();
			}
			else if(messageType == 4)
			{
				byte[] byteArray = convertToByteArray(in);
				response = new Have(byteArray);
			}
			else if(messageType == 5)
			{
				response = new BitField(new boolean[MetaInfo.getFileSize()/MetaInfo.getPieceSize()]);
			}
			else if(messageType == 6)
			{
				response = new Request();
			}
			else if(messageType == 7)
			{
				response = new Piece();
			}
			
			//response= new ActualMessage();
		}
		else
		{
			byte[] byteArray = convertToByteArray(in);
			response= new HandshakeMessage(byteArray);
		}
	
		return null;
	}

	private static byte[] convertToByteArray(InputStream in) 
	{
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try 
		{
			while ((nRead = in.read(data, 0, data.length)) != -1) 
			{
			  buffer.write(data, 0, nRead);
			}

		buffer.flush();
		}
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	private static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	
}
