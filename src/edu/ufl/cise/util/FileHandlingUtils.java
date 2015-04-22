package edu.ufl.cise.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import edu.ufl.cise.config.MetaInfo;

public class FileHandlingUtils {

	public void writePiece(int pieceId, byte[] piece) {
		
		String fileName=null;
		
		 fileName = MetaInfo.getPeerBasePath() + "piece_" + pieceId;
		
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(fileName));
			out.write(piece);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public byte[] getPiece(int pieceId) {
		int pieceLength;
		byte[] piece = null;
		InputStream in = null;
		if (pieceId == MetaInfo.getnPieces()-1) {
			pieceLength = MetaInfo.getLastPieceSize();
		} else {
			pieceLength = MetaInfo.getPieceSize();
		}
		piece = new byte[pieceLength];

		String fileName = MetaInfo.getPeerBasePath() + "piece_" + pieceId;
		try {

			File file = new File(fileName);
			in = new FileInputStream(file);
			int bytesRead=in.read(piece);
			in.close();
			//System.out.println(bytesRead);
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

	public void combinePieces() {
		FileOutputStream fout = null;
		FileInputStream fin = null;
		byte[] bytes = null;
		int nPieces = MetaInfo.getnPieces();
		String fileName = MetaInfo.getPeerBasePath() + MetaInfo.getFileName();
		File file = new File(fileName);
		try {
			fout = new FileOutputStream(file);
			for (int i = 0; i < nPieces-1; i++) {
				String pieceName = MetaInfo.getPeerBasePath() + "piece_" + i;
				fin = new FileInputStream(pieceName);
				bytes = new byte[MetaInfo.getPieceSize()];
				fin.read(bytes);
				fout.write(bytes);
				fin.close();
			}
			// finally write the last piece
			String pieceName = MetaInfo.getPeerBasePath() + "piece_" + (nPieces-1);
			fin = new FileInputStream(pieceName);
			bytes = new byte[MetaInfo.getLastPieceSize()];
			fin.read(bytes);
			fout.write(bytes);
			fin.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fin != null)
					fin.close();
				if (fout != null)
					fout.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createPieces() {
		OutputStream out_piece = null;
		RandomAccessFile rFile = null;
		byte[] buffer;
		int pos = 0;

		String fileName = MetaInfo.getFileName();
		String basePath = MetaInfo.getPeerBasePath();
		String filePath = basePath + fileName;
		File file = new File(filePath);

		int pieceSize = MetaInfo.getPieceSize();
		int nPieces = MetaInfo.getnPieces();

		try {
			rFile = new RandomAccessFile(file, "r");

			for (int i = 0; i < nPieces - 1; i++) {
				String pieceName = "piece_" + i;
				File pieceFile = new File(basePath + pieceName);
				out_piece = new FileOutputStream(pieceFile);

				buffer = new byte[pieceSize];
				rFile.seek(pos);

				// Read pieceSize equivalent bytes from the original file
				rFile.read(buffer);
				out_piece.write(buffer);
				pos += pieceSize;

				out_piece.close();
			}
			// Write the last piece
			pieceSize = MetaInfo.getLastPieceSize();
			String pieceName = "piece_" + (nPieces - 1);
			File pieceFile = new File(basePath + pieceName);
			out_piece = new FileOutputStream(pieceFile);

			buffer = new byte[pieceSize];
			rFile.seek(pos);

			// Read pieceSize equivalent bytes from the original file
			rFile.read(buffer);
			out_piece.write(buffer);
			pos += pieceSize;
			assert (pos == MetaInfo.getFileSize());

			out_piece.close();
			rFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out_piece != null)
					out_piece.close();
				if (rFile != null)
					rFile.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void finish() {
		combinePieces();
	}

	public void deletePieces() {
		String dir = MetaInfo.getPeerBasePath();
		File dirFile = new File(dir);
		for (File file : dirFile.listFiles()) {
			if (file.getName().contains("piece"))
				file.delete();
		}
	}
	
	public static void main(String[] args)
	{
		
		FileInputStream in=null;
		byte[] temp=new byte[5];
		try {

			
			File file = new File("a.tmp");
			in = new FileInputStream(file);
			in.read(temp);
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

		FileHandlingUtils fh=new FileHandlingUtils();
		fh.writePiece(45, temp);
	}

}
