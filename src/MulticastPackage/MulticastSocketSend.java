package MulticastPackage;

import ChatPackage.Client;
import ChatPackage.UserInfo;
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
    private FileList fileClient;
    private Boolean close = false;

    public MulticastSocketSend(InetAddress group, MulticastSocket ms, int porta) {
        this.group = group;
        this.ms = ms;
        this.fileClient = new FileList();
        this.porta = porta;
    }

    @Override
    public void run() {
        try {
            while (!close) {
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteArr);
                objOut.writeObject(fileClient);
                FilesList();
                byte[] buf = byteArr.toByteArray();
                DatagramPacket fileList = new DatagramPacket(buf, buf.length, group, porta);
                ms.send(fileList);
                System.out.println("Package is sent!");

                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    System.out.println("Exception handled " + e);
                }

            }
        } catch (Exception e) {
            System.out.println("Exception Multicast Send -> " + e);
        }
    }

    public File[] listDir(File dir) {
        ArrayList<File> enc = new ArrayList<>();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //Adiciona no Vector os arquivos encontrados dentro de 'files[i]':
                File[] recFiles = listDir(files[i]);
                for (int j = 0; j < recFiles.length; j++) {
                    enc.add(recFiles[j]);
                }
            } else {
                //Adiciona no Array o arquivo encontrado dentro de 'dir':
                enc.add(files[i]);
            }
        }
        //Transforma um Array em um File[]:
        File[] encontrados = new File[enc.size()];
        for (int i = 0; i < enc.size(); i++) {
            encontrados[i] = (File) enc.get(i);
        }
        return encontrados;
    }

    public void FilesList() {
        UserInfo client =  Client.getUserinfo();
        File dir = new File(client.getDirectory());
        File[] files = listDir(dir);
        if (files != null) {
            this.fileClient.setClientName(client.getUsername());
            for(File f : files) {
                this.fileClient.addFileName(f.getName());
            }
        }
    }

    public void setClose() {
        this.close = true;
    }
}
