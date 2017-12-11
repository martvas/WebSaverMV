import DataBase.DataBase;
import FileWork.FileWork;
import Network.ServerSocketThread;

public class Server {

    /*

     */
    public static void main(String[] args) {
        FileWork fileWork = new FileWork();
        DataBase db = new DataBase(fileWork);
        ServerSocketThread serverSocketThread = new ServerSocketThread(db);
    }
}
