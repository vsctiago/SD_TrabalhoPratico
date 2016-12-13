package Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastSocketTest extends Thread {

    private InetAddress group;
    private MulticastSocket s;
    private String msg;

    @Override
    public void run() {

        String msg =  "Hello";

        try {
            group = InetAddress.getByName("230.1.1.1");
        } catch (Exception e) {
            System.out.println("1-->" + e);
        }

        try {
            s = new MulticastSocket(6789);
        } catch (Exception e) {
            System.out.println("2-->" + e);
        }

        try {
            s.joinGroup(group);
        } catch (Exception e) {
            System.out.println("3-->" + e);
        }

        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);

        try {
            s.send(hi);
        } catch (Exception e) {
            System.out.println("4-->" + e);
        }
        
        System.out.println("Recev---1>");
        
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        System.out.println("---2>");
        
        try {
            s.receive(recv);
        } catch (Exception e) {
            System.out.println("5-->" + e);
        }
        
        System.out.println("---3>");

        ObjectInputStream ois = null;
        
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(recv.getData()));
        } catch (IOException ex) {
            Logger.getLogger(MulticastSocketTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("---4>");
        
        try {
            System.out.println(ois.readObject().toString());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MulticastSocketTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e){
            System.out.println(e);
        }

        String received = new String(recv.getData());
        System.out.println(recv.getData());
        
        System.out.println("---5>");

        try {
            s.leaveGroup(group);
        } catch (Exception e) {
            System.out.println("6-->" + e);
        }
    }

    public String getMsg() {
        return msg;
    }

}
