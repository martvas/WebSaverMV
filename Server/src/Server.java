public class Server {
    public static void main(String[] args) {
        DataBase db = new DataBase();
        ServerSocketThread serverSocketThread = new ServerSocketThread(db);
    }
}
