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
public class SellerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private static final String FILENAME = "sellers.json";

    private JSONObject data;
    private JSONObject sellers;

    public SellerDAO() {
        this.loadFile();
    }

    private void loadFile() {
        JSONParser parser = new JSONParser();
        try(Reader reader = new FileReader(FILENAME)) {
            this.data = (JSONObject) parser.parse(reader);
            this.sellers = (JSONObject) this.data.get("sellers");
        } catch(Exception e) {
            this.data = new JSONObject();
            this.sellers = new JSONObject();

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
                this.data.put("type", "sellers");
                this.data.put("createdAt", new Date().toString());
            }
            this.data.put("sellers", this.sellers);
            file.write(data.toJSONString());            
        } catch(Exception e) {
            this.data = new JSONObject();
            this.sellers = new JSONObject();
            /*
            System.err.println("Não foi possível escrever arquivo '" + FILENAME + "'");
            e.printStackTrace();
            */
            // TODO handle saveFile error
        }

    }

    public void add_funds(double funds, String sellerId) {
        Seller seller = (Seller) this.sellers.get(sellerId);
        seller.add_funds(funds);
        this.sellers.replace(sellerId, seller);
    }
        
    public void add_seller(Seller seller) {
        JSONObject sel = new JSONObject();
        JSONObject sellers = new JSONObject();

        sel.put("fullname", seller.fullname);
        sel.put("funds", seller.funds);
        sel.put("id", seller.id);
        sel.put("password", seller.password);
        sel.put("username", seller.username);

        for (Sell s : seller.getSells()) {
            sellers.put(s.productId, s);
        }
        sel.put("sells", sellers);
        this.sellers.put(seller.id, sel);
    }

    public Seller get_seller(String seller) {
        return (Seller) this.sellers.get(seller);
    }

    public List<Seller> get_sellers() {
        Iterator<String> keys = this.sellers.keySet().iterator();
        List<Seller> out = new ArrayList<Seller>();
        while (keys.hasNext()) {
            out.add((Seller) this.sellers.get(keys.next()));
        }
        return out;
    }

    public boolean exists(String id) {
        return this.sellers.containsKey(id);
    }
    
}