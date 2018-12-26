package bgu.spl.net.Assignment3;

import javax.jws.soap.SOAPBinding;
import java.util.LinkedList;
import java.util.List;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String> {

    private  int connectionId;
    private Connections connections;
    private Manager manager;
    private boolean shouldTerminate = false;
    private boolean isLoggedIn;

    public BidiMessagingProtocolImpl(Manager manager) {
        this.manager = manager;
    }

    public void start(int connectionId, Connections<String> connections) {
        this.connectionId =connectionId;
        this.connections=connections;
        this.isLoggedIn = false;

    }

    public void process(String message) {
        System.out.println("Message: " + message);
        String[] splited = message.split(" ");
        int opNum = Integer.parseInt(splited[0]);
        if (opNum == 1) {
            String username = splited[1];
            String password = splited[2];
            if (manager.containsUser(username) || manager.getUser(username).getLoggedin()) {
                //error
                connections.send(connectionId, "11 1");
            }
            else {
                //success
                manager.addUserToMap(username, password);
                connections.send(connectionId, "10 1");
            }
        }
        else if (opNum == 2) { //LOGIN
            String username = splited[1];
            String password = splited[2];
            if (!manager.containsUser(username) ||
                    manager.getUser(username).getLoggedin() ||
                    !manager.getUser(username).getPassword().equals(password)) {
                connections.send(connectionId, "11 1");
            }
            else {
                manager.addConidName(connectionId,username);
                User user = manager.getUser(username);
                user.setLoggedin(true);
                connections.send(connectionId, "10 2");
                for (Message mess : user.getUnreadMessages()) {
                    if (mess instanceof Post) {
                        connections.send(connectionId, "9 1 " + mess.getSender() + " " + mess.getContent());
                    }
                    else {
                        connections.send(connectionId, "9 0 " + mess.getSender() + " " + mess.getContent());
                    }
                }
            }
        }
        else if (opNum == 3) {//LOGOUT
            String username=manager.getUserName(connectionId);
            User user=manager.getUser(username);
            if(user.getLoggedin()) { // connect
                user.setLoggedin(false);
                manager.removeFromConidName(connectionId);
                connections.send(connectionId,"10 3");// ACK LOGOUT
            }
            else // not connect=ERROR
                connections.send(connectionId,"11 3");
        }


        else if (opNum == 4) { //FOLLOW
            int counter = 0;
            List<String> names_success = new LinkedList<>();
            String output;
            if (!isLoggedIn) { //ERROR
                connections.send(connectionId, "11 4");
            }
            else{
            String username = manager.getUserName(connectionId);
            User user = manager.getUser(username);
            String name_fromlist;
            User user_fromlist;
            String f_o = splited[1];
            int num_users_to_follow = Integer.parseInt(splited[2]);

            if (f_o.equals("0")) //FOLLOW
            {
                for (int i = 0; i < num_users_to_follow; i++) {
                    name_fromlist = splited[i];
                    user_fromlist = manager.getUser(name_fromlist);
                    if(user_fromlist!=null) {
                        if (!user_fromlist.alreadyInFollowers(username)) {
                            user_fromlist.addFollower(username);
                            user.incrementFollowing();
                            counter++;
                            ((LinkedList<String>) names_success).addLast(name_fromlist);
                        }
                    }
                }
            }
            if (f_o.equals("1"))//UNFOLLOW
            {
                for (int i = 0; i < num_users_to_follow; i++) {
                    name_fromlist = splited[i];
                    user_fromlist = manager.getUser(name_fromlist);
                    if(user_fromlist!=null) {
                        if (user_fromlist.alreadyInFollowers(username)) {
                            user_fromlist.removeFollower(username);
                            user.decrementFollowing();
                            counter++;
                            ((LinkedList<String>) names_success).addLast(name_fromlist);
                        }
                    }
                }
            }
            if (counter == 0) {
                connections.send(connectionId, "11 4");
            } else {
                output = "10 4 " + counter;
                for (int i = 0; i < counter; i++) {
                    output += ((LinkedList<String>) names_success).removeFirst();
                    output += " ";
                    int size = output.length();
                    output = output.substring(0, size - 1);
                    connections.send(connectionId, output);
                }
            }
        }
        }
        else if (opNum == 5) {

        }
        else if (opNum == 6) {
            String name = splited[1];
            String user_name = manager.getUserName(connectionId);
            if (user_name != null) {
                User user = manager.getUser(user_name);
                if (user.getLoggedin()) {
                    String content = "";
                    String output = "";
                    for (int i = 2; i < splited.length; i++) {
                        content += splited[i];
                    }
                    User user_to_send = manager.getUser(name);
                    if (user_to_send != null) {
                        if (user_to_send.getLoggedin()) // login
                        {
                            int id;
                             id= user. getId();
                           if(connections.send(id, output))
                               user.addPrivateMessage(user_name+" "+content);
                           else{
                                user_to_send.addUnRead("9 0 " + user_name + " " + content); //add to unread
                           }
                            user.addPrivateMessage(output);
                        } else // LOGOUT
                        {
                            output = "9 0 " + user_name + " " + content;
                            user.addPrivateMessage(user_name + " " + content);
                            user_to_send.addUnRead(output);
                        }
                    }
                    else //user_to_send not register
                    {
                        connections.send(connectionId, "11 6");//error
                    }
                }
                else // user not logged in
                    connections.send(connectionId, "11 6");//error

            }
            else // user not registered
            {
                connections.send(connectionId, "11 6");//error
            }
        }
        else if (opNum == 7) {
            String output = "10 7 ";
            List<String> registeredUsers = manager.getRegisteredUsers();
            output += registeredUsers.size() + " ";
            for (String user : registeredUsers) {
                output += user + " ";
            }
            output = output.substring(0, output.length()-2);
            connections.send(connectionId, output);
        }
        else if (opNum == 8) {
            String output = "10 8 ";
            String username = splited[1];
            User user = manager.getUser(username);
            output += user.getNumOfPosts() + " " + user.getNumOfFollowers() + " " + user.getNumOfFollowing();
            connections.send(connectionId, output);
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
