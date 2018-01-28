package file;

import authorization.User;
import library.ServiceMessage;
import network.ClientServiceThread;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class ClientFile extends WebSaverFile {
    public static final String DIRECTORY_WITH_CLIENTS_FOLDERS = "Server/clients_folder/";

    private ClientServiceThread clientServiceThread;
    private volatile String nextFileName;
    private volatile String nextFileHash;
    private String clientUserName;

    public ClientFile(ClientServiceThread clientServiceThread, User user) {
        this.clientServiceThread = clientServiceThread;
        this.clientUserName = user.getClientUserName();
    }

    public static boolean makeDir(String folderName) {
        Path path = Paths.get(DIRECTORY_WITH_CLIENTS_FOLDERS + folderName);
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

    public String getFileCatalog() {
        Path path = Paths.get(DIRECTORY_WITH_CLIENTS_FOLDERS + clientUserName);
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

    public synchronized void addFileToServerRequest(String fileName, String fileHash) {
        this.nextFileName = fileName;
        this.nextFileHash = fileHash;
    }

    public synchronized void tryToSaveFile(byte[] nextFileByteArr) {
        String directory = DIRECTORY_WITH_CLIENTS_FOLDERS + clientUserName + "/";
        if (saveFile(directory, nextFileName, nextFileByteArr, nextFileHash)) {
            System.out.println("Файл уcпешно сохранился " + nextFileName);
            clientServiceThread.sendResponse(ServiceMessage.UPDATE + ":" + getFileCatalog());
        } else {
            clientServiceThread.sendResponse(ServiceMessage.INFO + ":" + "Файл " + nextFileName + "не сохранен на сервере");
            clientServiceThread.sendResponse(ServiceMessage.UPDATE + ":" + getFileCatalog());
            System.out.println(nextFileName + "Не сохранился");
        }

        //Обнуляю название и хэш файла
        this.nextFileName = null;
        this.nextFileHash = null;
    }

    public synchronized void tryToDeleteFile(String filename) {
        if (deleteFile(filename)) {
            clientServiceThread.sendResponse(ServiceMessage.UPDATE + ":" + getFileCatalog());
        } else {
            clientServiceThread.sendResponse(ServiceMessage.INFO + ":" + "Файл " + filename + " не удален");
            clientServiceThread.sendResponse(ServiceMessage.UPDATE + ":" + getFileCatalog());
        }
    }

    public boolean deleteFile(String fileName) {
        Path path = Paths.get("Server/clients_folder/" + clientUserName + "/" + fileName);
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

    public synchronized void sendFileToClient(String fileName) {
        File file = new File(DIRECTORY_WITH_CLIENTS_FOLDERS + clientUserName + "/" + fileName);
        clientServiceThread.sendResponse(ServiceMessage.SAVE_FILE_INFO + ":" + fileName + ":" + getFileMd5Hash(file.toPath()));

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            clientServiceThread.sendFileInBytes(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
