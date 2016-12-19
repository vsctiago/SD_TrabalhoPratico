package ChatServerPackage;

public class ServerProtocol {

    private String[] msg = {"/help", "/quit", "/logout",
        "/reg", "/log",
        "/users", "/fupdate",
        "/files", "/myfiles"};

    public String processInput(String theInput) {
        String theOutput = null;

        if (theInput != null) {
            if (theInput.startsWith("/quit") || theInput.startsWith("/logout")) {
                //break;
            } else if (theInput.startsWith("/reg")) {
                String[] regparams = theInput.split("\\s+");
                SClientThread.userRegister(regparams);
            } else if (theInput.startsWith("/log")) {
                String[] regparams = theInput.split("\\s+");
                SClientThread.userLogin(regparams);
            } else if (theInput.equals("/help")) {
                //out.println("# Chat Commands: ");
                for (String item : ChatServer.cmds) {
                    //out.println(item);
                }
            } else if (theInput.equals("/users")) {
                //metodo para devolver T or F
                if (this.myInfo.isLogged()) {
                    listAllUsers();
                } else {
                    out.println("# Guests don't have access to this command.");
                }
            } else if (theInput.equals("/fupdate")) {
                if (this.myInfo.isLogged()) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i].myInfo.isLogged()) {
                            threads[i].out.println(msg);
                        }
                    }
                } else {
                    out.println("# Guests don't have access to this command.");
                }
            } else if (theInput.startsWith("/files")) {
                if (this.myInfo.isLogged()) {
                    //TODO: listar ficheiros de um utilizador
                } else {
                    out.println("# Guests don't have access to this command.");
                }
            } else if (theInput.startsWith("/myfiles")) {
                if (this.myInfo.isLogged()) {
                    //TODO: listar os meus ficheiros
                } else {
                    out.println("# Guests don't have access to this command.");
                }
            } else {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].out.println("<" + this.myInfo.getUsername() + ">: " + theInput);
                    }
                }
            }
        }

        return theOutput;
    }
}
