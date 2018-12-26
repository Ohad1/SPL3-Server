package bgu.spl.net.Assignment3;

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
            if (manager.containsUser(username) | isLoggedIn) {
                //error
                connections.send(connectionId, "11 1");
            }
            else {
                //success
                manager.addUserToMap(username, password);
                connections.send(connectionId, "10 1");
            }
        }
        else if (opNum == 2) {
            String username = splited[1];
            String password = splited[2];
            System.out.println(manager.getUser(username).getPassword());
            System.out.println(password);
            System.out.println(!manager.containsUser(username));
            System.out.println(isLoggedIn);
            System.out.println(!manager.getUser(username).getPassword().equals(password));
            if (!manager.containsUser(username) ||
                    isLoggedIn ||
                    !manager.getUser(username).getPassword().equals(password)) {
                connections.send(connectionId, "11 1");
            }
            else {
                isLoggedIn = true;
                User user = manager.getUser(username);
                user.setLoggedin(true);
                connections.send(connectionId, "10 1");
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
        else if (opNum == 3) {

        }
        else if (opNum == 4) {

        }
        else if (opNum == 5) {

        }
        else if (opNum == 6) {

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
