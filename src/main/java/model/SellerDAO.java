package model;

import java.io.Serializable;
import java.util.HashMap;

class SellerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private HashMap<String, Seller> sellersList;

    SellerDAO(){
        this.sellersList = new HashMap<String, Seller>();
    }

    public void add_funds(double funds, Seller seller) {
        this.sellersList.get(seller.id).add_funds(funds);
    }

    public void add_seller(Seller seller) {
        this.sellersList.put(seller.id, seller);
    }

    public Seller get_seller(String seller){
        return this.sellersList.get(seller);
    }

    public boolean exists(String seller){
        if(this.sellersList.containsKey(seller))
            return true;
        return false;
    }
    
}