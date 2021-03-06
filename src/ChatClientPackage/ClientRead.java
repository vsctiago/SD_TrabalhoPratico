package ChatClientPackage;

import FileTransferPackage.FileSocketSend;
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
                if (msg.equals("/quit") || msg.equals("/logout")) {
                    ((MulticastSocketSend)multicastSocketSend).setClose();
                    ((MulticastSocketSend)multicastSocketSend).interrupt();
                    ((MulticastSocketReceive)multicastSocketReceive).setClose();
                    break;
                } else if(msg.equals("/fupdate") && Client.userinfo.isLogged()) {
                    Client.clearList();
                    multicastSocketSend.interrupt();
                } else if(msg.equals("# [INTERNAL] Logged in.")) {
                    Client.userinfo = Client.tmpInfo;
                    Client.userinfo.setLogged(true);
                    File dir = new File(Client.chatDirectory + '\\' + Client.userinfo.getUsername());
                    if(!dir.exists()) {
                        System.out.println(" [Log] Creating files folder.");
                        dir.mkdirs();
                    }
                    startMulticastSocketSend();
                    startMulticastSocketReceive();
                } else if(msg.startsWith("/dl")) {
                    String[] params = msg.split("\\s+", 4);
                    Thread send = new FileSocketSend(params[3], Integer.parseInt(params[2]));
                    send.start();
                } else {
                    System.out.println(msg);
                } 
            }
            in.close();
            
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
