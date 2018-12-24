package bgu.spl.net.Assignment3;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Post {

    private String sender;
    private String content;
    private Date timesent;
    private ConcurrentLinkedQueue<String> tagged;


    public Post(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timesent =  new Date();
        String[] split = content.split( " ");
        for (String string : split) {
            if (string.charAt(0) == '@') {
                tagged.add(string.substring(1));
            }
        }
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimesent() {
        return timesent;
    }

    public ConcurrentLinkedQueue<String> getTagged() {
        return tagged;
    }
}
