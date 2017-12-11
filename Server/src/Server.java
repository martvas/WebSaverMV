import DataBase.DataBase;
import FileWork.FileWork;
import Network.ServerSocketThread;

public class Server {

    /*
    Сейчас:
    Клиент в новом потоке, создается директория, таблица, можно закинуть файл.

    Сделать:
    1. Отдавать информацию из БД клиенту о добавленном файле

     */
    public static void main(String[] args) {
        FileWork fileWork = new FileWork();
        DataBase db = new DataBase(fileWork);
        ServerSocketThread serverSocketThread = new ServerSocketThread(db);
    }
}
