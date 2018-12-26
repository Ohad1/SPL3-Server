package bgu.spl.net.Assignment3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private String username;
    private String password;
    private Boolean isLoggedin;
    private ConcurrentLinkedQueue<Post> userPosts;
    private ConcurrentLinkedQueue<Message> unreadMessages;
    private ConcurrentLinkedQueue<String> userPrivateMessages;
    private ConcurrentLinkedQueue<String> followers; // number of people that follow me
    private AtomicInteger following; // number of people i follow
    private final ReadWriteLock readWriteLockPosts;
    private final ReadWriteLock readWriteLockFollowers;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedin = false;
        this.userPosts = new ConcurrentLinkedQueue<>();
        this.unreadMessages = new ConcurrentLinkedQueue<>();
        this.userPrivateMessages = new ConcurrentLinkedQueue<>();
        this.followers = new ConcurrentLinkedQueue<>();
        this.following = new AtomicInteger(0);
        this.readWriteLockPosts = new ReentrantReadWriteLock();
        this.readWriteLockFollowers = new ReentrantReadWriteLock();
    }

    public Boolean getLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(Boolean loggedin) {
        isLoggedin = loggedin;
    }

    public void addPost(Post post) {
        readWriteLockPosts.writeLock().lock();
        try {
            userPosts.add(post);
        }
        finally {
            readWriteLockPosts.writeLock().unlock();
        }
    }

    public int getNumOfPosts () {
        readWriteLockPosts.readLock().lock();
        try {
            return userPosts.size();
        }
        finally {
            readWriteLockPosts.readLock().unlock();
        }
    }

    public void addPrivateMessage(String privateMessage) {
        userPrivateMessages.add(privateMessage);
    }

    public void addFollower(String string) {
        readWriteLockFollowers.writeLock().lock();
        try {
            followers.add(string);
        }
        finally {
            readWriteLockFollowers.writeLock().unlock();
        }
    }

    public void removeFollower(String string) {
        readWriteLockFollowers.writeLock().lock();
        try {
            followers.remove(string);
        }
        finally {
            readWriteLockFollowers.writeLock().unlock();
        }
    }

    public int getNumOfFollowers () {
        readWriteLockFollowers.readLock().lock();
        try {
            return followers.size();
        }
        finally {
            readWriteLockFollowers.readLock().unlock();
        }
    }

    public void incrementFollowing() {
        following.getAndIncrement();
    }

    public void decrementFollowing() {
        following.getAndDecrement();
    }

    public AtomicInteger getNumOfFollowing() {
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
