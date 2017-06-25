package chmapp17.chmapp.database;

public class UsersInfo {
    public String user_name, user_email, user_id;

    public UsersInfo() {
        // Default constructor
    }

    public UsersInfo(String user_name, String user_email, String user_id) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_id = user_id;
    }
}
