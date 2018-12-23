package bgu.spl.net.srv.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandlerImpl<T> implements ConnectionHandler<T> {
    private final BidiMessagingProtocolImpl<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionId;

    public ConnectionHandlerImpl(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocolImpl<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connectionId = -1;
    }
    public void send(T msg) {
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
}
