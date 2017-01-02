package ChatClientPackage;

import StructPackage.UserInfo;
import MulticastPackage.MulticastSocketSend;
import MulticastPackage.ListFileClient;
import MulticastPackage.MulticastSocketReceive;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread {

    static Socket cs = null;
    static InetAddress group;
    
    static String ipChatServe = "localhost";
    static int portaChatServe = 4444;
    
    static MulticastSocket ms;
    static ArrayList<ListFileClient> listFiles = new ArrayList<>();
    
    static String ipMulticast = "230.1.1.2";
    static int portaMulticast = 6789;
    static boolean closed = false;
    
    static String chatDirectory = "C:\\ChatApp";
    static UserInfo userinfo = new UserInfo();
    static UserInfo tmpInfo = new UserInfo();
    
    public static void main(String[] args) throws IOException {
        
        chatDirectory = new File("").getAbsolutePath();
        chatDirectory = chatDirectory + "\\ChatShare";
        
        try {
            cs = new Socket(ipChatServe, portaChatServe);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {

            group = InetAddress.getByName(ipMulticast);
            ms = new MulticastSocket(portaMulticast);
            ms.joinGroup(group);

            Thread clientWrite = new ClientWrite(cs);
            Thread clientRead = new ClientRead(cs);

            Thread multicastSocketSend = new MulticastSocketSend(group, ms, portaMulticast);
            Thread multicastSocketReceive = new MulticastSocketReceive(ms);

            ((ClientRead)clientRead).setMulticastSocketSend(multicastSocketSend);
            ((ClientRead)clientRead).setMulticastSocketReceive(multicastSocketReceive);
            
            clientWrite.start();
            clientRead.start();
            
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

    public static UserInfo getUserinfo() {
        return userinfo;
    }

    public static ArrayList<ListFileClient> getFileList(){
        return Client.listFiles;
    }
    
    public synchronized static void removeFileList(ListFileClient toRemove){
        Client.listFiles.remove(toRemove);
    }
    
    public synchronized static void addFileList(ListFileClient newFileList){
        Client.listFiles.add(newFileList);
    }
    
    public static void clearList() {
        Client.listFiles.clear();
    }
}
