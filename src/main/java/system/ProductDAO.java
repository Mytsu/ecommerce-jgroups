package system;

import java.io.Serializable;
import java.util.List;

public class ProductDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "products.json";

    private Json filecontroller;

    private List<Product> products;

    public ProductDAO() {
        this.filecontroller = new Json();
        this.loadFile();
    }

    private void loadFile() {
        this.filecontroller.readJson(FILENAME, products.getClass());
    }

    public synchronized void saveFile() {
        this.filecontroller.writeJson(products, FILENAME);
    }
        
    public void add_product(Product product) {
        this.products.add(product);
    }

    public Product get_product(String product) {
        for (Product p: products) {
            if(p.id.equals(product)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> get_products() {
        return this.products;
    }

    public boolean exists(String id) {
        for (Product p: products) {
            if(p.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

}