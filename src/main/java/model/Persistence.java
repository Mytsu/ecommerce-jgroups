package model;

import java.util.ArrayList;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;

import system.*;

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
    
    public void receive(Message msg) { //exibe mensagens recebidas
    	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem informativa
    		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
    		Message poderia ser um registro contendo vários campos, para facilitar
    	*/

    		// DEBUG: neste exemplo, iremos apenas imprimir a mensagem que chegou
            System.out.println("" + msg.getSrc() + ": " + msg.getObject()+"(1)");
        }
        

    /*
    // responde requisições recebidas
    public Object handle(Comunication msg) throws Exception{


        	
        	Comunication response = new Comunication();
        	
        	ArrayList<Object> content = new ArrayList<Object>();
        
        	if(msg.channel == EnumChannel.VIEW_TO_CONTROL){
        		
        		if (msg.service == EnumServices.ADD_ITEM){
        			// int add_product(String idSeller, String product, Float price, long amount, String description)
        			int var = this.add_product((String)msg.content.get(0), (String)msg.content.get(1), (float)msg.content.get(2),
        					(long)msg.content.get(3), (String)msg.content.get(4));
        			content.add(var);
        			response.service = EnumServices.ADD_ITEM;
        		}
        		
        		else if (msg.service == EnumServices.BUY_ITEM){
        			// int purchase(String customer, String seller, String product, int amount)
        			int var = this.purchase((String)msg.content.get(0), (String)msg.content.get(1),
        					(String)msg.content.get(2), (int)msg.content.get(3));
        			content.add(var);
        			response.service = EnumServices.BUY_ITEM;
        		}
        		
        		else if(msg.service == EnumServices.CREATE_CUSTOMER) {
        			//boolean add_customer(String id, String fullname, String password)
        			boolean var = this.add_customer((String)msg.content.get(0),(String)msg.content.get(1),
        					(String)msg.content.get(2));
        			content.add(var);
        			response.service = EnumServices.CREATE_CUSTOMER;
        		}
        		
        		else if(msg.service == EnumServices.CREATE_SELLER) {
        			//boolean add_seller(String id, String fullname, String password)
        			boolean var = this.add_seller((String)msg.content.get(0),(String)msg.content.get(1),
        					(String)msg.content.get(2));
        			content.add(var);
        			response.service = EnumServices.CREATE_CUSTOMER;    			
        		}
        		
        		else if(msg.service == EnumServices.LIST_ITENS) {
        			//ArrayList<Product> list_products()
        			ArrayList<Product> var = this.list_products();
        			content.add(var);
        			response.service = EnumServices.LIST_ITENS;
        		}
        		
        		else if(msg.service == EnumServices.LOGIN_CUSTOMER) {
        			//boolean login_customer(String customer, String password)
        			boolean var = this.login_customer((String)msg.content.get(0),(String)msg.content.get(1));
        			content.add(var);
        			response.service = EnumServices.LOGIN_CUSTOMER;
        		}
        		
        		else if(msg.service == EnumServices.LOGIN_SELLER) {
        			//boolean login_seller(String seller, String password)
        			boolean var = this.login_customer((String)msg.content.get(0),(String)msg.content.get(1));
        			content.add(var);
        			response.service = EnumServices.LOGIN_SELLER;    			
        		}
        		
        		else if(msg.service == EnumServices.MAKE_QUESTION){
        			//boolean sendQuestion(String customer, String product, String msg)
        			boolean var = this.sendQuestion((String)msg.content.get(0), (String)msg.content.get(1),
        					(String)msg.content.get(2));
        			content.add(var);
        			response.service = EnumServices.MAKE_QUESTION;
        		}
        		
        		else if(msg.service == EnumServices.SEARCH_ITEM) {
        			//ArrayList<Product> search_product(String string)
        			ArrayList<Product> var = this.search_product((String)msg.content.get(0));
        			content.add(var);
        			response.service = EnumServices.SEARCH_ITEM;
        		}
        		
        		else if(msg.service == EnumServices.SEND_ANSWER) {
        			//boolean sendAnswer(String seller, String product, String question, String answer)
        			boolean var = this.sendAnswer((String)msg.content.get(0), (String)msg.content.get(1),
        					(String)msg.content.get(2), (String)msg.content.get(3));
        			content.add(var);
        			response.service = EnumServices.SEND_ANSWER;
        		}
        		
        		else if(msg.service == EnumServices.TOTAL_FUNDS_BOOL) {
        			//boolean is_founds_right()
        			boolean var = this.is_founds_right();
        			content.add(var);
        			response.service = EnumServices.TOTAL_FUNDS_BOOL;
        		}
        		
        		response.channel = EnumChannel.CONTROL_TO_VIEW;
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

		*/


}
