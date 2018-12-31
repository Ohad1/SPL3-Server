package bgu.spl.net.Assignment3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Manager {
    private ConcurrentHashMap<String, User> userNameHashMap;
    private ConcurrentHashMap<Integer, String> conIDNameHashMap;
    private ConcurrentLinkedQueue<String> registeredUsers;
    private final ReadWriteLock usernameHashMapReadWriteLock;
    private final ReadWriteLock conIDNameHashMapReadWriteLock;
    private final ReadWriteLock registeredUsersReadWriteLock;

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
        this.conIDNameHashMap=new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentLinkedQueue<>();
        this.usernameHashMapReadWriteLock = new ReentrantReadWriteLock();
        this.conIDNameHashMapReadWriteLock = new ReentrantReadWriteLock();
        this.registeredUsersReadWriteLock = new ReentrantReadWriteLock();
    }
    public List<String> getRegisteredUsers() {
        registeredUsersReadWriteLock.readLock().lock();
        try {
            return new ArrayList<>(registeredUsers);
        } finally {
            registeredUsersReadWriteLock.readLock().unlock();
        }
    }

    public String getUserName(int connid){
        conIDNameHashMapReadWriteLock.readLock().lock();
        try {
            return this.conIDNameHashMap.get(connid);
        } finally {
            conIDNameHashMapReadWriteLock.readLock().unlock();
        }
    }
    public Boolean containsUser(String user) {
        usernameHashMapReadWriteLock.readLock().lock();
        try {
            return userNameHashMap.containsKey(user);
        } finally {
            usernameHashMapReadWriteLock.readLock().unlock();
        }
    }

    public String getNameFromConId(int conid) {
        conIDNameHashMapReadWriteLock.readLock().lock();
        try {
            return conIDNameHashMap.get(conid);
        } finally {
            conIDNameHashMapReadWriteLock.readLock().unlock();
        }
    }

    public void addConidName(int conid,String name) {
        conIDNameHashMapReadWriteLock.writeLock().lock();
        try {
            this.conIDNameHashMap.put(conid,name);
        } finally {
            conIDNameHashMapReadWriteLock.writeLock().unlock();
        }
    }

    public void removeFromConidName(int conid) {
        conIDNameHashMapReadWriteLock.writeLock().lock();
        try {
            this.conIDNameHashMap.remove(conid);
        } finally {
            conIDNameHashMapReadWriteLock.writeLock().unlock();
        }
    }


    public User getUser(String user) {
        usernameHashMapReadWriteLock.readLock().lock();
        try {
            return userNameHashMap.get(user);
        } finally {
            usernameHashMapReadWriteLock.readLock().unlock();
        }
    }

    public void addUser(String name, String password) {
        usernameHashMapReadWriteLock.writeLock().lock();
        try {
            userNameHashMap.put(name, new User(name, password));
        } finally {
            usernameHashMapReadWriteLock.writeLock().unlock();
        }
        registeredUsersReadWriteLock.writeLock().lock();
        try {
            registeredUsers.add(name);
        } finally {
            registeredUsersReadWriteLock.writeLock().unlock();
        }
    }
}
