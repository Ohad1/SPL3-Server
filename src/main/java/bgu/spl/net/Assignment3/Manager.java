package bgu.spl.net.Assignment3;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {
    private ConcurrentHashMap<String, User> userNameHashMap;

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, User> getUserNameHashMap() {
        return userNameHashMap;
    }

    public void addUserToMap(String name, String password) {
        userNameHashMap.put(name, new User(name, password));
    }
}
