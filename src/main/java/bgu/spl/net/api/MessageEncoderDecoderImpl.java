package bgu.spl.net.api;

import java.nio.ByteBuffer;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {
    private byte[] bytes = new byte[1024]; //start with 1k
    private int len = 0;

    public T decodeNextByte(byte nextByte) {
        T output = ;
        return output;
    }

    public byte[] encode(T message) {
        return new byte[5];
    }
}
