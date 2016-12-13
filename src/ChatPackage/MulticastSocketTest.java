package ChatPackage;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class MulticastSocketTest extends Thread {
    
    private Socket cs = null;
    private InetAddress group;
    private MulticastSocket s;
    private String msg;
    
    public MulticastSocketTest(Socket cs) {
        this.cs = cs;
    }
    
    @Override
    public void run() {
        
        String msg = "Hello";
        
        try {
            group = InetAddress.getByName("localhost");
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            s = new MulticastSocket(6789);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            s.joinGroup(group);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),group, 6789);

        try {
            s.send(hi);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        try {
            s.receive(recv);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        msg = s.toString();
        
        try {
            s.leaveGroup(group);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getMsg() {
        return msg;
    }
    
}
