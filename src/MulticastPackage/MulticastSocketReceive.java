package MulticastPackage;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastSocketReceive extends Thread {

    private final MulticastSocket ms;
    private Boolean close = false;

    public MulticastSocketReceive(MulticastSocket ms) {
        this.ms = ms;
    }

    @Override
    public void run() {

        try {
            while (!close) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                ms.receive(recv);
                Thread workFileList = new WorkFileList(recv);
                workFileList.start();
                recv.setLength(buf.length);
            }
        } catch (Exception e) { }
        
        ms.close();
    }

    public void setClose() {
        this.close = true;
    }
}
