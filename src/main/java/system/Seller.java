package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Seller implements Serializable {

    private static final long serialVersionUID = -585749592930578838L;
    public HashMap<String, Product> products;
    
    // Atributos comuns de cliente e vendedor
    public final String id;
    public String username, fullname, password;
    public ArrayList<Sell> sell;
    public double funds;

    public Seller(String id, String fullname, String password) {
        this.id = id;
        this.fullname = fullname;
        this.password = password; // TODO Usar algoritmo Bcrypt para hashing
        this.funds = 0;
        this.sell = new ArrayList<Sell>();
        // funds remains as zero during creation
        this.products = new HashMap<String, Product>(); // 
    }

    public void add_funds(double funds) {
        this.funds += funds;
    }

    public double get_funds() {
        return this.funds;
    }

    public void add_sell(Sell sell) {
        this.sell.add(sell);
    }

    public List<Sell> getSells() {
        return this.sell;
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
