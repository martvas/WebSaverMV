public class Server {

    /*
    Что сделать:
    1. После регистрации сделать создание таблицы под каждого клиента с файлами
    2.

     */
    public static void main(String[] args) {
        FileWork fileWork = new FileWork();
        DataBase db = new DataBase(fileWork);
        ServerSocketThread serverSocketThread = new ServerSocketThread(db);
    }
}
