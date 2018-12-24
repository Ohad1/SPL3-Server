package bgu.spl.net.Assignment3;

import java.util.Date;

public class PrivateMessage {
    private String sender;
    private String recipient;
    private String content;
    private Date timesent;


    public PrivateMessage(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timesent =  new Date();
    }
}
