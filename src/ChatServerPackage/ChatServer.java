package ChatServerPackage;

import StructPackage.Group;
import StructPackage.UserInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    static ServerSocket ss = null;
    static Socket cs = null;
    static boolean listening = true;
    
    static final int MAXCLIENTS = 10;
    static SClientThread[] clients = new SClientThread[MAXCLIENTS];
    static ArrayList<UserInfo> userDB = new ArrayList<>();
    
    static int guestCount = 1;
    static final String USERDB_FILENAME = "userDB.txt";
    static String chatDirectory = "C:\\ChatApp";
    static ArrayList<Group> groups = new ArrayList<>();
    static String[] cmds = new String[]{
        "CMD: /reg [username] [password] [password]\t\t-> Signup user.",
        "CMD: /log [username] [password]\t\t-> Login user.",
        "CMD: /users \t\t-> List all users connected.",
        "CMD: /files \t\t-> List all files from users connected.",
        "CMD: /dl [username] [file]\t\t-> Download one or more files from one user.",
        "CMD: /quit or /logout \t-> Logout and leaves chat."};

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        chatDirectory = new File("").getAbsolutePath();
        
        loadFile();
        
        try {
            ss = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            while (listening) {
                cs = ss.accept();
                for(int i = 0; i < MAXCLIENTS; i++) {
                    if(clients[i] == null) {
                        (clients[i] = new SClientThread(cs, clients)).start();
                        break;
                    }
                }
            }

            cs.close();
            ss.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    static synchronized void saveToFile() throws IOException {
        System.out.println("Saving userDB!");
        try (FileOutputStream fos = new FileOutputStream(chatDirectory + "\\" + USERDB_FILENAME); ObjectOutputStream ow = new ObjectOutputStream(fos)) {
            ow.writeObject(userDB);
            ow.flush();
            ow.close();
            fos.close();
        }
    }
    
    
    private static void loadFile() throws IOException, ClassNotFoundException {
        System.out.println("Loading userDB!");
        try {
        FileInputStream fis = new FileInputStream(chatDirectory + "\\" + USERDB_FILENAME);
        ObjectInputStream in = new ObjectInputStream(fis);
        userDB = (ArrayList<UserInfo>)in.readObject();
        in.close();
        fis.close();
        } catch(Exception e) {
            System.out.println("Creating userDB file!");
                File f = new File(chatDirectory + "\\" + USERDB_FILENAME);
                f.createNewFile();
        }
    }
    
    static synchronized void createGroup(String groupName, String clientName) {
        ChatServer.groups.add(new Group(groupName, clientName));
        System.out.println("Created group " + groupName + ".");
    }
    
    static synchronized void deleteGroup(Group toDelete) {
        ChatServer.groups.remove(toDelete);
        System.out.println("Deleted group " + toDelete.getName() + ".");
    }
}
