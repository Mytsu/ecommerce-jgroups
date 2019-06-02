package system;

import java.io.Serializable;

public class Customer extends User implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    public Customer(String id, String fullname, String password) {
        super(id, fullname, password);
    }
}