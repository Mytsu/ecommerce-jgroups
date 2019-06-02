package model;

import system.CustomerDAO;
import system.ProductDAO;
import system.SellerDAO;

public class Persistence {

    public CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    
    Persistence(){
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
    }
}
