package bgu.spl.net.Assignment3;

import java.util.function.Supplier;

public class ThreadPerClientServer<T> extends BaseServer<T> {

    public ThreadPerClientServer (int port,
        Supplier<BidiMessagingProtocolImpl<T>> protocolFactory,
        Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        super(port, protocolFactory, encdecFactory);
    }

    @Override
    protected void execute(ConnectionHandlerImpl<T> handler) {
        new Thread(handler).start();
    }
}
