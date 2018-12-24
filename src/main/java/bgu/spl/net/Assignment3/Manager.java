package bgu.spl.net.Assignment3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {
    private ConcurrentHashMap<String, String> userPasswordHashMap;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userFollowersHashMap;
    private ConcurrentHashMap<String, AtomicInteger> userFollowingsHashMap;
    private ConcurrentLinkedQueue<String> registeredUsers;
    private ConcurrentHashMap<String, Boolean> userIsLoggedinHashMap;

    public Manager(ConcurrentHashMap<String,
            String> userPasswordHashMap,
                   ConcurrentHashMap<String,
                           ConcurrentLinkedQueue<String>> userFollowersHashMap,
                   ConcurrentHashMap<String, AtomicInteger> userFollowingsHashMap,
                   ConcurrentLinkedQueue<String> registeredUsers,
                   ConcurrentHashMap<String, Boolean> userIsLoggedinHashMap) {
        this.userPasswordHashMap = userPasswordHashMap;
        this.userFollowersHashMap = userFollowersHashMap;
        this.userFollowingsHashMap = userFollowingsHashMap;
        this.registeredUsers = registeredUsers;
        this.userIsLoggedinHashMap = userIsLoggedinHashMap;
    }

    public ConcurrentHashMap<String, String> getUserPasswordHashMap() {
        return userPasswordHashMap;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> getUserFollowersHashMap() {
        return userFollowersHashMap;
    }

    public ConcurrentHashMap<String, AtomicInteger> getUserFollowingsHashMap() {
        return userFollowingsHashMap;
    }

    public ConcurrentLinkedQueue<String> getRegisteredUsers() {
        return registeredUsers;
    }

    public ConcurrentHashMap<String, Boolean> getUserIsLoggedinHashMap() {
        return userIsLoggedinHashMap;
    }
}
