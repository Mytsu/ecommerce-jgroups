package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import org.jgroups.*;
import system.*;


public class Control implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    private static final double INITIALFUNDING = 1000.0;

    public CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    public JChannel controlChannel;
    public JChannel viewChannel;
    public JChannel modelChannel;
    
    Control(){
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
        
        try {
			this.controlChannel = new JChannel("control.xml");
			this.viewChannel = new JChannel("view.xml");
			this.modelChannel = new JChannel("model.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        this.controlChannel.setReceiver((Receiver) this);
        this.viewChannel.setReceiver((Receiver) this);
        this.modelChannel.setReceiver((Receiver) this);
    }
  
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos clientes                                  //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    // Function to add a customer
    public boolean add_customer(String id, String fullname, String password){

        Customer customer = new Customer(id, fullname, password);

        // Verifica se o cliente existe
        if(this.customers.exists(id))
            return false;
    
        customer.funds = INITIALFUNDING;
        this.customers.add_customer(customer);        
        return true;
    }

    public boolean login_customer(String customer, String password){
        // Verifica se o cliente existe
        if(!this.customers.exists(customer))
            return false;
        if(this.customers.get_customer(customer).password != password)
            return false;
        
        return true;
    }

    // Funcao feita para busca de produtos
    public ArrayList<Product> search_product(String string){
        
        ArrayList<Product> lista = new ArrayList<Product>();

        for (Entry<String, Product> entry : this.products.get_products().entrySet()) {
            String key = entry.getKey();
            if(key.contains(string)){
                Product prod = entry.getValue();
                lista.add(prod);
            }
        }
        return lista;
    }

    public int purchase(String customer, String seller, String product, int amount){

        // Verifica se o produto está no hashmap
        if(!this.products.exists(product)) {
            return -1;
        }

        // Verifica se o cliente existe
        if(!this.customers.exists(customer)) {
            return -2;
        }

        // Verifica se o vendedor existe
        if(!this.sellers.exists(seller)) {
            return -3;
        }

        // Verifica se a quantidade não é negativa
        if(amount <= 0) {
            return -4;
        }

        // Verifica se tem produtos o suficiente do vendedor X
        if(!this.products.get_product(product).has_enougth(seller, amount)) {
            return -5;
        }

        // Pega o preço do produto desta venda referente ao vendedor escolhido pelo cliente
        double price = this.products.get_product(product).get_price(seller);

        // Verifica se o cliente tem dinheiro o suficiente para comprar
        if(! (price > this.customers.get_customer(customer).get_funds())) {
            return -6;
        }

        // Incrementa o valor nos fundos do vendedor
        this.sellers.add_funds(price*amount, this.sellers.get_seller(seller));   
        // Deduz o valor nos fundos do cliente
        this.customers.add_funds(-price*amount, customer);

        // Deduz a quantidade do produto X do vendedor Y
        this.products.get_product(product).deduce_amount(seller, amount);
        Sell sell = new Sell(seller, customer, product, price, amount);
        this.customers.get_customer(customer).add_sell(sell);
        this.sellers.get_seller(seller).add_sell(sell);

        return 0;
    }

    public ArrayList<Product> list_products(){
    	
    	ArrayList<Product> lista = new ArrayList<Product>();
        for (Entry<String, Product> entry : this.products.get_products().entrySet()) {
        	lista.add(entry.getValue());
        }
        
        return lista;  	
    }
    
    public boolean sendQuestion(String customer, String product, String msg) {
    	
    	boolean retorno = false;
    	
    	if(products.exists(product)) {
        	Question question = new Question(msg, customer);
        	retorno = products.get_product(product).add_question(question);
    	}
    	
    	return retorno;
    }
    
       
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos vendedores                                //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    
    // Function to add a seller
    public boolean add_seller(String id, String fullname, String password) {
        
        // Verifica se o vendedor existe
        if(this.sellers.exists(id))
            return false;
        
        Seller seller = new Seller(id, fullname, password);
        this.sellers.add_seller(seller);

        return true;
    }

    public boolean login_seller(String seller, String password) {
        // Verifica se o cliente existe
        if(!this.sellers.exists(seller)) {
            return false;
        }

        if(this.sellers.get_seller(seller).password != password) {
            return false;
        }
        
        return true;
    }

    public int add_product(String idSeller, String product, Float price, long amount, String description) {
        //Checa se a quantidade e maior que 0
        if(amount <= 0) {
            return -1;
        }

        //Checa se o preco e maior que 0
        if(price <= 0) {
            return -2;
        }

        // Verifica se o vendedor existe
        if(!this.sellers.exists(idSeller)) {
            return -3;
        }

        // Verifica se o produto nao esta no hashmap
        if(!this.products.exists(product))
            products.add_product(new Product(product, description));

        //Decidir se vai incrementar caso o produto e o vendedor ja exista
        //Ou se simplesmente vai criar uma nova oferta
        products.get_product(product).add_offer(new Offer(idSeller, price, amount));

        return 0;
    }

    public boolean sendAnswer(String seller, String product, String question, String answer) {
    	
    	boolean retorno = false;
    	
    	if(!products.exists(product)) {
    		return false;
    	}
    	
    	Answer response = new Answer(seller, answer);
    	
    	if(products.get_product(product).add_answer(response)){
    		return true;
    	}
    	
    	return retorno;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte do sistema                                    //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public double get_total_founds() {
        
        double soma = 0.0;
        for (Entry<String, Customer> entry : this.customers.get_customers().entrySet()) {
            Customer custom = entry.getValue();
            soma += custom.get_funds();
        }
        
        for (Entry<String, Seller> entry : this.sellers.get_sellers().entrySet()) {
            Seller seller = entry.getValue();
            soma += seller.get_funds();
        }

        return soma;
    }

    public boolean is_founds_right(){
        if( this.customers.num_customers*1000 == this.get_total_founds())
            return true;
        return false;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Comunicacao                                         //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public void receive(Message msg) { //exibe mensagens recebidas
	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem informativa
		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
		Message poderia ser um registro contendo vários campos, para facilitar
	*/

		// DEBUG: neste exemplo, iremos apenas imprimir a mensagem que chegou
        System.out.println("" + msg.getSrc() + ": " + msg.getObject()+"(1)");
    }
    

    // responde requisições recebidas
    public Object handle(Comunication msg) throws Exception{

	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem requisitativa
		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
		Message poderia ser um registro contendo vários campos, para facilitar
	*/
    	
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

}