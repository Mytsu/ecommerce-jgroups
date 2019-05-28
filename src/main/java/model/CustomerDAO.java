package model;

import java.io.Serializable;
import java.util.HashMap;

public class CustomerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    static double INITIALFUNDING = 1000.0;
    public HashMap<String, Customer> customers;
	public long num_customers;

    public CustomerDAO() {
        this.customers = new HashMap<String, Customer>();
        this.num_customers = 0;
    }

    public void add_customer(Customer customer) {
        this.customers.put(customer.id, customer);
        this.num_customers += 1;
    }

    public void add_funds(double funds, String customerId) {
        this.customers.get(customerId).add_funds(funds);
    }

    public Customer get_customer(String customer){
        return this.customers.get(customer);
    }

    public HashMap<String, Customer> get_customers(){
        return this.customers;
    }

    public boolean exists(String customer){
        if(this.customers.containsKey(customer))
            return true;
        return false;
    }

}