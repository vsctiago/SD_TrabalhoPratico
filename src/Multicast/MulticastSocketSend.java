package Multicast;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSocketSend extends Thread {

    private InetAddress group;
    private MulticastSocket ms;
    private FileList fileClient;
    private Boolean close = false;

    public MulticastSocketSend(FileList fileClient) {
        this.fileClient = fileClient;
    }

    @Override
    public void run() {
        try {
            group = InetAddress.getByName("230.1.1.1");
            ms = new MulticastSocket(6789);
            ms.joinGroup(group);
           
            while (!close) {
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteArr);
                objOut.writeObject(fileClient);
                byte[] buf = byteArr.toByteArray();
                DatagramPacket fileList = new DatagramPacket(buf, buf.length, group, 6789);
                ms.send(fileList);
                System.out.println("Package is sent!");
                sleep(600000);
            }
        } catch (Exception e) {
            System.out.println("Multicast Send -> " + e);
        }
    }

    public void setClose() {
        this.close = true;
    }
}
