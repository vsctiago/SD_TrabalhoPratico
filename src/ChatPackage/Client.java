/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

import MulticastPackage.MulticastSocketSend;
import MulticastPackage.FileList;
import MulticastPackage.MulticastSocketReceive;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Tiago Fernandes
 */
public class Client extends Thread {

    static Socket cs = null;
    static InetAddress group;
    
    static String ipChatServe = "localhost";
    static int portaChatServe = 4444;
    
    static MulticastSocket ms;
    static ArrayList<FileList> fileList = new ArrayList<>();
    
    static String ipMulticast = "230.1.1.1";
    static int portaMulticast = 6789;
    static boolean closed = false;
    
    static UserInfo userinfo;
    
    public static void main(String[] args) throws IOException {
        try {
            cs = new Socket(ipChatServe, portaChatServe);
        } catch (IOException e) {
            System.out.println(e);
        }

        FileList file = new FileList("dadasda");

        try {

            group = InetAddress.getByName(ipMulticast);
            ms = new MulticastSocket(portaMulticast);
            ms.joinGroup(group);

            Thread clientWrite = new Thread(new ClientWrite(cs));
            Thread clientRead = new Thread(new ClientRead(cs));

            Thread multicastSocketSend = new Thread(new MulticastSocketSend(group, ms, portaMulticast, file));
            Thread multicastSocketReceive = new Thread(new MulticastSocketReceive(ms, fileList));

            clientWrite.start();
            clientRead.start();
            multicastSocketSend.start();
            multicastSocketReceive.start();

            try {
                multicastSocketSend.join();
                multicastSocketReceive.join();
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
