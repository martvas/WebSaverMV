import java.sql.*;

public class DataBase {
    private Connection connection;
    private Statement st;
    private PreparedStatement pst;
    private ResultSet rSet;

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
        try {
            String folderName = username + (int)(Math.random() * 1000);
            pst = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            pst.setString(1, username);
            rSet = pst.executeQuery();

            if (!rSet.next()) {
                pst = connection.prepareStatement("INSERT INTO users (username, password, folder) VALUES (?, ?, ?)");
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, folderName);
                int i = pst.executeUpdate();
                if (i > 0){
                    return true;
                } else {
                    System.out.println("Рег: Не смог зарегистрировать. ");
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
