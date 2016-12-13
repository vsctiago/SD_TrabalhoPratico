package Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransfer extends Thread {

    Socket cs = null;

    public FileTransfer(Socket cs) {
        this.cs = cs;
    }

    @Override
    public void run(){
        int x = 1;
        String address;
        int port;
        if (x == 1) {
            DownloadFile(port);
        } else {
            SendFile(address, port);
        }
    }

    public void DownloadFile(int port) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

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

        try {
            out = new FileOutputStream("M:\\test2.xml");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[16 * 1024];

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
        serverSocket.close();
    }

    public void SendFile(String address, int port) throws IOException {
        Socket socket = cs;
        String host = address;

        socket = new Socket(host, port);

        File file = new File("C:\\SD\\Client 1\\test.png");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
    }

}
