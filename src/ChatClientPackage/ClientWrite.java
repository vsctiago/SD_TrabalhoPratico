package ChatClientPackage;

import MulticastPackage.ListFileClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

public class ClientWrite extends Thread {

    Socket cs = null;
    PrintWriter out = null;
    BufferedReader input = null;

    public ClientWrite(Socket cs) {
        this.cs = cs;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(cs.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(System.in));

            String msg;
            while (!Client.isClosed()) {
                msg = input.readLine();
                if (msg.startsWith("/reg") || msg.startsWith("/log")) {
                    String[] regparams = msg.split("\\s+");
                    Client.tmpInfo.setUsername(regparams[1]);
                    Client.tmpInfo.setPassword(regparams[2]);
                    Client.tmpInfo.setDirectory(Client.chatDirectory + "\\" + Client.tmpInfo.getUsername());
                } else if (msg.startsWith("/files")) {
                    showAllFiles();
                } else if (msg.equals("/quit")) {
                    Client.closeInput();
                }
                out.println(msg);
            }
            out.close();
            input.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void showAllFiles() {
        System.out.println("@ Files available in the group:");
        System.out.println("@ Ex: Username - File name");
        Iterator<ListFileClient> it = Client.listFiles.iterator();
        while(it.hasNext()) {
            ListFileClient fl = it.next();
            Iterator<String> it2 = fl.getListFileNames().iterator();
            while(it2.hasNext()) {
                String fn = it2.next();
                System.out.println("@ " + fl.getClientName() + " - " + fn);
            }
        }
    }
}
