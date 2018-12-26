package bgu.spl.net.Assignment3;

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
        System.out.println("Message:" + message);
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
        else if (opNum == 2) { //LOGIN
            String username = splited[1];
            String password = splited[2];
            if (!manager.containsUser(username) ||
                    isLoggedIn ||
                    manager.getUser(username).getPassword()!=password) {
                //error
                connections.send(connectionId, "11 2");
            }
            else {
                manager.addConidName(connectionId,username);
                isLoggedIn = true;
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
            if(isLoggedIn) { // connect
                isLoggedIn=false;
                user.setLoggedin(false);
                manager.removeFromConidName(connectionId);
                connections.send(connectionId,"10 3");// ACK LOGOUT
            }
            else // not connect=ERROR
                connections.send(connectionId,"11 3");
        }
        else if (opNum == 4) { //FOLLOW
            int counter=0;
            if(!isLoggedIn ){ //ERROR
                connections.send(connectionId,"11 4");
            }
            String username=manager.getUserName(connectionId);
            User user=manager.getUser(username);
            String name_fromlist;
            User user_fromlist;
            String f_o=splited[1];
            int num_users_to_follow=Integer.parseInt(splited[2]);

            if(f_o.equals("0")) //FOLLOW
            {
                for (int i = 0; i < num_users_to_follow; i++) {
                    name_fromlist = splited[i];
                    user_fromlist=manager.getUser(name_fromlist);
                    if (!user_fromlist.alreadyInFollowers(username)) {
                        user_fromlist.addFollowers(username);
                        user.incrementFollowing();
                        counter++;
                    }
                }
            }
            if(f_o.equals("1"))//UNFOLLOW
            {
                for (int i = 0; i < num_users_to_follow; i++) {
                    name_fromlist = splited[i];
                    user_fromlist=manager.getUser(name_fromlist);
                    if (user_fromlist.alreadyInFollowers(username)) {
                        //user_fromlist.removeFollowers(username);
                        //user.decrementFollowing();
                        counter++;
                    }
                }
            }
            if(counter==0) {
                connections.send(connectionId,"11 4");
            }
        }
        else if (opNum == 5) {

        }
        else if (opNum == 6) {

        }
        else if (opNum == 7) {

        }
        else if (opNum == 8) {

        }
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
