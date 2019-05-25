package model;

import java.io.Serializable;
import java.util.HashMap;

class CustomerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    static double INITIALFUNDING = 1000.0;
    final double TOTALFUNDINGINSYSTEM = 0;
    private HashMap<String, Customer> customers;

    CustomerDAO() {
        this.customers = new HashMap<String, Customer>();
    }

    public void add_customer(Customer customer) {
        this.customers.put(customer.id, customer);
    }

    public void add_funds(double funds, String customerId) {
        this.customers.get(customerId).add_funds(funds);
    }
}