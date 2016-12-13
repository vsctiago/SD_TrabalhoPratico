package Multicast;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class MulticastSocketReceive extends Thread {

    private InetAddress group;
    private MulticastSocket ms;
    private ArrayList<FileList> fileList;
    private Boolean close = false;

    public MulticastSocketReceive(InetAddress group, MulticastSocket ms) {
        this.group = group;
        this.ms = ms;
        this.fileList  = new ArrayList<>();
    }

    @Override
    public void run() {
        int rear = 0;
        
        try {
            while (!close) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                ms.receive(recv);
                ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
                ObjectInputStream o_in = new ObjectInputStream(b_in);
                fileList.add((FileList) o_in.readObject());
                recv.setLength(buf.length);
                b_in.reset();
                System.out.println(fileList.get(rear).getClientName());
                System.out.println("Package is receive!");
                rear++;
            }
        } catch (Exception e) {
            System.out.println("Multicast Receive -> " + e);
        }
    }

    public void setClose() {
        this.close = true;
    }
}
