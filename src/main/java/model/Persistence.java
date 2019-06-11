package model;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;

import system.*;

public class Persistence {

    private CustomerDAO customers;
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
    
    public void receive(Message msg) { //exibe mensagens recebidas
    	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem informativa
    		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
    		Message poderia ser um registro contendo vários campos, para facilitar
    	*/

    		// DEBUG: neste exemplo, iremos apenas imprimir a mensagem que chegou
            System.out.println("" + msg.getSrc() + ": " + msg.getObject()+"(1)");
        }
        

    
    private HashMap<String, Product> getItens() {
    	return products.get_products();
    }
    
    private HashMap<String, Customer> getCustomers(){
    	return customers.get_customers();
    }
    
    private HashMap<String, Seller> getSellers() {
    	return sellers.get_sellers();
    }
    
    // responde requisições recebidas
    public Object handle(Comunication msg) throws Exception{

    	
        
    	Comunication response = new Comunication();
    		
    	ArrayList<Object> content = new ArrayList<Object>();
    	
    	if(msg.channel == EnumChannel.CONTROL_TO_MODEL) {
    		
    		if(msg.service == EnumServices.GET_ITENS) {
    			
    			response.service = EnumServices.GET_ITENS;
    			HashMap<String, Product> var = getItens();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_CUSTOMERS) {
    			
    			response.service = EnumServices.GET_CUSTOMERS;
    			HashMap<String, Customer> var = getCustomers();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_SELLERS) {
    			
    			response.service = EnumServices.GET_SELLERS;
    			HashMap<String, Seller> var = getSellers();
    			content.add(var);
    		}    		
    		
    		response.channel = EnumChannel.MODEL_TO_CONTROL;
    		response.content = content;
    	}      

    	  //DEBUG: neste exemplo, a Message contém apenas uma String 
          // contendo uma pergunta qualquer. 
          //String pergunta = (String) msg.getObject();
          //System.out.println("RECEBI uma mensagem: " + pergunta+"\n");
          //User usuario = new User("UserNameQQ1","NomeCompletoQQ1","PassWordQQ1",122.541);
          //if(pergunta.contains("concorda"))
          //    return pergunta;
            //return "SIM (1)"; //resposta à requisição contida na mensagem
          //else
          //  return " NÃO (1)";
        
        
        	return response;
        }

		
}
