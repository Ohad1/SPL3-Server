package bgu.spl.net.srv;

import bgu.spl.net.Assignment3.ConnectionHandler;
import bgu.spl.net.Assignment3.Connections;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConnectionsImpl<T> implements Connections<T> {

    ConcurrentHashMap<Integer, bgu.spl.net.Assignment3.ConnectionHandler> connectionHandlerConcurrentHashMap;
    AtomicInteger countId;
    private ReadWriteLock readWriteLock;

    public ConnectionsImpl() {
        this.connectionHandlerConcurrentHashMap = new ConcurrentHashMap<>();
        this.countId = new AtomicInteger(0);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public boolean send(int connectionId, T msg) {
        readWriteLock.readLock().lock();
        try {
            if (!connectionHandlerConcurrentHashMap.containsKey(connectionId)) {
                return false;
            }
            bgu.spl.net.Assignment3.ConnectionHandler connectionHandler = connectionHandlerConcurrentHashMap.get(connectionId);
            synchronized (connectionHandler) {
                connectionHandler.send(msg);
            }
            return true;
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    public void broadcast(T msg) {
        readWriteLock.readLock().lock();
        try {
            for (bgu.spl.net.Assignment3.ConnectionHandler connectionHandler : connectionHandlerConcurrentHashMap.values()) {
                connectionHandler.send(msg);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void disconnect(int connectionId) {
        readWriteLock.writeLock().lock();
        try {
            connectionHandlerConcurrentHashMap.remove(connectionId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public int add(ConnectionHandler connectionHandler) {
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
