package bgu.spl.net.Assignment3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private String username;
    private String password;
    private Boolean isLoggedin;
    private int ConnId;
    private LinkedList<String> userPosts;
    private LinkedList<String> unreadMessages;
    private LinkedList<String> userPrivateMessages;// messages that the user sent
    private ConcurrentLinkedQueue<String> followers; // number of people that follow me
    private AtomicInteger following; // number of people i follow
    private final ReadWriteLock readWriteLockPosts;
    private final ReadWriteLock readWriteLockFollowers;



    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedin = false;
        this.ConnId = -1;
        this.userPosts = new LinkedList<>();
        this.unreadMessages = new LinkedList<>();
        this.userPrivateMessages = new LinkedList<>();
        this.followers = new ConcurrentLinkedQueue<>();
        this.following = new AtomicInteger(0);
        this.readWriteLockPosts = new ReentrantReadWriteLock();
        this.readWriteLockFollowers = new ReentrantReadWriteLock();
    }

    public ReadWriteLock getReadWriteLockFollowers() {
        return readWriteLockFollowers;
    }

    public Boolean getLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(Boolean loggedin) {
        isLoggedin = loggedin;
    }

    public ConcurrentLinkedQueue<String> getFollowers() {
        readWriteLockFollowers.readLock().lock();
        try {
            return followers;
        }
        finally {
            readWriteLockFollowers.readLock().unlock();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getConnId() {
        return ConnId;
    }

    public void setConnId(int connId) {
        ConnId = connId;
    }

    public void addPost(String post) {
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
        readWriteLockFollowers.readLock().lock();
        try {
            return followers.contains(username);
        }
        finally {
            readWriteLockFollowers.readLock().unlock();
        }
    }
    public LinkedList<String> getUnreadMessages() {
        return unreadMessages;
    }

    public void addUnreadMessage(String message) {
        unreadMessages.add(message);
    }
}
