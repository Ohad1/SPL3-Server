package bgu.spl.net.api;

import bgu.spl.net.Assignment3.Manager;
import bgu.spl.net.Assignment3.User;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

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
        String[] splitted = message.split(" ");
        int opNum = Integer.parseInt(splitted[0]);
        if (opNum == 1) { //REGISTER
            String username = splitted[1];
            String password = splitted[2];
            String isConnected = manager.getNameFromConId(connectionId);
            // connid is already loggedin
            if (isConnected!=null) {
                //error
                connections.send(connectionId, "11 1");
                return;
            }
            // prevent login to the same user from two computers
            Boolean isPut = manager.putIfAbsent(username, password);
            // fail
            if (!isPut) {
                connections.send(connectionId, "11 1");
            }
            else {
                //success
                connections.send(connectionId, "10 1");
            }
        } else if (opNum == 2) { //LOGIN
            String username = splitted[1];
            String password = splitted[2];
            if (!manager.containsUser(username) ) {
                connections.send(connectionId, "11 2");
            }
            else {
                User user = manager.getUser(username);
                synchronized (user.getLoggedIn()) {
                    if (user.getLoggedIn() || !user.getPassword().equals(password)) {
                        connections.send(connectionId, "11 2");
                    } else {
                        manager.addConnection(connectionId, username);
                        user.setLoggedIn(true);
                        user.setConnId(connectionId);
                        connections.send(connectionId, "10 2");
                        for (String mess : user.getUnreadMessages()) {
                            connections.send(connectionId, mess);
                        }
                        user.getUnreadMessages().clear();
                    }
                }
            }
        } else if (opNum == 3) {//LOGOUT
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 3");
            } else {
                User user = manager.getUser(username);
                synchronized (user.getLoggedIn()) {
                    if (user.getLoggedIn()) { // connect
                        user.setLoggedIn(false);
                        manager.removeConnection(connectionId);
                        boolean sent = connections.send(connectionId, "10 3");// ACK LOGOUT
                        if (sent) {
                            connections.disconnect(connectionId);
                            shouldTerminate = true;
                        }
                    } else // not connect=ERROR
                        connections.send(connectionId, "11 3");
                }
            }
        } else if (opNum == 4) { //FOLLOW
            int counter = 0;
            LinkedList<String> names_success = new LinkedList<>();
            StringBuilder output;
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 4");
            } else {
                User user = manager.getUser(username);
                String name_fromlist;
                User user_fromlist;
                String f_o = splitted[1];
                int num_users_to_follow = Integer.parseInt(splitted[2]);
                if (f_o.equals("0")) //FOLLOW
                {
                    for (int i = 0; i < num_users_to_follow; i++) {
                        name_fromlist = splitted[i+3];
                        user_fromlist = manager.getUser(name_fromlist);
                        if(user_fromlist!=null) {
                            if (!user_fromlist.alreadyInFollowers(username)) {
                                user_fromlist.addFollower(username);
                                user.incrementFollowing();
                                counter++;
                                names_success.addLast(name_fromlist);
                            }
                        }
                    }
                }
                if (f_o.equals("1"))//UNFOLLOW
                {
                    for (int i = 0; i < num_users_to_follow; i++) {
                        name_fromlist = splitted[i+3];
                        user_fromlist = manager.getUser(name_fromlist);
                        if (user_fromlist.alreadyInFollowers(username)) {
                            user_fromlist.removeFollower(username);
                            user.decrementFollowing();
                            counter++;
                            names_success.addLast(name_fromlist);
                        }
                    }
                }
                if (counter == 0) {
                    connections.send(connectionId, "11 4");
                } else {
                    output = new StringBuilder("10 4 " + counter);
                    for (int i = 0; i < counter; i++) {
                        output.append(" ").append(names_success.removeFirst());
                    }
                    int size = output.length();
                    connections.send(connectionId, output.toString());
                }
            }
        } else if (opNum == 5) { //POST
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 5");
            }
            else {
                String content = message.substring(2);
                String[] splitedContent = content.split( " ");
                LinkedList<String> tagged = new LinkedList<>();
                for (String string : splitedContent) {
                    if (string.contains("@")) {
                        System.out.println(string.indexOf('@'));
                        System.out.println(string.length());
                        if (string.indexOf('@') < string.length()-1) {
                            tagged.add(string.substring(string.indexOf('@')+1));
                        }
                    }
                }
                User sendingUser = manager.getUser(username);
                LinkedList<String> followers = new LinkedList<>(sendingUser.getFollowers());
                LinkedList<String> all = new LinkedList<>();
                all.addAll(tagged);
                all.addAll(followers);
                LinkedList<String> result = new LinkedList(new LinkedHashSet(all));
                for (String receiver : result) {
                    User receiverUser = manager.getUser(receiver);
                    String output = "9 1 " + username + " " + content;
                    synchronized (receiverUser.getLoggedIn()) {
                        boolean isSent = connections.send(receiverUser.getConnId(), output);
                        if (!isSent) {
                            receiverUser.addUnreadMessage(output);
                        }
                    }
                }
                sendingUser.addPost(content);
                connections.send(connectionId, "10 5");
            }
        }
        else if (opNum == 6) { //PM
            String name = splitted[1];
            String user_name = manager.getUserName(connectionId);
            if (user_name != null) {
                User user = manager.getUser(user_name);
                if (user.getLoggedIn()) {
                    StringBuilder content = new StringBuilder();
                    String output;
                    for (int i = 2; i < splitted.length; i++) {
                        content.append(splitted[i]).append(" ");
                    }
                    content = new StringBuilder(content.substring(0, content.length() - 1));
                    User user_to_send = manager.getUser(name);
                    if (user_to_send != null) {
                        if (user_to_send.getLoggedIn()) // login
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
            String username = manager.getUserName(connectionId);
            if (username==null) { //ERROR
                connections.send(connectionId, "11 7");
            } else {
                StringBuilder output = new StringBuilder("10 7 ");
                List<String> registeredUsers = manager.getRegisteredUsers();
                output.append(registeredUsers.size()).append(" ");
                for (String user : registeredUsers) {
                    output.append(user).append(" ");
                }
                output = new StringBuilder(output.substring(0, output.length() - 1));
                connections.send(connectionId, output.toString());
            }
        } else if (opNum == 8) {
            String connectedUser = manager.getUserName(connectionId);
            if (connectedUser==null) { //ERROR
                connections.send(connectionId, "11 8");
            } else {
                String output = "10 8 ";
                String username = splitted[1];
                User user = manager.getUser(username);
                if(user==null) // no user with this name
                {
                    connections.send(connectionId, "11 8");
                }
                else{
                    output += user.getNumOfPosts() + " " + user.getNumOfFollowers() + " " + user.getNumOfFollowing();
                    connections.send(connectionId, output);
                }

            }
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
