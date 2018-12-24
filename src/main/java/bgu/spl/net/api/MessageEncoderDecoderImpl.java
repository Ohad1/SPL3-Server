package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.stream.Stream;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1024]; //start with 1k TODO CHANGE
    private int len = 0;
    private int opcodeCount = 0;
    private String output = "";
    private short opcode;
    private byte[] opcodeArray = new byte[2];

    private int numOfZeros = 0;
    private HashMap<String,Short> types_codeMap;

    private byte[] numOfUsersArray = new byte[2];
    private int countNum = 0;
    private short numOfUsers;

    public MessageEncoderDecoderImpl(byte[] bytes, int len) {
        this.types_codeMap=new HashMap<>();
        types_codeMap.put("REGISTER",(short)1);
        types_codeMap.put("LOGIN",(short)2);
        types_codeMap.put("LOGOUT",(short)3);
        types_codeMap.put("FOLLOW",(short)4);
        types_codeMap.put("POST",(short)5);
        types_codeMap.put("PM",(short)6);
        types_codeMap.put("USERLIST",(short)7);
        types_codeMap.put("STAT",(short)8);
        types_codeMap.put("NOTIFICATION",(short)9);
        types_codeMap.put("ACK",(short)10);
        types_codeMap.put("ERROR",(short)11);
    }



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
        return null;
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
        return "";
    }
    private String decodeNextBytePm(byte nextByte) {
        return "";
    }
    private String decodeNextByteUserlist(byte nextByte) {
        return "";
    }
    private String decodeNextByteStat(byte nextByte) {
        return "";
    }

    public byte[] encode(String message) {
        String[] split=new String[1024];
        String type=split[0];
        switch (type) {
            case "ACK":
            {

              switch (split[1]) {


                  case "4"://follow
                  {
                      return FollowRegister(split);
                  }
                  case "7": //userlist
                  {
                      return UserList(split);
                  }
                  case "8": {
                      return StatRegister(split);
                  }
                  default:
                      return DefaultConvert(split);
              }
            }
            case "ERROR":
            {
                System.out.println("Statement 2 ");
                break;
            }
            default:
                System.out.println("You entered wrong value");
        }
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
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private byte[] DefaultConvert(String[] split) {
        String type=split[0];
        Short opcode=types_codeMap.get(split[0]);
        byte[]opcode_byte=shortToBytes(opcode);
        byte[]type_opcode=(type + "\n").getBytes();
        int length=type_opcode.length+opcode_byte.length;
        byte[] fin= new byte[length];

        for(int i=0;i<type_opcode.length;i++){
            fin[i]=type_opcode[i];
        }
        for(int j=0;j<opcode_byte.length;j++){
            fin[type_opcode.length+j]=type_opcode[j];
        }
        return fin;
    }

    private byte[] FollowRegister(String[] split) {

        return null;
    }

    private byte[] UserList(String[] split) {
        return null;
    }

    private byte[] StatRegister(String[] split) {
        return null;
    }


}
