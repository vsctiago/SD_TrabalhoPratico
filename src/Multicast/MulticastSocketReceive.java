package Multicast;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSocketReceive extends Thread {

    private InetAddress group;
    private MulticastSocket ms;
    private FileList fileClient;
    private Boolean close = false;

    @Override
    public void run() {
        try {
            group = InetAddress.getByName("230.1.1.1");
            ms = new MulticastSocket(6789);
            ms.joinGroup(group);

            while (!close) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                ms.receive(recv);
                ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
                ObjectInputStream o_in = new ObjectInputStream(b_in);
                fileClient = (FileList) o_in.readObject();
                recv.setLength(buf.length);
                b_in.reset();
                System.out.println(fileClient.getClientName());
                System.out.println("Package is receive!");
            }
        } catch (Exception e) {
            System.out.println("Multicast Receive -> " + e);
        }
    }

    public void setClose() {
        this.close = true;
    }
}
