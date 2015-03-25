package edu.ufl.cise.protocol;

import edu.ufl.cise.config.MetaInfo;

public class BitField extends Message {
	byte[] bitField;

	public BitField(byte[] bitArray) {
		int fileSize = MetaInfo.getFileSize();
		int pieceSize = MetaInfo.getPieceSize();

		int numberOfPieces = fileSize / pieceSize;

		for (int i = 0; i < numberOfPieces; i++)
			bitField[i] = bitArray[i];
	}
}
