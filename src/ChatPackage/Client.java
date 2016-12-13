/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

import Multicast.*;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Tiago Fernandes
 */
public class Client extends Thread {

    static Socket cs = null;
    private static boolean closed = false;

    public static void main(String[] args) throws IOException {
        try {
            cs = new Socket("localhost", 4444);
        } catch (IOException e) {
            System.out.println(e);
        }

        FileList file = new FileList("dadasda");
        
        try {
            
            Thread clientWrite = new Thread(new ClientWrite(cs));
            Thread clientRead = new Thread(new ClientRead(cs));
            
            Thread MulticastSocketSend = new MulticastSocketSend(file);
            Thread MulticastSocketReceive = new MulticastSocketReceive();
            
            clientWrite.start();
            clientRead.start();
            MulticastSocketSend.start();
            MulticastSocketReceive.start();
            
            try {
                clientWrite.join();
                clientRead.join();
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            cs.close();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static boolean isClosed() {
        return closed;
    }

    public synchronized static void closeInput() {
        Client.closed = true;
    }
    
    
}
