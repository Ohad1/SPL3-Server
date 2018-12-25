package bgu.spl.net.Assignment3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Manager {
    private ConcurrentHashMap<String, User> userNameHashMap;
    private final ReadWriteLock readWriteLock;

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
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

    public Boolean containsUser(String user) {
        readWriteLock.readLock().lock();
        try {
            return userNameHashMap.containsKey(user);
        } finally {
            readWriteLock.readLock().unlock();
        }
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
        readWriteLock.writeLock();
        try {
            userNameHashMap.put(name, new User(name, password));
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
