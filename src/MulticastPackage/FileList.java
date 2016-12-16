package MulticastPackage;

import java.io.Serializable;
import java.util.ArrayList;

public class FileList implements Serializable{

    private String clientName;
    private ArrayList<String> fileNames = new ArrayList<>();

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void addFileName(String fileName) {
        this.fileNames.add(fileName);
    }
    
}
