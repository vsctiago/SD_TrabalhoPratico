package MulticastPackage;

import java.io.Serializable;
import java.util.ArrayList;

public class FileList implements Serializable{

    private String clientName;
    private ArrayList<String> fileName = new ArrayList<>();

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<String> getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.add(fileName);
    }
    
}
