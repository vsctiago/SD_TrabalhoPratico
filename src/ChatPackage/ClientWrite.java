package ChatPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            
            String msg;
            //TODO: Escrever os dados do login ou registo em variaveis temporarias para meter em userinfo.
            while (!Client.isClosed()) {
                msg = input.readLine();
                out.println(msg);
            }
            
            out.close();
            input.close();
            
        } catch (IOException e) {
            System.out.println(e);
        }
        
    }
}
