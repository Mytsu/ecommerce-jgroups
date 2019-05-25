package model;

import java.util.ArrayList;

abstract class User {
    final String id;
    String username, fullname, password;
    ArrayList<Sell> sell;
    double funds;

    User(String id, String username, String fullname, String password) {
        this.id = id;
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

    public void add_sell(Sell sell){
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