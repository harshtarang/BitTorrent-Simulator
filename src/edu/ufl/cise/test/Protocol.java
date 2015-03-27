package edu.ufl.cise.test;

public class Protocol implements Runnable {

	int peerId; // this belongs for the peer whom message has to be sent
	String message;
	int currPeerId;

	public Protocol(int peerId) {
		this.peerId = peerId;
	}

	public Protocol(int currPeerId, int peerId, String message) {
		this.currPeerId = currPeerId;
		this.peerId = peerId;
		this.message = message;
	}

	public void performActionAndChangeState() {
		if (isNumeric(message)) {
			if (!PeerInfo.getInstance().getMap().get(peerId)
					.isFirstMessageSent()) {
				// means start. Send first message
				PeerInfo.getInstance().updateFirstMessageReceived(peerId);
				SendMessage message = new SendMessage(peerId, "" + currPeerId);
				System.out.println("Sending message : " + currPeerId + " to: "
						+ peerId);
				ExecutorPool.getInstance().getPool().execute(message);
				PeerInfo.getInstance().updateFirstMessageSent(peerId);
			} else {
				// Send second message
				System.out.println("Sending message : firstMessage from: " + currPeerId + " to: "
						+ peerId);

				SendMessage message = new SendMessage(peerId, "firstMessage");
				ExecutorPool.getInstance().getPool().execute(message);
				PeerInfo.getInstance().updateSecondMessageSent(peerId);
			}
		}else{
			PeerInfo.getInstance().updateSecondMessageReceived(peerId);
			System.out.println("Nothing to do: " + currPeerId);
		}
	}

	public void run() {
		performActionAndChangeState();
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
