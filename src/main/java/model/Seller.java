package model;

import java.io.Serializable;
import java.util.ArrayList;

class Seller extends User implements Serializable {

    private static final long serialVersionUID = -585749592930578838L;

    Seller(String username, String fullname, String password) {
        super(username, fullname, password);
        // funds remains as zero during creation
        this.products = new ArrayList<Integer>();
    }
}
