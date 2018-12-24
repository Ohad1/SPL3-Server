package bgu.spl.net.Assignment3;

import java.io.Closeable;
import java.io.IOException;

public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;

}
