import Network.ServerSocketThread;
import FileWork.FileWork;
import DataBase.DataBase;

public class Server {

    public static void main(String[] args) {
        FileWork fileWork = new FileWork();
        DataBase db = new DataBase(fileWork);
        ServerSocketThread serverSocketThread = new ServerSocketThread(db);
    }
}
