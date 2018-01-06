package file;

import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientFile {

    private static final String DIRECTORY_WITH_CLIENTS_FOLDERS = "Server/clients_folder/";

    public boolean makeDir(String newFolderName) {
        Path path = Paths.get(DIRECTORY_WITH_CLIENTS_FOLDERS + newFolderName);
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.printf("Не возможно создать директорию: %s. File Already Exists Exception: %s", path, e);
            return false;
        } catch (Exception e) {
            System.out.printf("Не возможно создать директорию: %s. Exception: %s", path, e);
            return false;
        }
        return true;
    }

    public boolean deleteFile(String folderName, String fileName) {
        Path path = Paths.get("Server/clients_folder/" + folderName + "/" + fileName);
        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            System.out.printf("%s: Файла не существует%n", path);
            return false;
        } catch (DirectoryNotEmptyException e) {
            System.out.printf("%s not empty%n", path);
            return false;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public String getFileCatalog(String folderName) {
        Path path = Paths.get(DIRECTORY_WITH_CLIENTS_FOLDERS + folderName);
        final StringBuilder strFiles = new StringBuilder();
        try {
            Files.walkFileTree(path, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    strFiles.append(file.getFileName().toString());
                    strFiles.append(":");
                    strFiles.append(Files.size(file));
                    strFiles.append(":");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strFiles.toString();
    }

    public boolean saveFileOnServer(String clientUserName, String fileName, byte[] fileByteArr, String hashcode) {
        String clientFolder = DIRECTORY_WITH_CLIENTS_FOLDERS + clientUserName + "/";
        Path targetPath = Paths.get(clientFolder + fileName);

        //Проверка есть ли файл с таким же названием
        int n = 1;
        String fileNameWoutExt = FilenameUtils.removeExtension(fileName);
        String fileExtension = FilenameUtils.getExtension(fileName);
        while (Files.exists(targetPath)) {
            targetPath = Paths.get(clientFolder + fileNameWoutExt + "(" + n + ")." + fileExtension);
            n++;
        }

        try {
            Files.write(targetPath, fileByteArr);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        //проверка Хэша
        if (hashcode.equals(getFileMd5Hash(clientUserName, fileName))) {
            System.out.println("Хэши одинаковые");
            return true;
        } else {
            System.out.println("Хэш не сошался");
            //Удаляю файл, который сохранил, если хэши не совпадают
            try {
                Files.delete(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public String getFileMd5Hash(String clientUserName, String fileName) {
        Path pathToFile = Paths.get(DIRECTORY_WITH_CLIENTS_FOLDERS + clientUserName + "/" + fileName);
        String md5HashStr = null;
        try {
            byte[] data = Files.readAllBytes(pathToFile);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            md5HashStr = DatatypeConverter.printHexBinary(digest);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5HashStr;
    }
}