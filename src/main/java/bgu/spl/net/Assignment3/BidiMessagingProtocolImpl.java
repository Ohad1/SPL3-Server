package bgu.spl.net.Assignment3;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {

    private  int con_id;
    private Connections connections;
    private Manager manager;
    public BidiMessagingProtocolImpl(Manager manager) {
        this.manager = manager;
    }

    public void start(int connectionId, Connections<T> connections) {
        this.con_id=connectionId;
        this.connections=connections;
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
