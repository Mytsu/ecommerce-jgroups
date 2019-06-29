package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "sellers.json";

    private Json filecontroller;

    private List<Seller> sellers;

    public SellerDAO() {
        this.filecontroller = new Json();
        this.sellers = new ArrayList<Seller>();
        this.loadFile();
    }

    private void loadFile() {
        this.filecontroller.readJson(FILENAME, sellers.getClass());        
    }

    public synchronized void saveFile() {
        this.filecontroller.writeJson(this, FILENAME);
    }

    public void add_funds(double funds, String sellerId) {
        for (Seller s : this.sellers) {
            if (s.id.equals(sellerId)) {
                s.funds += funds;
            }
        }
    }
        
    public void add_seller(Seller seller) {
        this.sellers.add(seller);
    }

    public Seller get_seller(String seller) {
        for (Seller s: this.sellers) {
            if (s.id.equals(seller)) {
                return s;
            }
        }
        return null;
    }

    public List<Seller> get_sellers() {
        return this.sellers;
    }

    public boolean exists(String id) {
        for (Seller s: this.sellers) {
            if (s.id.equals(id)) {
                return true;
            }
        }
        return false;
    }
    
}