package bgu.spl.net.Assignment3;

import java.util.Date;

public class PrivateMessage extends Message{
    private String recipient;

    public PrivateMessage(String sender, String recipient, String content) {
        super(sender, content);
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

}
