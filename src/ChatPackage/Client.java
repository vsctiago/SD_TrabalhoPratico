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
public class Client extends Thread {

    static Socket cs = null;
    private static boolean closed = false;

    public static void main(String[] args) throws IOException {
        try {
            cs = new Socket("localhost", 4444);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            
            new Thread(new ClientWrite(cs)).start();
            new Thread(new ClientRead(cs)).start();

            if(closed) {
                cs.close();
            }

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
