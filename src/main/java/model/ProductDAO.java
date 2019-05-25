package model;

import java.io.Serializable;
import java.util.HashMap;

class ProductDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private HashMap<String, Product> products;
    long count;

    ProductDAO(){
        this.products = new HashMap<String, Product>();
    }

    public void add_product(Product product) {
        this.count++;
        this.products.put(product.id, product);        
    }
}