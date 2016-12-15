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
                Thread workFileList = new WorkFileList(fileList, recv);
                workFileList.start();
                recv.setLength(buf.length);
            }
        } catch (Exception e) {
            System.out.println("Exception Multicast Receive -> " + e);
        }
    }

    public void setClose() {
        this.close = true;
    }
}
