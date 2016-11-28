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
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Tiago Fernandes
 */
public class TcpMultiServer {

    static ServerSocket ss = null;
    static Socket cs = null;
    static boolean listening = true;
    
    static int maxClients = 10;
    static SClientThread[] clients = new SClientThread[maxClients];

    public static void main(String[] args) throws IOException {
        try {
            ss = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            while (listening) {
                cs = ss.accept();
                for(int i = 0; i < maxClients; i++) {
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