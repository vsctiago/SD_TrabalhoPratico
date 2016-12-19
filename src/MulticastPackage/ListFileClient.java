package MulticastPackage;

import java.io.Serializable;
import java.util.ArrayList;

public class ListFileClient implements Serializable{

    private String clientName;
    private ArrayList<String> listFileNames = new ArrayList<>();

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<String> getListFileNames() {
        return listFileNames;
    }

    public void addFileName(String fileName) {
        this.listFileNames.add(fileName);
    }
    
}
