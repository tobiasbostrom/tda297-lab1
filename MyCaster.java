import java.util.ArrayList;
import mcgui.*;

/**
 *
 * @author Tobias Boström
 */
public class MyCaster extends Multicaster {
	private int seq;
	private ArrayList<MyMessage> delivered;
    private ArrayList<MyMessage> notDelivered;
	private ArrayList<MyMessage> messageBag;
	private int[] next;
	private boolean isSequencer;
	private int sequencerId;
	private boolean[] activeHosts;

	/**
	 * No initializations needed for this simple one
	 */

	public void init() {
		mcui.debug("The network has " + hosts + " hosts!"); //test commit
		seq = 0;
		sequencerId = 0;
		delivered = new ArrayList<MyMessage>();
		messageBag = new ArrayList<MyMessage>();
		activeHosts = new boolean[hosts];
		next = new int[hosts];
		for (int i = 0; i < hosts; i++) {
			// setting starting sequence numbers.
			next[i] = 1;
			// all hosts start out active.
			activeHosts[i] = true;
		}
		if (id == sequencerId) {
			isSequencer = true;
		}
		// mcui.debug(isSequencer ? "me: sequencer node" : "me: normal node");

	}

	/**
	 * The GUI calls this module to multicast a message
	 */
	public void cast(String messageText) {

		// seq++;
		// mcui.debug("Sequence number: \"" + seq + "\"");
        MyMessage message = new MyMessage(id, id, messageText, seq, MyMessage.TOSEQ);
		bcom.basicsend(sequencerId, message);
        notDelivered.add(message);

	}

	public void basicreceive(int peer, Message message) {
		if (isSequencer) {
			if (((MyMessage) message).getMessageType() == MyMessage.TOSEQ) {
				// mcui.debug("broadcasts out the message it received from a node");
				r_broadcast(((MyMessage) message).getText(),
						((MyMessage) message).getSender());
			} else {
				// mcui.debug("tries to deliver message from itself");
				r_deliver(peer, message);
			}

		} else {
			// mcui.debug("A non sequencer node receives a message, tries to deliver it");
			r_deliver(peer, message);
		}

	}

	public void r_broadcast(String messageText, int source) {
		seq++;
		// mcui.debug("Sequence number: \"" + seq + "\"");
		for (int i = 0; i < hosts; i++) {
			bcom.basicsend(i, new MyMessage(id, source, messageText, seq,
					MyMessage.FROMSEQ));
		}
		// mcui.debug("Sent out: \"" + messageText + "\"");
	}

	public void r_deliver(int peer, Message message) {
		// if not already delivered
		if (!delivered.contains(message)) {
			// if i am not sender of m
			if (message.getSender() != id) {
				for (int i = 0; i < hosts; i++) {
					bcom.basicsend(i, message);
				}
			}
			// FIFO
			f_deliver(message.getSender(), message);
			delivered.add((MyMessage) message);

		} 
	}

	public void f_deliver(int peer, Message message) {
		int sender = message.getSender();
		messageBag.add((MyMessage) message);
		// mcui.debug("messagebag size: " + messageBag.size());
		for (int i = 0; i < messageBag.size(); i++) {
			MyMessage m = messageBag.get(i);
			/*
			 * mcui.debug("found message in bag: sender: " + m.getSender() +
			 * " seq: " + m.getSeq() + " text: " + m.getText());
			 */
			if (m.getSender() == sender && m.getSeq() == next[sender]) {
                notDelivered.remove(message);
                mcui.deliver(((MyMessage) message).getOrigSender(),
						((MyMessage) message).getText());
				next[sender]++;
				messageBag.remove(m);
			}
		}

	}

	/**
	 * Receive a basic message
	 * 
	 * @param message
	 *            The message received
	 */

	/**
	 * Signals that a peer is down and has been down for a while to allow for
	 * messages taking different paths from this peer to arrive.
	 * 
	 * @param peer
	 *            The dead peer
	 */
	public void basicpeerdown(int peer) {
		activeHosts[peer] = false;
		// elects the new sequencer, which will be the node with the lowest id
		// still active.
		for (int i = 0; i < hosts; i++) {
            if (activeHosts[i]) {
                sequencerId = i;
                break;
            }
        }
        //sends out all messages that has not yet been delivered to the new
        //sequencer.
        for (int i = 0; i < notDelivered.size(); i++) {
            bcom.basicsend(sequencerId, notDelivered.get(i));
        }

		if (id == sequencerId) {
			isSequencer = true;
		}
		// mcui.debug(isSequencer ? "Sequencer node" : "Normal node");
		// mcui.debug("The sequencer id is: " + sequencerId);
	}
}
