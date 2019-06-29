package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "customers.json";

    private Json filecontroller;

    private List<Customer> customers;

    static double INITIALFUNDING = 1000.0;
    // public HashMap<String, Customer> customers;
    public long num_customers;

    public CustomerDAO() {
        // this.customers = new HashMap<String, Customer>();
        this.filecontroller = new Json();
        customers = new ArrayList<Customer>();
        this.loadFile();
    }

    private void loadFile() {
        this.filecontroller.readJson(FILENAME, customers.getClass());
    }

    public synchronized void saveFile() {
        this.filecontroller.writeJson(customers, FILENAME);
    }
    
    public void add_customer(Customer customer) {
        this.customers.add(customer);
    }

    public void add_funds(double funds, String customerId) {
        for (Customer c : this.customers) {
            if (c.id.equals(customerId)) {
                c.funds += funds;
            }
        }
    }

    public Customer get_customer(String customerId) {
        for (Customer c : this.customers) {
            if (c.id.equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    public List<Customer> get_customers() {
        return this.customers;
    }

    public boolean exists(String id) {
        for (Customer c : this.customers) {
            if (c.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

}