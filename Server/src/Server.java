import network.ServerSocketStart;
import fileWork.FileWork;
import database.DataBase;

/*
что сделать:
1. В clientServiceThread проверять clientName = на нуль
 */

public class Server {

    public static void main(String[] args) {
        FileWork fileWork = new FileWork();
        DataBase db = new DataBase(fileWork);
        ServerSocketStart serverSocketStart = new ServerSocketStart(db, fileWork);
        serverSocketStart.searchClients();
    }
}
