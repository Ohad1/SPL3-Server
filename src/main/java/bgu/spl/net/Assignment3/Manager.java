package bgu.spl.net.Assignment3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager {
    private ConcurrentHashMap<String, User> userNameHashMap;
    private ConcurrentHashMap<Integer, String> connectionIdNameHashMap;
    private ConcurrentLinkedQueue<String> registeredUsers;

    public Manager() {
        this.userNameHashMap = new ConcurrentHashMap<>();
        this.connectionIdNameHashMap =new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentLinkedQueue<>();
    }
    public List<String> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    public String getUserName(int connid){
        return this.connectionIdNameHashMap.get(connid);
    }

    public Boolean containsUser(String user) {
        return userNameHashMap.containsKey(user);
    }

    public Boolean putIfAbsent(String name, String password) {
        User output = userNameHashMap.putIfAbsent(name, new User(name, password));
        if (output==null) {
                registeredUsers.add(name);
        }
        return output == null;
    }

    public String getNameFromConId(int connectionId) {
        return connectionIdNameHashMap.get(connectionId);
    }

    public void addConnection(int connectionId, String name) {
        this.connectionIdNameHashMap.put(connectionId,name);
    }

    public void removeConnection(int connectionId) {
        this.connectionIdNameHashMap.remove(connectionId);
    }

    public User getUser(String user) {
        return userNameHashMap.get(user);
    }
    
}
