package bgu.spl.net.Assignment3;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Post extends Message{

    private ConcurrentLinkedQueue<String> tagged;

    public Post(String sender, String content) {
        super(sender, content);
        String[] split = content.split( " ");
        for (String string : split) {
            if (string.charAt(0) == '@') {
                tagged.add(string.substring(1));
            }
        }
    }



    public ConcurrentLinkedQueue<String> getTagged() {
        return tagged;
    }
}
