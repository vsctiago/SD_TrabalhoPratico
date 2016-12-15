/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatPackage;

/**
 *
 * @author Tiago Fernandes
 */
public class UserInfo {
    
    private String username;
    private String password;
    private String directory;
    private boolean logged = false;

    public UserInfo() {}
    
    public UserInfo(String username) {
        this.username = username;
    }
    
    public UserInfo(String username, String password, boolean logged) {
        this.username = username;
        this.password = password;
        this.logged = logged;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    
    
    
}
