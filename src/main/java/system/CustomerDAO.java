package system;

import java.io.FileReader;  
import java.io.FileWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
public class CustomerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "customers.json";

    private JSONObject data;
    private JSONObject customers;

    static double INITIALFUNDING = 1000.0;
    // public HashMap<String, Customer> customers;
    public long num_customers;

    public CustomerDAO() {
        // this.customers = new HashMap<String, Customer>();
        this.loadFile();
    }

    private void loadFile() {
        JSONParser parser = new JSONParser();
        try(Reader reader = new FileReader(FILENAME)) {
            this.data = (JSONObject) parser.parse(reader);
            this.customers = (JSONObject) this.data.get("customers");
        } catch(Exception e) {

            this.data = new JSONObject();
            this.customers = new JSONObject();

            /*
            System.err.println("Não foi possível ler arquivo '" + FILENAME + "'");
            e.printStackTrace();
            */
            // TODO handle loadFile error
        }
    }

    protected synchronized void saveFile() {
        try(FileWriter file = new FileWriter(FILENAME);) {
            if (!this.data.containsKey("type") || this.data.containsKey("createdAt")) {
                this.data.put("type", "customers");
                this.data.put("createdAt", new Date().toString());
            }
            this.data.put("customers", this.customers);
            file.write(data.toJSONString());            
        } catch(Exception e) {
            System.err.println("Não foi possível escrever arquivo '" + FILENAME + "'");
            e.printStackTrace();
            // TODO handle saveFile error
        }

    }
    
    public void add_customer(Customer customer) {
        this.customers.put(customer.id, customer);
    }

    public void add_funds(double funds, String customerId) {
        Customer customer = (Customer) this.customers.get(customerId);
        customer.add_funds(funds);
        this.customers.replace(customerId, customer);
    }

    public Customer get_customer(String customerId) {
        return (Customer) this.customers.get(customerId);
    }

    public List<Customer> get_customers() {
        Iterator<String> keys = this.customers.keySet().iterator();
        List<Customer> out = new ArrayList<Customer>();
        while (keys.hasNext()) {
            out.add((Customer) this.customers.get(keys.next()));
        }
        return out;
    }

    public boolean exists(String id) {
        return this.customers.containsKey(id);
    }

}