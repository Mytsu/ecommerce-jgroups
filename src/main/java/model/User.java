package model;

abstract class User {
	String id, username, fullname, password;
    double funds;

    User(String id, String username, String fullname, String password) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.password = password; // TODO Use Bcrypt algorithm for hashing
        this.funds = 0;
    }

    @Override
    public String toString() {
        return "[USER]: "
            + this.id 
            + "\nUsername: " 
            + this.username
            + "\nFull name: "
            + this.fullname
            + "\nFunds: "
            + this.funds;
    }
}