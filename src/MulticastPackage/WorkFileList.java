package MulticastPackage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class WorkFileList extends Thread {

    private ArrayList<FileList> fileList;
    private DatagramPacket recv;

    public WorkFileList(ArrayList<FileList> fileList, DatagramPacket recv) {
        this.fileList = fileList;
        this.recv = recv;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
            ObjectInputStream o_in = new ObjectInputStream(b_in);
            FileList tempFileList = (FileList) o_in.readObject();
            for (int i = 0; i < fileList.size(); i++) {
                if (fileList.get(i).getClientName().equals(tempFileList.getClientName())) {
                    fileList.remove(i);
                    fileList.add(tempFileList);
                    return;
                }
            }
            b_in.reset();
            fileList.add(tempFileList);
        } catch (Exception e) {
            System.out.println("Exception Multicast Receive -> " + e);
        }
    }
}
