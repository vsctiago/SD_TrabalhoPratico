package MulticastPackage;

import java.io.Serializable;

public class FileList implements Serializable{

    private String clientName;
    private String[] fileName;

    public FileList(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String[] getFileName() {
        return fileName;
    }

    public void setFileName(int pos, String fileName) {
        this.fileName[pos] = fileName;
    }
    
}
