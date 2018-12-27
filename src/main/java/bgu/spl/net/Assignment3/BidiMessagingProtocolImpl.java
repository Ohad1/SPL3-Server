package bgu.spl.net.Assignment3;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String> {

    private int connectionId;
    private Connections connections;
    private Manager manager;
    private boolean shouldTerminate = false;

    public BidiMessagingProtocolImpl(Manager manager) {
        this.manager = manager;
    }

    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    public void process(String message) {
        System.out.println("Message: " + message);
        System.out.println("My id: " + connectionId);
        String[] splited = message.split(" ");
        int opNum = Integer.parseInt(splited[0]);
        if (opNum == 1) {
            String username = splited[1];
            String password = splited[2];
            if (manager.containsUser(username)) {
                //error
                connections.send(connectionId, "11 1");
            }
            else if (manager.containsUser(username) && manager.getUser(username).getLoggedin()) {
                connections.send(connectionId, "11 1");
            }
            else {
                //success
                manager.addUserToMap(username, password);
                connections.send(connectionId, "10 1");
            }
        } else if (opNum == 2) { //LOGIN
            String username = splited[1];
            String password = splited[2];
            if (!manager.containsUser(username) ||
                    manager.getUser(username).getLoggedin() ||
                    !manager.getUser(username).getPassword().equals(password)) {
                connections.send(connectionId, "11 2");
            } else {
                manager.addConidName(connectionId, username);
                User user = manager.getUser(username);
                user.setLoggedin(true);
                user.setConnId(connectionId);
                connections.send(connectionId, "10 2");
                for (String mess : user.getUnreadMessages()) {
                    connections.send(connectionId, mess);
                }
            }
        } else if (opNum == 3) {//LOGOUT
            String username = manager.getUserName(connectionId);
            User user = manager.getUser(username);
            if (user.getLoggedin()) { // connect
                user.setLoggedin(false);
                manager.removeFromConidName(connectionId);
                connections.send(connectionId, "10 3");// ACK LOGOUT
            } else // not connect=ERROR
                connections.send(connectionId, "11 3");
        } else if (opNum == 4) { //FOLLOW
            int counter = 0;
            List<String> names_success = new LinkedList<>();
            String output;
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 4");
            } else {
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
                        if (!user_fromlist.alreadyInFollowers(username)) {
                            user_fromlist.addFollower(username);
                            user.incrementFollowing();
                            counter++;
                            ((LinkedList<String>) names_success).addLast(name_fromlist);
                        }
                    }
                }
                if (f_o.equals("1"))//UNFOLLOW
                {
                    for (int i = 0; i < num_users_to_follow; i++) {
                        name_fromlist = splited[i];
                        user_fromlist = manager.getUser(name_fromlist);
                        if (user_fromlist.alreadyInFollowers(username)) {
                            user_fromlist.removeFollower(username);
                            user.decrementFollowing();
                            counter++;
                            ((LinkedList<String>) names_success).addLast(name_fromlist);
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
        } else if (opNum == 5) {
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 4");
            }
            else {
                String content = splited[1];
                String[] splitedContent = content.split( " ");
                LinkedList<String> tagged = new LinkedList<>();
                for (String string : splitedContent) {
                    if (string.charAt(0) == '@') {
                        tagged.add(string.substring(1));
                    }
                }
                User sendingUser = manager.getUser(username);
                LinkedList<String> followers = sendingUser.getFollowers();
                LinkedList<String> all = new LinkedList<>();
                all.addAll(tagged);
                all.addAll(followers);
                LinkedList<String> result = new LinkedList(new LinkedHashSet(all));
                for (String reciever : result) {
                    User recieverUser = manager.getUser(reciever);
                    String output = "9 1 " + username + " " + content;
                    Boolean isSent = connections.send(recieverUser.getConnId(), output);
                    if (!isSent) {
                        recieverUser.addUnreadMessage(output);
                    }
                }
                sendingUser.addPost(content);
                connections.send(connectionId, "10 5");
            }
        }
        else if (opNum == 6) { //PM
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
                            id= user_to_send.getConnId();
                            if(connections.send(id, "9 0 " + user_name + " " + content))
                            {
                                connections.send(connectionId,"10 6");
                            }
                            else{ //send failed
                                user_to_send.addUnreadMessage("9 0 " + user_name + " " + content); //add to unread
                                connections.send(connectionId,"10 6"); //TODO CHECK
                            }
                            user.addPrivateMessage(user_name+" "+content); //TODO CHECK
                        } else // LOGOUT
                        {
                            output = "9 0 " + user_name + " " + content;
                            user.addPrivateMessage(user_name + " " + content);
                            user_to_send.addUnreadMessage(output);
                            connections.send(connectionId,"10 6");//TODO CHECK
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
            output = output.substring(0, output.length() - 2);
            connections.send(connectionId, output);
        } else if (opNum == 8) {
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
