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

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
        this.conIDNameHashMap=new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentLinkedQueue<>();
    }
    public List<String> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    public String getUserName(int connid){
        return this.conIDNameHashMap.get(connid);
    }

    public Boolean containsUser(String user) {
        return userNameHashMap.containsKey(user);
    }

    public Boolean putIfAbsenct(String name, String password) {
        User output = userNameHashMap.putIfAbsent(name, new User(name, password));
        if (output==null) {
                registeredUsers.add(name);
        }
        return output == null;
    }

    public String getNameFromConId(int conid) {
        return conIDNameHashMap.get(conid);
    }

    public void addConidName(int conid,String name) {
        this.conIDNameHashMap.put(conid,name);
    }

    public void removeFromConidName(int conid) {
        this.conIDNameHashMap.remove(conid);
    }

    public User getUser(String user) {
        return userNameHashMap.get(user);
    }

//    public void addUser(String name, String password) {
//        usernameHashMapReadWriteLock.writeLock().lock();
//        try {
//            userNameHashMap.put(name, new User(name, password));
//        } finally {
//            usernameHashMapReadWriteLock.writeLock().unlock();
//        }
//    }
}
