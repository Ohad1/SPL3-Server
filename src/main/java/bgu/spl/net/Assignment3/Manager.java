package bgu.spl.net.Assignment3;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Manager {
    private ConcurrentHashMap<String, User> userNameHashMap;


    private ConcurrentHashMap<Integer, String> conIDNameHashMap;

    private final ReadWriteLock readWriteLock;

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.conIDNameHashMap=new ConcurrentHashMap<>();
    }
    public List<String> getRegisteredUsers() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(userNameHashMap.keySet());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public ConcurrentHashMap<String, User> getUserNameHashMap() {
        return userNameHashMap;
    }

    public String getUserName(int connid){
        return this.conIDNameHashMap.get(connid);
    }
    public Boolean containsUser(String user) {
        readWriteLock.readLock().lock();
        try {
            return userNameHashMap.containsKey(user);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    public void addConidName(int conid,String name) {
        this.conIDNameHashMap.put(conid,name);
    }
    public void removeFromConidName(int conid) {
        this.conIDNameHashMap.remove(conid);
    }


    public User getUser(String user) {
        readWriteLock.readLock().lock();
        try {
            return userNameHashMap.get(user);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void addUserToMap(String name, String password) {
        readWriteLock.writeLock().lock();
        try {
            userNameHashMap.put(name, new User(name, password));
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
