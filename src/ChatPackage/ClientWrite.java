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
public class ClientWrite extends Thread {

    Socket cs = null;
    PrintWriter out = null;
    BufferedReader input = null;

    public ClientWrite(Socket cs) {
        this.cs = cs;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(cs.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(System.in));
            
            while (!Client.isClosed()) {
                out.println(input.readLine());
            }
            
            out.close();
            input.close();
            
        } catch (IOException e) {
            System.out.println(e);
        }
        
    }
}
