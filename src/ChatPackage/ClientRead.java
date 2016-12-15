/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Tiago Fernandes
 */
public class ClientRead extends Thread {

    Socket cs = null;
    BufferedReader in = null;
    Thread multicastSocketSend;
    Thread multicastSocketReceive;
    
    public ClientRead(Socket cs) {
        this.cs = cs;
    }
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            
            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("/quit")) {
                    System.out.println(msg);
                    break;
                } else if(msg.equals("/fupdate")) {
                    
                } else if(msg.equals("# [INTERNAL] Start multicast.")) {
                    startMulticastSocketSend();
                    startMulticastSocketReceive();
                } else {
                    System.out.println(msg);
                }
            }
            in.close();
            Client.closeInput();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setMulticastSocketSend(Thread multicastSocketSend) {
        this.multicastSocketSend = multicastSocketSend;
    }

    public void setMulticastSocketReceive(Thread multicastSocketReceive) {
        this.multicastSocketReceive = multicastSocketReceive;
    }
    
    public void startMulticastSocketSend() {
        this.multicastSocketSend.start();
    }
    
    public void startMulticastSocketReceive() {
        this.multicastSocketReceive.start();
    }
    
}
