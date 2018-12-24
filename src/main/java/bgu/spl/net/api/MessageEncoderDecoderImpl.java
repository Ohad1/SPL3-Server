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

    }


    public String decodeNextByte(byte nextByte) {
        return  null;
    }


    public byte[] encode(String message) {
        String[] split=new String[message.length()];
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
        }
        return null;
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
