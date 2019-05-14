package model;

import java.io.Serializable;

class Seller extends User implements Serializable {

    private static final long serialVersionUID = -585749592930578838L;

    Seller(String id, String username, String fullname, String password) {
        super(id, username, fullname, password);
        // funds remains as zero during creation
    }
}
