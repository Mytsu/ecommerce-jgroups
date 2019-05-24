package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class ProductDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    ProductDAO(){
        this.productsList = new ArrayList<Product>();
        this.idCurrentProduct = 0;
    }

    public void add_product(Product product){
        product.id = this.idCurrentProduct;
        this.idCurrentProduct++;
        this.productsList.add(product);        
    }
}