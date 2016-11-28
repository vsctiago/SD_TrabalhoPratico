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
    private int maxClientCount;
    private boolean listening = true;
    private String msg;
    private String name;

    public SClientThread(Socket cs, SClientThread[] threads) {
        this.cs = cs;
        this.threads = threads;
        maxClientCount = threads.length;
    }

    @Override
    public void run() {
        int maxClientsCount = this.maxClientCount;
        SClientThread[] threads = this.threads;

        try {
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            out = new PrintWriter(cs.getOutputStream(), true);
            //First line read will be the username!
            out.println("Enter your name.");
            name = in.readLine().trim();
            out.println("*** Hello " + name + " ***");
            //Notifying all users in chat that someone arrived!
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].out.println("*** " + name + " entered the chat! ***");
                }
            }

            while (listening) {
                msg = in.readLine();
                if (msg != null) {
                    if(msg.startsWith("/quit")){
                        break;
                    }
                    if(msg.startsWith("/list")){
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
            for(int i = 0; i < maxClientsCount; i++) {
                if(threads[i] == this)
                    threads[i] = null;
            }
            
            out.close();
            in.close();
            cs.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
