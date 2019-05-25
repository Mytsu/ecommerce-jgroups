package model;

import java.io.Serializable;
import java.util.HashMap;

class SellerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private HashMap<String, Seller> sellersList;
    private HashMap<String, Customer> customersList;

    SellerDAO(){
        this.sellersList = new HashMap<String, Seller>();
    }

    public void add_funds(double funds, Customer customer){
        this.customersList.get(customer.id).add_funds(funds);
    }

    public void add_seller(Seller seller){
        this.sellersList.put(seller.id, seller);
    }
    
}