package FileWork;

import java.io.File;

public class FileWork {
    private File file;

    public FileWork(){

    }

    public boolean makeDir(String folderName){
        File file = new File("Server/clients_folder/" + folderName);
        if (file.mkdir()){
            System.out.println("Успешно создал папку: " + folderName);
            return true;
        } else {
            System.out.println("Не смог создать папку: " + folderName);
            return false;
        }
    }
}
