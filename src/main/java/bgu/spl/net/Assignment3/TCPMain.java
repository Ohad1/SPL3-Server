package bgu.spl.net.Assignment3;

import java.io.IOException;

public class TCPMain {
    public static void main(String[] args) throws IOException {
        Manager manager = new Manager();
        Server.threadPerClient(
                7777, //port
                () -> new BidiMessagingProtocolImpl(manager), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
