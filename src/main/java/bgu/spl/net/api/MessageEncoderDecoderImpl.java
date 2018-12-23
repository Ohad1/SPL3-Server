package bgu.spl.net.api;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1024]; //start with 1k TODO CHANGE
    private int len = 0;
    private HashMap<String,Short> types_codeMap;

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


    public String decodeNextByte(byte nextByte) {
        return  null;
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
