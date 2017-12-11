package DataBase;

import FileWork.FileWork;
import java.sql.*;

public class DataBase {
    private Connection connection;
    private PreparedStatement pst;
    private ResultSet rSet;

    private FileWork fileWork;

    public DataBase(FileWork fileWork){
        this.fileWork = fileWork;
    }

    public boolean loginRequest(String username, String password) {
        connect();
        try {
            pst = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            pst.setString(1, username);
            rSet = pst.executeQuery();
            if (!rSet.next()) {
                System.out.println("Такого юзера не существует");
            } else {
                String passFromDB = rSet.getString("password");
                if (password.equals(passFromDB)) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    public boolean regRequrst(String username, String password) {
        connect();
        try { pst = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            pst.setString(1, username);
            rSet = pst.executeQuery();

            if (!rSet.next()) {
                //Создаю имя папки для пользователя такое же как аккаунт, чтобы легче было добраться до папки
                //Имя всегда уникальное, в дальнейшем возможно поменять
                pst = connection.prepareStatement("INSERT INTO users (username, password, folder) VALUES (?, ?, ?)");
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, username);
                int newUserResult = pst.executeUpdate();

                Statement st = connection.createStatement();
                st.execute("CREATE TABLE IF NOT EXISTS " + username + " (\n" +
                        "    id       INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                        "                     UNIQUE\n" +
                        "                     NOT NULL,\n" +
                        "    filename TEXT    UNIQUE\n" +
                        "                     NOT NULL,\n" +
                        "    filesize TEXT    NOT NULL\n" +
                        ");");

                if (newUserResult > 0 && fileWork.makeDir(username)){
                    return true;
                } else {
                    System.out.println("Рег: Не смог зарегистрировать. ");
                    return false;
                }
            } else {
                System.out.println("Рег: Данное имя уже есть в базе");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    public boolean addFileToDB(String username, String fileName, String fileSize){
        try {
            connect();
            pst = connection.prepareStatement("INSERT INTO " + username +" (filename, filesize) VALUES (?, ?)");
            pst.setString(1, fileName);
            pst.setString(2, fileSize);
            int addFileResult = pst.executeUpdate();

            if (addFileResult > 0) {
                System.out.println("БД: " + fileName + " - добавлена информация о файле в БД");
                return true;
            } else {
                System.out.println("БД: " + fileName + " - не смог добавить в Бд");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;

    }

    public String getTable(String userName){
        StringBuffer resultSB = new StringBuffer();
        connect();
        Statement st = null;
        try {
            st = connection.createStatement();
            ResultSet rstUserFiles = st.executeQuery("SELECT filename, filesize FROM martin");
            while (rstUserFiles.next()) {
                String fileName = rstUserFiles.getString(1);
                String fileSize = rstUserFiles.getString(2);

                resultSB.append(fileName);
                resultSB.append(":");
                resultSB.append(fileSize);
                resultSB.append(":");
            }
            rstUserFiles.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        return resultSB.toString();
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Server/WebSaverDB.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
