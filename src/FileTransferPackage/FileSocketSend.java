package FileTransferPackage;

import ChatClientPackage.Client;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileSocketSend extends Thread {

    private Socket cs;
    private final String address = "localhost";
    private int portToSend;
    private String fileToSend;

    public FileSocketSend(String fileToSend, int portToSend) {
        this.fileToSend = fileToSend;
        this.portToSend = portToSend;
    }

    @Override
    public void run() {
        Socket socket = cs;
        String host = address;

        try {
            socket = new Socket(host, portToSend);

            InputStream in = null;
            OutputStream out = null;
            byte[] bytes = new byte[1024];
            
            File file = new File(Client.getUserinfo().getDirectory() + fileToSend);
            if (!file.exists()) {
                in = new ByteArrayInputStream("NO LONG EXIST".getBytes());
            }   else{
                long length = file.length();
                in = new FileInputStream(file);
            }
            
            out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("FileSocketSend -> " + ex);
        }
    }
}
