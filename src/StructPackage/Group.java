package StructPackage;

import java.util.ArrayList;

public class Group {
    
    private String name;
    private ArrayList<String> clients;

    public Group(String name, String clientName) {
        this.name = name;
        this.clients = new ArrayList<>();
        this.clients.add(clientName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getClients() {
        return clients;
    }

    public void setClients(ArrayList<String> clients) {
        this.clients = clients;
    }
    
    public void add(String clientName) {
        this.clients.add(clientName);
    }
    
    public boolean remove(String clientName) {
        return this.clients.remove(clientName);
    }
    
    public int getClientsSize() {
        return this.clients.size();
    }
}
