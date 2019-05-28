package model;

import java.io.Serializable;
import java.util.HashMap;

public class Seller extends User implements Serializable {

    private static final long serialVersionUID = -585749592930578838L;
    public HashMap<String, Product> products;

    public Seller(String id, String fullname, String password) {
        super(id, fullname, password);
        // funds remains as zero during creation
        this.products = new HashMap<String, Product>(); // 
    }
}
