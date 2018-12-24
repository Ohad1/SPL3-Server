package bgu.spl.net.Assignment3;

import bgu.spl.net.srv.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocolImpl<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl connections;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocolImpl<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.connections = new ConnectionsImpl();
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");
            BidiMessagingProtocolImpl protocol=  protocolFactory.get();

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                ConnectionHandlerImpl<T> handler = new ConnectionHandlerImpl<>(
                        clientSock,
                        encdecFactory.get(),
                        protocol);
                int connectionId = connections.add(handler);
                protocol.start(connectionId, connections);
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null){
            sock.close();
        }

    }

    protected abstract void execute(ConnectionHandlerImpl<T>  handler);

}
