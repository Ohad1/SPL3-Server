package bgu.spl.net.Assignment3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String username;
    private String password;
    private Boolean isLoggedin;
    private ConcurrentLinkedQueue<Post> userPosts;
    private ConcurrentLinkedQueue<PrivateMessage> userPrivateMessages;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedin = false;
        this.userPosts = new ConcurrentLinkedQueue<>();
        this.userPrivateMessages = new ConcurrentLinkedQueue<>();
    }

    public Boolean getLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(Boolean loggedin) {
        isLoggedin = loggedin;
    }

    public void addPost(Post post) {
        userPosts.add(post);
    }

    public void addPrivateMessage(PrivateMessage privateMessage) {
        userPrivateMessages.add(privateMessage);
    }
}
