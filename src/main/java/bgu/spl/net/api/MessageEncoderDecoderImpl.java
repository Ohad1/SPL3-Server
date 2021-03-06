package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1024];
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
        countNum = 0;
    }

    public String decodeNextByte(byte nextByte) {
        if (opcodeCount==2) {
            switch (opcode) {
                case 1:
                case 2:
                case 6:
                    return decodeNextByteRegisterLoginAndPm(nextByte);
                case 4:
                    return decodeNextByteFollow(nextByte);
                case 5:
                case 8:
                    return decodeNextBytePostStat(nextByte);
            }
        }
        else if (opcodeCount == 0) {
            opcodeArray[opcodeCount] = nextByte;
            opcodeCount++;
            return null;
        }
        else if (opcodeCount == 1){
            opcodeArray[opcodeCount] = nextByte;
            opcode = bytesToShort(opcodeArray);
            output += opcode;
            opcodeCount = 2;
            if (opcode == 3 | opcode == 7) {
                String output = Short.toString(opcode);
                restart();
                return output;
            }
        }

        return null;
    }

    private String decodeNextByteRegisterLoginAndPm(byte nextByte) {
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
        if (output.length() == 1) {
            output += " " + nextByte;
        }
        else if (countNum == 0){
            numOfUsersArray[countNum] = nextByte;
            countNum++;
        }
        else if (countNum == 1){
            numOfUsersArray[countNum] = nextByte;
            numOfUsers = bytesToShort(numOfUsersArray);
            output += " " + numOfUsers;
            countNum++;
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
    private String decodeNextBytePostStat(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        if (nextByte == '\0') {
            String content = new String(bytes, 0, len, StandardCharsets.UTF_8);
            output += " " + content;
            String result = output;
            restart();
            return result;
        }
        bytes[len++] = nextByte;
        return null;
    }


    public byte[] encode(String message) {
        String[] split = message.split(" ");
        String type=split[0];
        switch (type) {
            case "10": //ACK
            {
                byte[]type_byte=shortToBytes((short) 10); //ACK
                byte[]opcode_request_byte=shortToBytes(Short.parseShort(split[1]));
                switch (split[1]) {

                  case "4"://follow
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
        byte[]pm_post = new byte[1];
        if (split[1].equals("0")) {
            pm_post[0] = '\0';
        }
        else {
            pm_post[0] = '\1';
        }
        byte[]posting_user=(split[2]+'\0').getBytes();
        String s="";
        for(int i=3;i<split.length;i++){
            s=s.concat(split[i]+" ");
        }
        s=s.substring(0,s.length()-1);
        byte[]content=(s+'\0').getBytes();

        byte[]opcode_char=DefaultConvert(type_byte,pm_post);
        byte[]opcode_char_postuser=DefaultConvert(opcode_char,posting_user);
        return DefaultConvert(opcode_char_postuser,content);
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
        return new String(bytes, 0, len, StandardCharsets.UTF_8);
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
        return fin;
    }

    private byte[] FollowRegisterUserList(String[] split,byte[]type_byte, byte[]opcode_request_byte) {
        int num_users=Integer.parseInt(split[2]);
        byte[]num_users_byte=shortToBytes((short)num_users);
        ArrayList<Byte> names_list= new ArrayList<>();
        names_list.add(type_byte[0]);
        names_list.add(type_byte[1]);
        names_list.add(opcode_request_byte[0]);
        names_list.add(opcode_request_byte[1]);
        names_list.add(num_users_byte[0]);
        names_list.add(num_users_byte[1]);

        String user_name;
        Byte zeroObject = (byte) '\0';
        byte[] username_byte;
        for(int i=3;i<split.length;i++)
        {
            user_name=split[i];
            username_byte=(user_name).getBytes();//uses utf8 by default
            for(int j=0;j<username_byte.length;j++){
                names_list.add(username_byte[j]);
            }
            names_list.add(zeroObject);
        }
        int size = names_list.size();
        byte[] output = new byte[size];
        for (int i = 0; i < size; i++) {
            output[i] = names_list.get(i);
        }
        return output;
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
        return DefaultConvert(ack_stat_posts_followers,num_following_byte);
    }


}
