package MulticastPackage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class MulticastSocketReceive extends Thread {

    private final MulticastSocket ms;
    static ArrayList<FileList> fileList;
    private Boolean close = false;

    public MulticastSocketReceive(MulticastSocket ms, ArrayList<FileList> fileList) {
        this.ms = ms;
        MulticastSocketReceive.fileList = fileList;
    }

    @Override
    public void run() {

        try {
            while (!close) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                ms.receive(recv);
                ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
                ObjectInputStream o_in = new ObjectInputStream(b_in);
                Thread workFileList = new WorkFileList(fileList,(FileList) o_in.readObject());
                workFileList.start();
                recv.setLength(buf.length);
                b_in.reset();
            }
        } catch (Exception e) {
            System.out.println("Exception Multicast Receive -> " + e);
        }
    }

    public void setClose() {
        this.close = true;
    }
}
