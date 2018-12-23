package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandlerImpl;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

    <T> int add(ConnectionHandlerImpl<T> handler);
}
