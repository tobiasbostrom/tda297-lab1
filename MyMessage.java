
import mcgui.*;

/**
 *
 * @author Tobias Boström
 */

public class MyMessage extends Message {

	private String text;
	private int seq;
    private int origSeq;
	public static int TOSEQ = 0;
	public static int FROMSEQ = 1;
	private int messageType;
	private int origSender;

	public MyMessage(int sender, String text, int seq, int messageType) {
		super(sender);
        this.origSender = sender;
		this.text = text;
		this.seq = seq;
		this.messageType = messageType;
	}
    public MyMessage(MyMessage message, int sender, int seq , int messageType) {
        super(sender);
        this.text = message.getText();
        this.seq = seq;
        this.origSender = message.getSender();
        this.origSeq = message.getSeq();
        this.messageType = messageType;

    }
    //transform message to original message
    public void revert() {
        this.setSender(origSender);
        this.setSeq(origSeq);
    }
    
    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */
    public String getText() {
        return text;
    }
    
    public int getSeq() {
		return seq;
	}
    
    public int getOrigSender() {
    	return origSender;
    }

	public void setSeq(int seq) {
		this.seq = seq;
	}
    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getOrigSeq() {
        return origSeq;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sender;
		result = prime * result + seq;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyMessage other = (MyMessage) obj;
		if (sender != other.sender)
			return false;
		if (seq != other.seq)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public static final long serialVersionUID = 0;
}
