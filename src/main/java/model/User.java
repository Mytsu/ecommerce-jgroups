package model;

abstract class User {
	String id, username, fullname, password;
    double funds;

    User(String username, String fullname, String password) {
        this.id = 0;
        this.username = username;
        this.fullname = fullname;
        this.password = password; // TODO Usar algoritmo Bcrypt para hashing
        this.funds = 0;
        this.sell = new ArrayList<Sell>();
    }

    public void add_funds(double funds){
        this.funds += funds;
    }

    public double get_funds(){
        return this.funds;
    }

    public add_sell(Sell sell){
        this.sell.add(sell);
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