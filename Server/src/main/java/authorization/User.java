package authorization;

public class User {
    private String clientUserName;

    public User(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public String getClientUserName() {
        return clientUserName;
    }
}
