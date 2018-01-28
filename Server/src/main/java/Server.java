import authorization.Database;
import network.ServerSocketStart;

public class Server {
    public static void main(String[] args) {
        Database auth = new Database();
        ServerSocketStart serverSocketStart = new ServerSocketStart(auth);
        serverSocketStart.searchClients();
    }
}
