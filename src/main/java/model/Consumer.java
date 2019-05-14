package model;

import java.io.Serializable;

class Consumer extends User implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    
    static double INITIALFUNDING = 1000.0;

    Consumer(String id, String username, String fullname, String password) {
        super(id, username, fullname, password);
        this.funds = INITIALFUNDING;
    }
}