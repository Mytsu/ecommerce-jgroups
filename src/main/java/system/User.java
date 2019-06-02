package system;

import java.util.ArrayList;

public class User {
    public final String id;
    public String username, fullname, password;
    public ArrayList<Sell> sell;
    public double funds;

    public User(String id, String fullname, String password) {
        this.id = id;
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
            + "\nUsername: " 
            + this.id 
            + "\nFull name: "
            + this.fullname
            + "\nFunds: "
            + this.funds;
    }
}