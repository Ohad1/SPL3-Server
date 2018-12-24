package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1024]; //start with 1k
    private int len = 0;
    private int opcodeCount = 0;
    private String output = "";
    private short opcode;
    private byte[] opcodeArray = new byte[2];

    private int numOfZeros = 0;

    private byte[] numOfUsersArray = new byte[2];
    private int countNum = 0;
    private short numOfUsers;



    private void restart()
    {
        len = 0;
        opcodeCount = 0;
        numOfZeros = 0;
        output = "";
    }

    public String decodeNextByte(byte nextByte) {
        if (opcodeCount == 0 | opcodeCount == 1) {
            opcodeArray[opcodeCount] = nextByte;
            opcodeCount++;
            return null;
        }
        else if (opcodeCount == 2){
            opcode = bytesToShort(opcodeArray);
            output += opcode;
            opcodeCount = -1;
        }
        switch (opcode) {
            case 1:
                return decodeNextByteRegisterLogin(nextByte);
            case 2:
                return decodeNextByteRegisterLogin(nextByte);
            case 3:
                return Short.toString(opcode);
            case 4:
                return decodeNextByteFollow(nextByte);
            case 5:
                return decodeNextBytePost(nextByte);
            case 6:
                return decodeNextBytePm(nextByte);
            case 7:
                return decodeNextByteUserlist(nextByte);
            case 8:
                return decodeNextByteStat(nextByte);
        }
    }
    private String decodeNextByteRegisterLogin(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        if (nextByte == '\0') {
            numOfZeros++;
            if (numOfZeros==1) {
                String username = new String(bytes, 0, len, StandardCharsets.UTF_8);
                output += " " + username;
                len = 0;
            }
            else if (numOfZeros==2) {
                String password = new String(bytes, 0, len, StandardCharsets.UTF_8);
                output += " " + password;
                String result = output;
                restart();
                return result;
            }
        }
        else {
            bytes[len++] = nextByte;
        }
        return null;
    }

    private String decodeNextByteFollow(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        if (output.length() == 0) {
            output += nextByte;
        }
        else if (countNum == 0 | countNum == 1){
            numOfUsersArray[countNum] = nextByte;
            countNum++;
        }
        else if (countNum == 2){
            numOfUsers = bytesToShort(numOfUsersArray);
            output += " " + numOfUsers;
        }
        else if (nextByte == '\0'){
            numOfUsers--;
            String username;
            if (numOfUsers == 0) {
                username = new String(bytes, 0, len, StandardCharsets.UTF_8);
                output += " " + username;
                String result = output;
                restart();
                return result;
            }
            else {
                username = new String(bytes, 0, len, StandardCharsets.UTF_8);
                output += " " + username;
                len = 0;
            }
        }
        else {
            bytes[len++] = nextByte;
        }
        return null;
    }
    private String decodeNextBytePost(byte nextByte) {

    }
    private String decodeNextBytePm(byte nextByte) {

    }
    private String decodeNextByteUserlist(byte nextByte) {

    }
    private String decodeNextByteStat(byte nextByte) {

    }

    public byte[] encode(String message) {
        return new byte[5];
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String pop() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        return result;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
}
