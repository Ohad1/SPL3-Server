package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Assignment3.Manager;
import bgu.spl.net.api.BidiMessagingProtocolImpl;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

import java.io.IOException;

public class ReactorMain {
    public static void main(String[] args) throws IOException {
        Manager manager = new Manager();
        Server.reactor(
                Integer.parseInt(args[1]), //nthreads
                Integer.parseInt(args[2]), //port
                () -> new BidiMessagingProtocolImpl(manager), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
