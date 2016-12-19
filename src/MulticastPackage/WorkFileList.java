package MulticastPackage;

import ChatClientPackage.Client;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class WorkFileList extends Thread {

    private ArrayList<ListFileClient> fileList;
    private DatagramPacket recv;

    public WorkFileList(DatagramPacket recv) {
        this.fileList = (ArrayList<ListFileClient>) Client.getFileList().clone();
        this.recv = recv;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream b_in = new ByteArrayInputStream(recv.getData());
            ObjectInputStream o_in = new ObjectInputStream(b_in);
            ListFileClient tempFileList = (ListFileClient) o_in.readObject();
                for (ListFileClient f : fileList) {
                    if (f.getClientName().equals(tempFileList.getClientName())) {
                        //System.out.println(Client.getUserinfo().getUsername() + "vou remover");
                        Client.removeFileList(f);
                        //System.out.println(Client.getUserinfo().getUsername() + "removi e vou adicionar");
                        Client.addFileList(tempFileList);
                        //System.out.println(Client.getUserinfo().getUsername() + "adicionei");
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
