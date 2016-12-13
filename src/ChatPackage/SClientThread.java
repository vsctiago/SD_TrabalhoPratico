/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Tiago Fernandes
 */
public class SClientThread extends Thread {

    private Socket cs = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private final SClientThread[] threads;
    private final int maxClientCount;
    private boolean listening = true;
    
    private String msg;
    private String name;
    private String pass;

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

            fill(); //TODO: É PARA TIRAR!!!!!!
            
            //Assigning Guest name and incrementing Guest count.
            this.name = "guest"+ChatServer.guestCount;
            incGuestCount();
            
            //Notifying all users in chat that someone arrived!
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].out.println("*** " + name + " entered the chat! ***");
                }
            }

            while (listening) {
                msg = in.readLine().trim();
                if (msg != null) {
                    if (msg.startsWith("/quit")) {
                        break;
                    }
                    if(msg.startsWith("/reg")) {
                        String[] regparams = msg.split("\\s+");
                        userRegister(regparams);
                    }
                    if (msg.startsWith("/list")) {
                        this.out.println();
                    }
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this) {
                            threads[i].out.println("<" + name + ">: " + msg);
                        }
                    }
                }
            }
            //Clear position in threads to free space for another connection
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

    private synchronized void incGuestCount() {
        ChatServer.guestCount++;
        if(ChatServer.guestCount == Integer.MAX_VALUE) {
            ChatServer.guestCount = 0;
        }
    }
    
    private void userRegister(String[] params) {
        if(params.length > 4) {
            out.println("[Registration] Wrong format used!");
            out.println("[Registration] Ex: /reg username password password");
        } else {
            if(validateRegister(params)) {
                UserInfo newClient = new UserInfo(params[1], params[2]);
                ChatServer.clientDB.add(newClient);
            } else {
                out.println("[Registration] Wrong format used!");
                out.println("[Registration] Username and password can only contain AlphaNumeric characters!");
            }
        }
    }
    
    private boolean validateRegister(String[] params) {
        String pattern = "^[a-zA-Z0-9]*$";
        for(int i = 1; i < params.length; i++){
            if(!params[i].matches(pattern)) {
                return false;
            }
        }
        //TODO: validar se as duas passes sao iguais
        return true;
    }
    
    private boolean login() throws IOException {
        boolean validated = false;
        int position;
        
        out.println("[Login] Enter your username.");
        name = in.readLine().trim();
        position = userExists();
        if (position != -1) {
            validated = checkPassword(position);
            if (validated) {
                out.println("[Login] Welcome " + name + ".");
                return true;
            } else {
                out.println("[Login] Wrong password!");
                return false;
            }
        } else {
            out.println("[Login] Username doesn't exist!");
            return false;
        }
    }

    private int userExists() {
        for (UserInfo cInfo : ChatServer.clientDB) {
            if (cInfo.getUsername().equals(name)) {
                return ChatServer.clientDB.indexOf(cInfo);
            }
        }
        return -1;
    }

    private boolean checkPassword(int position) throws IOException {
        out.println("[Login] Enter your password.");
        pass = in.readLine().trim();
        return ChatServer.clientDB.get(position).getPassword().equals(pass);
    }

    private void fill() {
        UserInfo ci = new UserInfo("cenas", "cenas");
        ChatServer.clientDB.add(ci);
        UserInfo co = new UserInfo("coisas", "coisas");
        ChatServer.clientDB.add(co);
        UserInfo ca = new UserInfo("yolo", "yolo");
        ChatServer.clientDB.add(ca);
    }
}
