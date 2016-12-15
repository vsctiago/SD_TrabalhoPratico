package MulticastPackage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class MulticastSocketReceive extends Thread {

    private final MulticastSocket ms;
    private final ArrayList<FileList> fileList;
    private Boolean close = false;

    public MulticastSocketReceive(MulticastSocket ms, ArrayList<FileList> fileList) {
        this.ms = ms;
        this.fileList = fileList;
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
                fileList.add((FileList) o_in.readObject()); //retirar os antigos filelist
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
