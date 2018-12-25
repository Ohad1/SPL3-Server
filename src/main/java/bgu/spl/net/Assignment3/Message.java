package bgu.spl.net.Assignment3;

import java.util.Date;

public abstract class Message {
    protected String sender;
    protected String content;
    protected Date sentTime;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.sentTime = new Date();
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
