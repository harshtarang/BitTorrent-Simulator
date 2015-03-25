package edu.ufl.cise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;

public class ServerWorker extends Thread {
	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
		
	}

	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			//byte[] buffer = new byte[1024];
			int pieceLength = MetaInfo.getPieceSize();
			byte[] piece = new byte[pieceLength];
			int offset = 0;
			while (true) {
				int flag = in.read(piece, offset, pieceLength);
				if( flag == -1){
					// Create a job and send to executor service
					//BitTorrentProtocol.processInput(piece);
					piece = new byte[pieceLength];
				}
				else{
					//			clientSocket.close();
				}
			}
		} catch (IOException e) {
			// Add a log statement
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}
}