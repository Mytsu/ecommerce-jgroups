package system;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
public class ProductDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "products.json";

    private JSONObject data;
    private JSONObject products;

    public ProductDAO(){
        this.loadFile();
    }

    private void loadFile() {
        JSONParser parser = new JSONParser();
        try(Reader reader = new FileReader(FILENAME)) {
            this.data = (JSONObject) parser.parse(reader);
            this.products = (JSONObject) this.data.get("products");
        } catch(Exception e) {
            this.data = new JSONObject();
            this.products = new JSONObject();

            /*
            System.err.println("Não foi possível ler arquivo '" + FILENAME + "'");
            e.printStackTrace();
            */
            // TODO handle loadFile error
        }
    }

    public synchronized void saveFile() {
        try(FileWriter file = new FileWriter(FILENAME);) {
            if (!this.data.containsKey("type") || this.data.containsKey("createdAt")) {
                this.data.put("type", "products");
                this.data.put("createdAt", new Date().toString());
            }
            this.data.put("products", this.products);
            file.write(data.toJSONString());            
        } catch(Exception e) {
            System.err.println("Não foi possível escrever arquivo '" + FILENAME + "'");
            e.printStackTrace();
            // TODO handle saveFile error
        }
    }

    public void add_product(Product product) {
        this.products.put(product.id, product);        
    }

        
    public void add_customer(Product product) {
        JSONObject prod = new JSONObject();
        JSONObject questions = new JSONObject();
        JSONObject offers = new JSONObject();
        
        offers.putAll(product.getOffers());
        questions.putAll(product.getQuestions());

        prod.put("offers", offers);
        prod.put("questions", questions);
        this.products.put(product.id, prod);
    }

    public Product get_product(String product) {
        return (Product) this.products.get(product);
    }

    public List<Product> get_products() {
        Iterator<String> keys = this.products.keySet().iterator();
        List<Product> out = new ArrayList<Product>();
        while (keys.hasNext()) {
            out.add((Product) this.products.get(keys.next()));
        }
        return out;
    }

    public boolean exists(String id) {
        return this.products.containsKey(id);
    }

}