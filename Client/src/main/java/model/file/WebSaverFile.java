package model.file;

import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WebSaverFile {

    public boolean saveFileOnComputer(String directory, String fileName, byte[] fileByteArr, String hashcode) {
        Path targetPath = Paths.get(directory + "/" + fileName);

        //Проверка есть ли файл с таким же названием
        int n = 1;
        String fileNameWoutExt = FilenameUtils.removeExtension(fileName);
        String fileExtension = FilenameUtils.getExtension(fileName);
        while (Files.exists(targetPath)) {
            targetPath = Paths.get(directory + "/" + fileNameWoutExt + "(" + n + ")." + fileExtension);
            n++;
        }

        try {
            Files.write(targetPath, fileByteArr);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //проверка Хэша
        if (hashcode.equals(getFileMd5Hash(targetPath))) {
            return true;
        } else {
            //Удаляю файл, который сохранил, если хэши не совпадают
            try {
                Files.delete(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public String getFileMd5Hash(Path path) {
        String md5HashStr = null;
        try {
            byte[] data = Files.readAllBytes(path);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            md5HashStr = DatatypeConverter.printHexBinary(digest);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5HashStr;
    }

}
