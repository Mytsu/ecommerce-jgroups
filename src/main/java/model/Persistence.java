package model;

import org.jgroups.JChannel;
import org.jgroups.Receiver;

import system.CustomerDAO;
import system.ProductDAO;
import system.SellerDAO;

public class Persistence {

    public CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    public JChannel controlChannel ;
    public JChannel modelChannel ;
    
    Persistence(){
    	try {
			this.controlChannel = new JChannel("control.xml");
			this.modelChannel = new JChannel("model.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        this.controlChannel.setReceiver((Receiver) this);
        this.modelChannel.setReceiver((Receiver) this);
    	
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
    }
}
