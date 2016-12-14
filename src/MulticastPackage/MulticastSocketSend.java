package MulticastPackage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSocketSend extends Thread {

    private final InetAddress group;
    private final MulticastSocket ms;
    private final int porta;
    private final FileList fileClient;
    private Boolean close = false;

    public MulticastSocketSend(InetAddress group, MulticastSocket ms, 
            int porta, FileList fileClient) {
        this.group = group;
        this.ms = ms;
        this.porta = porta;
        this.fileClient = fileClient;
    }

    @Override
    public void run() {
        try {           
            while (!close) {
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteArr);
                objOut.writeObject(fileClient);
                byte[] buf = byteArr.toByteArray();
                DatagramPacket fileList = new DatagramPacket(buf, buf.length, group, porta);
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
