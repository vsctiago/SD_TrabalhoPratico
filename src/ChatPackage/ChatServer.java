/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Tiago Fernandes
 */
public class ChatServer {

    static ServerSocket ss = null;
    static Socket cs = null;
    static boolean listening = true;
    
    static final int MAXCLIENTS = 10;
    static SClientThread[] clients = new SClientThread[MAXCLIENTS];
    static ArrayList<UserInfo> userDB = new ArrayList<>();
    
    static int guestCount;
    static String chatDirectory = "C:\\ChatApp";
    static String[] cmds = new String[]{
        "/reg \t\t-> Signup user.",
        "/log \t\t-> Login user.",
        "/users \t\t-> List all users connected.",
        "/files \t\t-> List all files from users connected.",
        "/myfiles \t-> List all my files.",
        "/dl \t\t-> Download one or more files from one user.",
        "/quit or /logout \t-> Logout and leaves chat."};

    public static void main(String[] args) throws IOException {
        try {
            ss = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            while (listening) {
                cs = ss.accept();
                for(int i = 0; i < MAXCLIENTS; i++) {
                    if(clients[i] == null) {
                        (clients[i] = new SClientThread(cs, clients)).start();
                        break;
                    }
                }
            }

            cs.close();
            ss.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    
}
