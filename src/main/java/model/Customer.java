package model;

import java.io.Serializable;

class Customer extends User implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Customer(String id, String fullname, String password) {
        super(id, fullname, password);
    }
}