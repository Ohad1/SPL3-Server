package bgu.spl.net.Assignment3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandlerImpl<T> implements Runnable, ConnectionHandler<T> {
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

    public void send(T msg) { // todo check generic
        try {
            out.write(encdec.encode(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
                /*
                if (response != null) {
                    out.write(encdec.encode(response));
                    out.flush();
                }
             */
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
}