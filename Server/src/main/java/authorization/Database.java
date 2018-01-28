package authorization;

import java.sql.*;

public class Database {
    private Connection connection;
    private PreparedStatement pst;
    private ResultSet rSet;

    public boolean loginRequestToDb(String username, String password) {
        connect();
        try {
            pst = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            pst.setString(1, username);
            rSet = pst.executeQuery();
            if (!rSet.next()) {
                System.out.println("Такого юзера не существует");
                return false;
            } else {
                String passFromDB = rSet.getString("password");
                if (password.equals(passFromDB)) {

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    public boolean registrationRequestToDb(String username, String password) {
        connect();
        try { pst = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            pst.setString(1, username);
            rSet = pst.executeQuery();

            if (!rSet.next()) {
                pst = connection.prepareStatement("INSERT INTO users (username, password, folder) VALUES (?, ?, ?)");
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, username);
                int newUserResult = pst.executeUpdate();

                if (newUserResult > 0){
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

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Server/WebSaverDB.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
