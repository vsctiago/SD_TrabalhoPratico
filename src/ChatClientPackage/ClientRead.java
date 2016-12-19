package ChatClientPackage;

import MulticastPackage.MulticastSocketReceive;
import MulticastPackage.MulticastSocketSend;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
                if (msg.startsWith("/quit") || msg.equals("/logout")) {
                    ((MulticastSocketSend)multicastSocketSend).setClose();
                    ((MulticastSocketReceive)multicastSocketReceive).setClose();
                    System.out.println(msg);
                    break;
                } else if(msg.equals("/fupdate") && Client.userinfo.isLogged()) {
                    multicastSocketSend.interrupt();
                } else if(msg.equals("# [INTERNAL] Logged in.")) {
                    Client.userinfo = Client.tmpInfo;
                    Client.userinfo.setLogged(true);
                    File dir = new File(ChatServer.chatDirectory + '\\' + Client.userinfo.getUsername()); //Problema aqui
                    if(!dir.exists()) {
                        System.out.println(" [Log] Creating files folder.");
                        dir.mkdirs();
                    }
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
