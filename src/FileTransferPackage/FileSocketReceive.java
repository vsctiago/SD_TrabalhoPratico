package FileTransferPackage;

import ChatClientPackage.Client;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileSocketReceive extends Thread {

    private String ficheiro, fileToReceive;
    private int portToSend;

    public FileSocketReceive(String fileToReceive) {
        this.fileToReceive = fileToReceive;
        this.portToSend = 3781;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(portToSend);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        portToSend = serverSocket.getLocalPort();

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        byte[] bytes = new byte[1024];

        int count;
        boolean exists = false;
        try {
            while ((count = in.read(bytes)) > 0) {
                String txt = new String(bytes, 0, count);
                if (txt.equals("FILE NO LONGER EXISTS")) {
                    System.out.println(txt);
                    break;
                } else {
                    if (!exists) {
                        try {
                            out = new FileOutputStream(Client.getUserinfo().getDirectory() + "\\" + fileToReceive);
                        } catch (FileNotFoundException ex) {
                            System.out.println("File not found. ");
                        }
                        exists = true;
                    }
                }
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("FileSocketReceive -> " + ex);
        }
    }

    public int getPort() {
        return this.portToSend;
    }
}
