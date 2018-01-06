import network.ServerSocketStart;
import file.ClientFile;
import database.DataBase;

/*
что сделать:
1. В clientServiceThread проверять clientName = на нуль
 */

public class Server {

    public static void main(String[] args) {
        ClientFile clientFile = new ClientFile();
        DataBase db = new DataBase();
        ServerSocketStart serverSocketStart = new ServerSocketStart(db, clientFile);
        serverSocketStart.searchClients();
    }
}
