package ChatServerPackage;

import StructPackage.Group;
import StructPackage.UserInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

public class SClientThread extends Thread {

    private Socket cs = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private final SClientThread[] threads;
    private final int maxClientsCount;
    private boolean listening = true;

    private String msg;
    private UserInfo userInfo = new UserInfo();

    public SClientThread(Socket cs, SClientThread[] threads) {
        this.cs = cs;
        this.threads = threads;
        this.maxClientsCount = threads.length;
    }

    @Override
    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            out = new PrintWriter(cs.getOutputStream(), true);

            guestJoin();

            //Notifying all users in chat that someone arrived!
            for (int i = 0; i < this.maxClientsCount; i++) {
                if (this.threads[i] != null && this.threads[i] != this) {
                    this.threads[i].out.println("# " + this.userInfo.getUsername() + " joined chat!");
                }
            }

            while (listening) {
                msg = in.readLine().trim();
                if (msg != null) {
                    if (msg.startsWith("/")) {
                        if (msg.equals("/quit") || msg.equals("/logout")) {
                            out.println(msg);
                            msgUserLeft();
                            for (SClientThread t : this.threads) {
                                if (t != null && t.userInfo.isLogged() && t != this) {
                                    t.out.println("/fupdate");
                                }
                            }
                            break;
                        } else if (msg.startsWith("/reg")) {
                            if (!this.userInfo.isLogged()) {
                                String[] regparams = msg.split("\\s+");
                                userRegister(regparams);
                            } else {
                                out.println("# You are already logged in.");
                            }
                        } else if (msg.startsWith("/log")) {
                            if (!this.userInfo.isLogged()) {
                                String[] regparams = msg.split("\\s+");
                                userLogin(regparams);
                            } else {
                                out.println("# You are already logged in.");
                            }
                        } else if (msg.equals("/help")) {
                            out.println("# Chat Commands: ");
                            for (String item : ChatServer.cmds) {
                                out.println(item);
                            }
                        } else if (msg.equals("/users")) {
                            if (this.userInfo.isLogged()) {
                                listAllUsers();
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.equals("/fupdate")) {
                            if (this.userInfo.isLogged()) {
                                for (int i = 0; i < this.maxClientsCount; i++) {
                                    if (this.threads[i] != null && this.threads[i].userInfo.isLogged()) {
                                        this.threads[i].out.println(msg);
                                    }
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.equals("/files")) {
                            if (this.userInfo.isLogged()) {
                                //TODO: listar ficheiros de um utilizador
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.equals("/myfiles")) {
                            if (this.userInfo.isLogged()) {
                                //TODO: listar os meus ficheiros
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.startsWith("/dl")) {
                            if (this.userInfo.isLogged()) {
                                String[] params = msg.split("\\s+");
                                boolean found = false;
                                for (SClientThread t : this.threads) {
                                    if (t != null) {
                                        if (t.userInfo.getUsername().equals(params[1]) && t.userInfo.isLogged()) {
                                            found = true;
                                            t.out.println(msg);
                                            break;
                                        }
                                    }
                                }
                                if (!found) {
                                    out.println("# There's no such user.");
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.startsWith("/gjoin")) {
                            if (this.userInfo.isLogged()) {
                                String[] params = msg.split("\\s+");
                                if (params.length == 2) {
                                    joinGroup(params[1]);
                                } else {
                                    out.println("# Invalid: Use /gjoin <groupname>.");
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.startsWith("/gleave")) {
                            if (this.userInfo.isLogged()) {
                                String[] params = msg.split("\\s+");
                                if (params.length == 2) {
                                    leaveGroup(params[1]);
                                } else {
                                    out.println("# Invalid: Use /gleave <groupname>.");
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.startsWith("/gmsg")) {
                            if (this.userInfo.isLogged()) {
                                String[] params = msg.split("\\s+", 3);
                                if (params.length == 3 && params[0].equals("/gmsg")) {
                                    sendMsgToGroup(params[1], params[2]);
                                } else {
                                    out.println("# Invalid: Use /gmsg <groupname> <msg>.");
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else if (msg.startsWith("/gusers")) {
                            if (this.userInfo.isLogged()) {
                                String[] params = msg.split("\\s+", 2);
                                if (params.length == 2 && params[0].equals("/gusers")) {
                                    listGroupUsers(params[1]);
                                } else {
                                    out.println("# Invalid: Use /gusers <groupname>.");
                                }
                            } else {
                                out.println("# Guests don't have access to this command.");
                            }
                        } else {
                            out.println("# CMD Unknown! Use /help.");
                        }
                    } else {// If not CMD
                        for (int i = 0; i < this.maxClientsCount; i++) {
                            if (this.threads[i] != null && this.threads[i] != this) {
                                this.threads[i].out.println("<" + this.userInfo.getUsername() + ">: " + msg);
                            }
                        }
                    }
                }
            }
            //Clear position in this.threads to free space for another connection.
            for (int i = 0; i < this.maxClientsCount; i++) {
                if (this.threads[i] == this) {
                    this.threads[i] = null;
                }
            }

            out.close();
            in.close();
            cs.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //List all connected users.
    private void listAllUsers() {
        out.println("# Users list:");
        for (SClientThread t : this.threads) {
            if (t != null && t != this) {
                out.println("# " + t.userInfo.getUsername());
            }
        }
    }

    //Informs users that a guest is now registered.
    private void msgGuestSignedUp(String guestName) {
        for (int i = 0; i < this.maxClientsCount; i++) {
            if (this.threads[i] != null && this.threads[i] != this) {
                this.threads[i].out.println("# " + guestName + " is now " + this.userInfo.getUsername() + "!");
            }
        }
    }

    private void msgUserLeft() {
        for (int i = 0; i < this.maxClientsCount; i++) {
            if (this.threads[i] != null && this.threads[i] != this) {
                this.threads[i].out.println("# " + this.userInfo.getUsername() + " left!");
            }
        }
    }

    //Informs users that a guest is now registered.
    private void msgGuestSignedIn(String guestName) {
        for (int i = 0; i < this.maxClientsCount; i++) {
            if (this.threads[i] != null && this.threads[i] != this) {
                this.threads[i].out.println("# " + guestName + " logged in as " + this.userInfo.getUsername() + "!");
            }
        }
    }

    //Increments Guest count.
    private synchronized void guestJoin() {
        //Assigning Guest name and incrementing Guest count.
        this.userInfo.setUsername("guest" + ChatServer.guestCount);
        out.println("# Welcome " + this.userInfo.getUsername() + ".");
        ChatServer.guestCount++;
        if (ChatServer.guestCount == Integer.MAX_VALUE) {
            ChatServer.guestCount = 1;
        }
    }

    //User signup.
    private synchronized void userRegister(String[] params) throws IOException {
        if (params.length != 4) {
            out.println("# [Reg] Wrong format used!");
            out.println("# [Reg] Ex: /reg username password password");
        } else if (validateRegister(params)) {
            boolean found = false;
            for (UserInfo u : ChatServer.userDB) {
                if (u.getUsername().equals(params[1])) {
                    found = true;
                    out.println("# Username already exists.");
                    return;
                }
            }
            if (!found) {
                UserInfo newUser = new UserInfo(params[1], params[2], true, ChatServer.chatDirectory);
                ChatServer.userDB.add(newUser);
                ChatServer.saveToFile();
                out.println("# [Reg] Account created successfully!");
                out.println("# [Reg] Logging in...");
                String guestName = this.userInfo.getUsername();
                this.userInfo = newUser;
                out.println("# [INTERNAL] Logged in.");
                out.println("# Welcome " + this.userInfo.getUsername() + ".");
                msgGuestSignedUp(guestName);
            }
        }
    }

    //Validates information for user signup.
    private boolean validateRegister(String[] params) {
        String pattern = "^[a-zA-Z0-9]*$";
        for (int i = 1; i < params.length; i++) {
            if (!params[i].matches(pattern)) {
                out.println("# [Reg] Username and password can only contain AlphaNumeric characters!");
                return false;
            }
        }
        if (!params[2].equals(params[3])) {
            out.println("# [Reg] Passwords must match!");
            return false;
        }
        return true;
    }

    //User login.
    private void userLogin(String[] params) {
        if (params.length != 3) {
            out.println("# [Log] Wrong format used!");
            out.println("# [Log] Ex: /log username password");
        } else {
            //Verify if user is logged in
            boolean found = false;
            for (SClientThread t : this.threads) {
                if (t != null && t.userInfo.getUsername().equals(params[1])) {
                    out.println("# [Log] User already logged in.");
                    found = true;
                    break;
                }
            }
            if (!found) {
                UserInfo tmp;
                if ((tmp = validateLogin(params)) != null) {
                    String guestName = this.userInfo.getUsername();
                    this.userInfo = tmp;
                    out.println("# [Log] Logging in...");
                    out.println("# [INTERNAL] Logged in.");
                    out.println("# Welcome " + this.userInfo.getUsername() + ".");
                    msgGuestSignedIn(guestName);
                }
            }
        }
    }

    //Validates information for user login.
    private UserInfo validateLogin(String[] params) {
        Iterator<UserInfo> it = ChatServer.userDB.iterator();
        while (it.hasNext()) {
            UserInfo user = it.next();
            if (user.getUsername().equals(params[1])) {
                if (user.getPassword().equals(params[2])) {
                    return user;
                } else {
                    out.println("# [Log] Wrong password!");
                    return null;
                }
            }
        }
        out.println("# [Log] That username doesn't exist!");
        return null;
    }

    private synchronized void joinGroup(String groupName) {
        boolean found = false;
        for (Group g : ChatServer.groups) {
            if (g.getName().equals(groupName)) {
                found = true;
                if(!g.getClients().contains(this.userInfo.getUsername())) {
                    g.add(this.userInfo.getUsername());
                    out.println("# Joined group " + groupName);
                } else {
                    out.println("# You are already in that group.");
                }
                break;
            }
        }
        if (!found) {
            ChatServer.createGroup(groupName, this.userInfo.getUsername());
            out.println("# Creating group");
        }
    }

    private synchronized void leaveGroup(String groupName) {
        boolean found = false;
        for (Group g : ChatServer.groups) {
            if (g.getName().equals(groupName)) {
                found = true;
                if(g.remove(this.userInfo.getUsername())) {
                    out.println("# Left group " + groupName);
                } else {
                    out.println("# You are not in that group.");
                }
                if (g.getClientsSize() == 0) {
                    ChatServer.deleteGroup(g);
                }
                break;
            }
        }
        if (!found) {
            out.println("# Group doens't exist.");
        }
    }

    private void sendMsgToGroup(String groupName, String msg) {
        boolean found = false;
        for (Group g : ChatServer.groups) {
            if (g.getName().equals(groupName)) {
                found = true;
                for (String client : g.getClients()) {
                    for (SClientThread t : this.threads) {
                        if (t != null && t != this && t.userInfo.getUsername().equals(client)) {
                            t.out.println("<" + groupName + "|" + this.userInfo.getUsername() + ">: " + msg);
                        }
                    }
                }
            }
        }
        if (!found) {
            out.println("# Group not found.");
        }
    }
    
    private void listGroupUsers(String groupName) {
        boolean found = false;
        for(Group g : ChatServer.groups) {
            if(g.getName().equals(groupName)) {
                found = true;
                out.println("# Users in group '" + groupName + "':");
                for(String s : g.getClients()) {
                    out.println("# " + s);
                }
                break;
            }
        }
        if(!found)
            out.println("# Group not found.");
    }

}
