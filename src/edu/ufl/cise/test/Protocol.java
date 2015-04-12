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
				System.out.println("Condition1");
				// means start. Send first message
				PeerInfo.getInstance().updateFirstMessageReceived(peerId);
				PeerInfo.getInstance().updateFirstMessageSent(peerId);
				
				SendMessage message = new SendMessage(peerId, (""+currPeerId).getBytes());
				System.out.println("Sending message : " + currPeerId + " to: "
						+ peerId);
				ExecutorPool.getInstance().getPool().execute(message);
			} else {
				System.out.println("Condition2");
				// Send second message
				System.out.println("Sending message : firstMessage from: " + currPeerId + " to: "
						+ peerId);

				PeerInfo.getInstance().updateSecondMessageSent(peerId);
				SendMessage message = new SendMessage(peerId, "firstMessage".getBytes());
				ExecutorPool.getInstance().getPool().execute(message);
			}
		}else if(!PeerInfo.getInstance().getMap().get(peerId).isSecondMessageSent()){
			// Send second message
			System.out.println("Condition3");
			System.out.println("Sending message : firstMessage from: " + currPeerId + " to: "
					+ peerId);

			PeerInfo.getInstance().updateSecondMessageSent(peerId);
			SendMessage message = new SendMessage(peerId, "firstMessage".getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
		}else {
			System.out.println("Condition4");
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
