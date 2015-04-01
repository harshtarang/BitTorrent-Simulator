package edu.ufl.cise.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.ufl.cise.config.MetaInfo;

public class FileHandlingUtils {

	public void writePiece(int pieceId, byte[] piece) {
		String fileName = MetaInfo.getBasePath() + "piece_" + pieceId;
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(fileName));
			out.write(piece);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(out != null){
					out.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}

	public byte[] getPiece(int pieceId) {
		int pieceLength;
		byte[] piece = null;
		InputStream in = null;
		if (pieceId == MetaInfo.getnPieces()) {
			pieceLength = MetaInfo.getLastPieceSize();
		} else {
			pieceLength = MetaInfo.getPieceSize();
		}
		piece = new byte[pieceLength];

		String fileName = MetaInfo.getBasePath() + "piece_" + pieceId;
		try {

			File file = new File(fileName);
			in = new FileInputStream(file);
			in.read(piece);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return piece;
	}

	public void combinePieces(){
		
	}
	
	public void finish(){
		
	}
	
	public void deletePieces(){
		
	}
	
}
