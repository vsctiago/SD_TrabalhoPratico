package MulticastPackage;

import ChatPackage.Client;
import ChatPackage.UserInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Vector;

public class MulticastSocketSend extends Thread {

    private final InetAddress group;
    private final MulticastSocket ms;
    private final int porta;
    private final FileList fileClient;
    private Boolean close = false;

    public MulticastSocketSend(InetAddress group, MulticastSocket ms, int porta) {
        this.group = group;
        this.ms = ms;
        this.fileClient = null;
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
            System.out.println("Multicast Send -> " + e);
        }
    }

    public File[] listDir(File dir) {
        Vector enc = new Vector();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //Adiciona no Vector os arquivos encontrados dentro de 'files[i]':
                File[] recFiles = listDir(files[i]);
                for (int j = 0; j < recFiles.length; j++) {
                    enc.addElement(recFiles[j]);
                }
            } else {
                //Adiciona no Vector o arquivo encontrado dentro de 'dir':
                enc.addElement(files[i]);
            }
        }
        //Transforma um Vector em um File[]:
        File[] encontrados = new File[enc.size()];
        for (int i = 0; i < enc.size(); i++) {
            encontrados[i] = (File) enc.elementAt(i);
        }
        return encontrados;
    }

    public void FilesList() {
        UserInfo client = new UserInfo();
        client = Client.getUserinfo();
        System.out.println(client.getUsername());
        File dir = new File(client.getDirectory());
        File[] files = listDir(dir);
        if (files != null) {
            fileClient.setClientName(client.getUsername());
            for (int i = 0; i < files.length; i++) {
                fileClient.setFileName(i, files[i].getName());
            }
        }
    }

    public void setClose() {
        this.close = true;
    }
}
