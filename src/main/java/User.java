import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final UserWallet wallet;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new UserWallet();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserWallet getWallet() {
        return wallet;
    }
}