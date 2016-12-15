package ChatPackage;

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
    private UserInfo myInfo = new UserInfo();

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
            this.myInfo.setUsername("guest"+ChatServer.guestCount);
            //TODO: Isto não pode estar aqui. Server não acede a variaveis do lado do Client
            Client.userinfo = this.myInfo;
            out.println("# Welcome " + this.myInfo.getUsername() + ".");
            incGuestCount();
            
            //Notifying all users in chat that someone arrived!
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].out.println("# " + this.myInfo.getUsername() + " joined chat!");
                }
            }

            while (listening) {
                msg = in.readLine().trim();
                if (msg != null) {
                    if (msg.startsWith("/quit") || msg.startsWith("/logout")) {
                        break;
                    } else if(msg.startsWith("/reg")) {
                        String[] regparams = msg.split("\\s+");
                        userRegister(regparams);
                    } else if(msg.startsWith("/log")) {
                        String[] regparams = msg.split("\\s+");
                        userLogin(regparams);
                    } else if(msg.equals("/help")) {
                        out.println("# Chat Commands: ");
                        for(String item : ChatServer.cmds) {
                            out.println(item);
                        }
                    } else if(msg.equals("/users")) {
                         if(this.myInfo.isLogged()) {
                             listAllUsers();
                         } else {
                             out.println("# Guests don't have access to this command.");
                         }
                    } else if(msg.equals("/fupdate")) {
                        if(this.myInfo.isLogged()) {
                            out.println(msg);
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else if(msg.startsWith("/files")) {
                        if(this.myInfo.isLogged()) {
                            //TODO: listar ficheiros de um utilizador
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else if(msg.startsWith("/myfiles")) {
                        if(this.myInfo.isLogged()) {
                            //TODO: listar os meus ficheiros
                        } else {
                            out.println("# Guests don't have access to this command.");
                        }
                    } else {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i] != this) {
                                threads[i].out.println("<" + this.myInfo.getUsername() + ">: " + msg);
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
        out.println("Users list:");
        for(SClientThread t : threads) {
            if(t != null && t != this)
                out.println(t.myInfo.getUsername());
        }
    }
    
    //Informs users that a guest is now registered.
    private void msgGuestSignedUp(String guestName) {
        for (int i = 0; i < this.maxClientCount; i++) {
            if (threads[i] != null && threads[i] != this) {
                threads[i].out.println("# " + guestName + " is now " + this.myInfo.getUsername() + " !");
            }
        }
    }
    
    //Increments Guest count.
    private synchronized void incGuestCount() {
        ChatServer.guestCount++;
        if(ChatServer.guestCount == Integer.MAX_VALUE) {
            ChatServer.guestCount = 1;
        }
    }
    
    //User signup.
    private synchronized void userRegister(String[] params) {
        if(params.length != 4) {
            out.println("# [Reg] Wrong format used!");
            out.println("# [Reg] Ex: /reg username password password");
        } else {
            if(validateRegister(params)) {
                UserInfo newUser = new UserInfo(params[1], params[2], true);
                ChatServer.userDB.add(newUser);
                out.println("# [Reg] Account created successfully!");
                out.println("# [Reg] Logging in...");
                String guestName = this.myInfo.getUsername();
                //TODO: inserir newUser nos connectedUsers!
                this.myInfo = newUser;
                Client.userinfo = newUser;
                System.out.println(Client.userinfo.getUsername());
                File newDir = new File(ChatServer.chatDirectory + '\\' + this.myInfo.getUsername());
                newDir.mkdirs();
                out.println("# [INTERNAL] Start multicast.");
                out.println("# Welcome " + this.myInfo.getUsername() + ".");
                msgGuestSignedUp(guestName);
            }
        }
    }
    
    //Validates information for user signup.
    private boolean validateRegister(String[] params) {
        String pattern = "^[a-zA-Z0-9]*$";
        for(int i = 1; i < params.length; i++){
            if(!params[i].matches(pattern)) {
                out.println("# [Reg] Username and password can only contain AlphaNumeric characters!");
                return false;
            }
        }
        if(!params[2].equals(params[3])) {
            out.println("# [Reg] Passwords must match!");
            return false;
        }
        return true;
    }
    
    //User login.
    private void userLogin(String[] params) {
        if(params.length != 3) {
            out.println("# [Log] Wrong format used!");
            out.println("# [Log] Ex: /log username password");
        } else {
            UserInfo tmp;
            if((tmp = validateLogin(params)) != null) {
                this.myInfo = tmp;
                Client.userinfo = this.myInfo;
                //TODO: Mover para o Client.
                File dir = new File(ChatServer.chatDirectory + '\\' + this.myInfo.getUsername());
                if(!dir.exists()) {
                    out.println("# [Log] Your Files folder was wiped somehow.");
                    out.println("# [Log] Creating another one...");
                    dir.mkdirs();
                }
                out.println("# [Log] Logging in as " + this.myInfo.getUsername() + ".");
                out.println("# [INTERNAL] Start multicast.");
                out.println("# Welcome " + this.myInfo.getUsername() + ".");
            }
        }
    }
    
    //Validates information for user login.
    private UserInfo validateLogin(String[] params) {
        Iterator<UserInfo> it = ChatServer.userDB.iterator();
        while(it.hasNext()) {
            UserInfo user = it.next();
            if(user.getUsername().equals(params[1])) {
                if(user.getPassword().equals(params[2])) {
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
