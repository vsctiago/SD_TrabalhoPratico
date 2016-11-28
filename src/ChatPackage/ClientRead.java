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
    
    public ClientRead(Socket cs) {
        this.cs = cs;
    }
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
                if (msg.startsWith("/quit")) {
                    break;
                }
            }
            in.close();
            Client.closeInput();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
