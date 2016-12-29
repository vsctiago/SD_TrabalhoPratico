package ChatServerPackage;

import StructPackage.UserInfo;
import java.io.BufferedReader;
import java.io.File;
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
    private final int maxClientCount;
    private boolean listening = true;

    private String msg;
    private UserInfo userInfo = new UserInfo();

    public SClientThread(Socket cs, SClientThread[] threads) {
        this.cs = cs;
        this.threads = threads;
        this.maxClientCount = threads.length;
    }

    @Override
    public void run() {
        int maxClientsCount = this.maxClientCount;
        SClientThread[] threads = this.threads;

        try {
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            out = new PrintWriter(cs.getOutputStream(), true);

            //Assigning Guest name and incrementing Guest count.
            this.userInfo.setUsername("guest" + ChatServer.guestCount);
            out.println("# Welcome " + this.userInfo.getUsername() + ".");
            incGuestCount();

            //Notifying all users in chat that someone arrived!
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].out.println("# " + this.userInfo.getUsername() + " joined chat!");
                }
            }

            while (listening) {
                msg = in.readLine().trim();
                if (msg != null) {
                    if (msg.equals("/quit") || msg.equals("/logout")) {
                        out.println(msg);
                        break;
                    } else if (msg.startsWith("/reg")) {
                        String[] regparams = msg.split("\\s+");
                        userRegister(regparams);
                    } else if (msg.startsWith("/log")) {
                        String[] regparams = msg.split("\\s+");
                        userLogin(regparams);
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
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i].userInfo.isLogged()) {
                                    threads[i].out.println(msg);
                                }
                            }
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else if (msg.startsWith("/files")) {
                        if (this.userInfo.isLogged()) {
                            //TODO: listar ficheiros de um utilizador
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else if (msg.startsWith("/myfiles")) {
                        if (this.userInfo.isLogged()) {
                            //TODO: listar os meus ficheiros
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else if (msg.startsWith("/dl")) {
                        if (this.userInfo.isLogged()) {
                            String[] params = msg.split("\\s+");
                            for (SClientThread t : threads) {
                                if (t.userInfo.getUsername().equals(params[1])) {
                                    t.out.println(msg);
                                    break;
                                }
                            }
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i] != this) {
                                threads[i].out.println("<" + this.userInfo.getUsername() + ">: " + msg);
                            }
                        }
                    }
                }
            }
            //Clear position in threads to free space for another connection.
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
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
        for (SClientThread t : threads) {
            if (t != null && t != this) {
                out.println("# " + t.userInfo.getUsername());
            }
        }
    }

    //Informs users that a guest is now registered.
    private void msgGuestSignedUp(String guestName) {
        for (int i = 0; i < this.maxClientCount; i++) {
            if (threads[i] != null && threads[i] != this) {
                threads[i].out.println("# " + guestName + " is now " + this.userInfo.getUsername() + "!");
            }
        }
    }

    //Informs users that a guest is now registered.
    private void msgGuestSignedIn(String guestName) {
        for (int i = 0; i < this.maxClientCount; i++) {
            if (threads[i] != null && threads[i] != this) {
                threads[i].out.println("# " + guestName + " logged in as " + this.userInfo.getUsername() + "!");
            }
        }
    }

    //Increments Guest count.
    private synchronized void incGuestCount() {
        ChatServer.guestCount++;
        if (ChatServer.guestCount == Integer.MAX_VALUE) {
            ChatServer.guestCount = 1;
        }
    }

    //User signup.
    synchronized void userRegister(String[] params) throws IOException {
        if (params.length != 4) {
            out.println("# [Reg] Wrong format used!");
            out.println("# [Reg] Ex: /reg username password password");
        } else if (validateRegister(params)) {
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
    void userLogin(String[] params) { //Tirei o private
        if (params.length != 3) {
            out.println("# [Log] Wrong format used!");
            out.println("# [Log] Ex: /log username password");
        } else {
            //Verify if user is logged in
            boolean found = false;
            for (SClientThread t : threads) {
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

}
