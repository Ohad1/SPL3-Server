package bgu.spl.net.Assignment3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConnectionsImpl<T> implements Connections<T> {

    ConcurrentHashMap<Integer, ConnectionHandlerImpl> connectionHandlerConcurrentHashMap;
    AtomicInteger countId;
    private ReadWriteLock readWriteLock;

    public ConnectionsImpl() {
        this.connectionHandlerConcurrentHashMap = new ConcurrentHashMap<>();
        this.countId = new AtomicInteger(0);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public boolean send(int connectionId, T msg) {
        if (!connectionHandlerConcurrentHashMap.containsKey(connectionId)) {

            return false;
        }
        ConnectionHandlerImpl connectionHandler = connectionHandlerConcurrentHashMap.get(connectionId);
        connectionHandler.send(msg);
        return true;
    }

    public void broadcast(T msg) {
        readWriteLock.readLock().lock();
        try {
            for (ConnectionHandlerImpl connectionHandler : connectionHandlerConcurrentHashMap.values()) {
                connectionHandler.send(msg);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void disconnect(int connectionId) {

    }

    public int add(ConnectionHandlerImpl connectionHandler) {
        readWriteLock.writeLock().lock();
        try {
            int id = countId.getAndIncrement();
            connectionHandlerConcurrentHashMap.put(id, connectionHandler);
            return id;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
