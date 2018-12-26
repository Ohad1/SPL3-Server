package bgu.spl.net.Assignment3;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private String username;
    private String password;
    private Boolean isLoggedin;
    private ConcurrentLinkedQueue<Post> userPosts;
    private ConcurrentLinkedQueue<Message> unreadMessages;
    private ConcurrentLinkedQueue<PrivateMessage> userPrivateMessages;
    private ConcurrentLinkedQueue<String> followers; // number of people that follow me
    private AtomicInteger following; // number of people i follow
    private Object lock;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedin = false;
        this.userPosts = new ConcurrentLinkedQueue<>();
        this.unreadMessages = new ConcurrentLinkedQueue<>();
        this.userPrivateMessages = new ConcurrentLinkedQueue<>();
        this.followers = new ConcurrentLinkedQueue<>();
        this.following = new AtomicInteger(0);
    }

    public Boolean getLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(Boolean loggedin) {
        isLoggedin = loggedin;
    }

    public void addPost(Post post) {
        synchronized (userPosts) {
            userPosts.add(post);
        }
    }

    public int getNumOfPosts () {
        synchronized (userPosts) {
            return userPosts.size();
        }
    }

    public void addPrivateMessage(PrivateMessage privateMessage) {
        userPrivateMessages.add(privateMessage);
    }

    public void addFollowers(String string) {
        synchronized (followers) {
            followers.add(string);
        }
    }

    public int getNumOfFollowers () {
        synchronized (followers) {
            return followers.size();
        }
    }

    public void incrementFollowing() {
        following.getAndIncrement();
    }

    public AtomicInteger getFollowing() {
        return following;
    }

    public String getPassword() {
        return password;
    }

    public boolean alreadyInFollowers(String username){
        if(followers.contains(username))
            return true;
        else
            return false;
    }
    public ConcurrentLinkedQueue<Message> getUnreadMessages() {
        return unreadMessages;
    }
}
