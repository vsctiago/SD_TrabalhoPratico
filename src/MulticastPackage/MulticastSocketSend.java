package MulticastPackage;

import ChatClientPackage.Client;
import StructPackage.UserInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class MulticastSocketSend extends Thread {

    private final InetAddress group;
    private final MulticastSocket ms;
    private final int porta;
    private ListFileClient listFilesClient;
    private Boolean close = false;

    public MulticastSocketSend(InetAddress group, MulticastSocket ms, int porta) {
        this.group = group;
        this.ms = ms;
        this.listFilesClient = new ListFileClient();
        this.porta = porta;
    }

    @Override
    public void run() {
        try {
            while (!close) {
                this.listFilesClient = new ListFileClient();
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteArr);
                FilesList();
                objOut.writeObject(listFilesClient);
                byte[] buf = byteArr.toByteArray();
                DatagramPacket fileList = new DatagramPacket(buf, buf.length, group, porta);
                ms.send(fileList);
                System.out.println("@ Files list updated!");
                try {
                    Thread.sleep(600000); //10mim
                } catch (InterruptedException e) { }
            }
        } catch (Exception e) { }
        
        ms.close();
    }

    public File[] listDir(File dir) {
        ArrayList<File> encTemp = new ArrayList<>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                encTemp.add(file);
            }
        }
        File[] encontrados = new File[encTemp.size()];
        for (int i = 0; i < encTemp.size(); i++) {
            encontrados[i] = (File) encTemp.get(i);
        }
        return encontrados;
    }

    public void FilesList() {
        UserInfo client =  Client.getUserinfo();
        File dir = new File(client.getDirectory());
        File[] files = listDir(dir);
        if (files != null) {
            this.listFilesClient.setClientName(client.getUsername());
            for(File f : files) {
                this.listFilesClient.addFileName(f.getName());
            }
        }
    }

    public void setClose() {
        this.close = true;
    }
}
