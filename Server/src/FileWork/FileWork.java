package FileWork;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

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

    public boolean deleteFile(String folderName, String fileName){
        Path path = Paths.get("Server/clients_folder/" + folderName + "/" + fileName);
        try {
            Files.delete(path);
        }  catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
            return false;
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
            return false;
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
            return false;
        }
        return true;
    }
}
