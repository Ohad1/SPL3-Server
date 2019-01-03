package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {

    ConcurrentHashMap<Integer, ConnectionHandler> connectionHandlerConcurrentHashMap;
    AtomicInteger countId;

    public ConnectionsImpl() {
        this.connectionHandlerConcurrentHashMap = new ConcurrentHashMap<>();
        this.countId = new AtomicInteger(0);
    }

    public boolean send(int connectionId, T msg) {
        // prevent writing in disconnect and add
        if (!connectionHandlerConcurrentHashMap.containsKey(connectionId)) {
            return false;
        }

        ConnectionHandler connectionHandler = connectionHandlerConcurrentHashMap.get(connectionId);
        // todo check need to check if is null
        if (connectionHandler!=null) {
            // not to send in parallel
            synchronized (connectionHandler) {
                if (connectionHandler!=null) {
                    connectionHandler.send(msg);
                }
            }
        }
        return true;

    }

    public void broadcast(T msg) {
        for (ConnectionHandler connectionHandler : connectionHandlerConcurrentHashMap.values()) {
            connectionHandler.send(msg);
        }
    }

    public void disconnect(int connectionId) {
        connectionHandlerConcurrentHashMap.remove(connectionId);
    }

    public int add(ConnectionHandler connectionHandler) {
        int id = countId.getAndIncrement();
        connectionHandlerConcurrentHashMap.put(id, connectionHandler);
        return id;
    }
}
