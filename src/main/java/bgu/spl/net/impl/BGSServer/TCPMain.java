package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Assignment3.Manager;
import bgu.spl.net.api.BidiMessagingProtocolImpl;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

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
