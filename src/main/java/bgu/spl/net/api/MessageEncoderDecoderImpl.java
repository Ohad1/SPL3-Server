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
        String[] split=message.split("\\s+"); //TODO CHECK
        String type=split[0];
        switch (type) {
            case "10": //ACK
            {
                byte[]type_byte=shortToBytes((short) 10); //ACK
                byte[]opcode_request_byte=shortToBytes(Short.parseShort(split[1]));
                switch (split[1]) {

                  case "4"://follow
                  {
                      return FollowRegisterUserList(split,type_byte,opcode_request_byte);
                  }
                  case "7": //userlist
                  {
                      return FollowRegisterUserList(split,type_byte,opcode_request_byte);
                  }
                  case "8": {
                      return StatRegister(split,type_byte,opcode_request_byte);
                  }

                  default:
                      return DefaultConvert(type_byte,opcode_request_byte);
              }
            }
            case "11": //ERROR
            {
                byte[]type_byte=shortToBytes((short) 11); //ERROR
                byte[]opcode_request_byte=shortToBytes(Short.parseShort(split[1]));
               return DefaultConvert(type_byte,opcode_request_byte);
            }
            case "9": {
                byte[]type_byte=shortToBytes((short) 9); //NOTIFICATION
                return Notification(split,message,type_byte);
            }
        }
        return null;
    }

    private byte[] Notification(String[]split,String line, byte[] type_byte) {

        byte[]pm_post=(split[1]).getBytes();//TODO CHECK CHAR
        byte[]posting_user=(split[2]+"\0").getBytes();
        String s="";
        for(int i=3;i<split.length;i++){
             s=s.concat(split[i]+" ");
        }
        byte[]content=(s+"\0").getBytes();

        byte[]opcode_char=DefaultConvert(type_byte,pm_post);
        byte[]opcode_char_postuser=DefaultConvert(opcode_char,posting_user);
        byte[]fin=DefaultConvert(opcode_char_postuser,content);
        return fin;
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

    private byte[] DefaultConvert(byte[]type_byte, byte[] opcode_request_byte) {
        byte[] fin = new byte[type_byte.length + opcode_request_byte.length];
        System.arraycopy(type_byte, 0, fin, 0, type_byte.length);
        System.arraycopy(opcode_request_byte, 0, fin, type_byte.length, opcode_request_byte.length);
      /*
        for(int i=0;i<type_byte.length;i++){
            fin[i]=type_byte[i];
        }
        for(int j=0;j<opcode_request_byte.length;j++){
            fin[type_byte.length+j]=type_byte[j];
        }
        */
        return fin;
    }

    private byte[] FollowRegisterUserList(String[] split,byte[]type_byte, byte[]opcode_request_byte) {
        int num_users=Integer.parseInt(split[2]);
        byte[]num_users_byte=shortToBytes((short)num_users);
        byte[]names_list= new byte[1024];
        String user_name;
        byte[] username_byte;
        int count=0;
        for(int i=3;i<split.length;i++)
        {
            user_name=split[i];
            username_byte=(user_name+"\0").getBytes();//uses utf8 by default
            for(int j=0;j<username_byte.length;j++){
                if(count+1>names_list.length){
                    names_list = Arrays.copyOf(names_list, len * 2);
                }
                names_list[count]=username_byte[j];
                count++;
            }
        }
        byte[]ack_follow= DefaultConvert(type_byte,opcode_request_byte);
        byte[]ack_follow_num=DefaultConvert(ack_follow,num_users_byte);
        byte[]ack_foolow_num_users=DefaultConvert(ack_follow_num,names_list);
        return ack_foolow_num_users;
    }


    private byte[] StatRegister(String[] split,byte[]type_byte, byte[]opcode_request_byte) {
        int num_post=Integer.parseInt(split[2]);
        byte[]num_post_byte=shortToBytes((short)num_post);
        int num_followers=Integer.parseInt(split[3]);
        byte[]num_followers_byte=shortToBytes((short)num_followers);
        int num_following=Integer.parseInt(split[4]);
        byte[]num_following_byte=shortToBytes((short)num_following);
        byte[]ack_stat=DefaultConvert(type_byte,opcode_request_byte);
        byte[]ack_stat_posts=DefaultConvert(ack_stat,num_post_byte);
        byte[]ack_stat_posts_followers=DefaultConvert(ack_stat_posts,num_followers_byte);
        byte[]fin=DefaultConvert(ack_stat_posts_followers,num_following_byte);
        return  fin;
    }


}
