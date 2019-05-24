package model;

import java.io.Serializable;

class Costumer extends User implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Costumer(String username, String fullname, String password) {
        super(username, fullname, password);
    }
}