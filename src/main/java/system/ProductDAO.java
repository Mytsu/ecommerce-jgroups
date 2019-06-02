package system;

import java.io.Serializable;
import java.util.HashMap;

public class ProductDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private HashMap<String, Product> products;

    public ProductDAO(){
        this.products = new HashMap<String, Product>();
    }

    public void add_product(Product product) {
        this.products.put(product.id, product);        
    }

    public Product get_product(String product){
        return this.products.get(product);
    }

    public HashMap<String, Product> get_products(){
        return this.products;
    }

    public boolean exists(String product){
        if(this.products.containsKey(product))
            return true;
        return false;
    }

}