package bgu.spl.net.api.bidi;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {

    public BidiMessagingProtocolImpl() {
    }

    public void start(int connectionId, Connections<T> connections) {

    }

    public void process(T message) {

    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate() {
        return true;
    }
}
