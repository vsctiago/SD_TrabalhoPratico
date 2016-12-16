package MulticastPackage;

import ChatPackage.Client;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class WorkFileList extends Thread {

    private ArrayList<FileList> fileList;
    private DatagramPacket recv;

    public WorkFileList(DatagramPacket recv) {
        this.fileList = Client.getFileList();
        this.recv = recv;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
            ObjectInputStream o_in = new ObjectInputStream(b_in);
            FileList tempFileList = (FileList) o_in.readObject();
            for (FileList f : fileList) {
                if (f.getClientName().equals(tempFileList.getClientName())) {
                    fileList.remove(f);
                    Client.addFileList(tempFileList);
                    return;
                }
            }
            Client.addFileList(tempFileList);
            b_in.reset();
        } catch (Exception e) {
            System.out.println("Exception Work File Receive -> " + e);
        }
    }
}
