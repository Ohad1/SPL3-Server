package bgu.spl.net.Assignment3;

import java.io.IOException;

public class ReactorMain {
    public static void main(String[] args) throws IOException {
        Manager manager = new Manager();
        Server.reactor(
                100,
                7777, //port
                () -> new BidiMessagingProtocolImpl(manager), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
