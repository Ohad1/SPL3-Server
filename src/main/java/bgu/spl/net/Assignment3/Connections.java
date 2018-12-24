package bgu.spl.net.Assignment3;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

}
