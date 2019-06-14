package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Vector;

import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import system.*;


public class Control extends ReceiverAdapter implements RequestHandler, Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    private static final double INITIALFUNDING = 1000.0;

    public CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    
    public JChannel view_controlChannel;
    public JChannel control_modelChannel;
    
    private MessageDispatcher control_viewDispatcher;
    private MessageDispatcher control_modelDispatcher;
    
    private Vector<Address> enderecosModelo;
    
    Control() throws Exception {
    	
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
        
        enderecosModelo = new Vector<Address>();
        
        try {
			this.view_controlChannel = new JChannel("view_control.xml");
			this.control_modelChannel = new JChannel("control_model.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        this.view_controlChannel.setReceiver(this);
        this.control_modelChannel.setReceiver(this);
        
        control_viewDispatcher = new MessageDispatcher(this.view_controlChannel, null, null, this);
        control_modelDispatcher = new MessageDispatcher(this.control_modelChannel, null, null, this);
        
        // Tive que colocar o throws para nao ter que dar try catch abaixo
        this.view_controlChannel.connect("ViewControlChannel");
        this.control_modelChannel.connect("ControlModelChannel");
        
        this.montaGrupo();
    }
    
    private void montaGrupo() {
    	
    	// Organiza todas as informações para mandar uma mensagem e obter todos os endereços do modelo
    	RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);
        
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.NEW_CONTROL_MEMBER, null);
        Address cluster = null;
        Message newMessage = new Message(cluster, comunication);

        RspList<Comunication> list = null;
		try {
			list = control_modelDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for(Rsp<Comunication> x : list) {
            if(x.getValue().channel == EnumChannel.MODEL_TO_CONTROL)
                enderecosModelo.add(x.getSender());
        }
        
        
        // Informa para todos os membros da visão que há um novo controle no pedaço e não anota nenhum endereço
        options = null;
        options = new RequestOptions();
        options.setMode(ResponseMode.GET_NONE);
        options.setAnycasting(false);
        
        comunication = null;
        comunication =  new Comunication(EnumChannel.CONTROL_TO_VIEW, EnumServices.NEW_CONTROL_MEMBER, null);
        cluster = null;
        newMessage = null;
        newMessage = new Message(cluster, comunication);
        
        try {
			this.control_viewDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return;
    }
  
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos clientes                                  //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    // Function to add a customer
    private boolean add_customer(String id, String fullname, String password) {

        // Verifica se o cliente existe
        /* OLD
        if(this.customers.exists(id))
            return false;
        */
        
    	// Checa se já existe esse cliente
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(id);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.CUSTOMER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        
        if((boolean)comunication.content.get(0) == true) {
        	return false;
        }
        
        // Adiciona ele realmente no modelo
        Customer customer = new Customer(id, fullname, password);
        customer.funds = INITIALFUNDING;
        
        content = null; content = new ArrayList<Object>();
        content.add(customer);
        
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_CUSTOMER, content);
        
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //o cliente foi adicionado com o sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        //this.customers.add_customer(customer); OLD
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        return bool;
    }

    private boolean login_customer(String customer, String password) {
        
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(customer);
        content.add(password);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.CONFIRM_LOGIN_CUSTOMER, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (boolean)comunication.content.get(0);
    }

    // Funcao feita para busca de produtos
    @SuppressWarnings("unchecked")
	private ArrayList<Product> search_product(String string) {
        
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(string);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.MAKE_SEARCH_ITEM, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (ArrayList<Product>)comunication.content.get(0);
    }

    private int purchase(String customer, String seller, String product, int amount) {

        //	Verifica se a quantidade não é negativa
        if(amount <= 0) {
            return -4;
        }
    	
        //	Faz a checagem sobre a compra poder ser feita
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(customer);
        content.add(seller);
        content.add(product);
        content.add(amount);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.POSSIBLE_MAKE_PURCHASE, content);
        comunication = this.sendMessageModelAny(comunication);
    	
    	if((int)comunication.content.get(0) != 0) {
    		return (int)comunication.content.get(0);
    	}
    	
    	
    	
    	//	Efetiva de fato a compra
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.MAKE_PURCHASE, content);
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //a compra com sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        if(bool) {
        	return 0;
        }
       	//	-7 seria onde deu algum erro na escrita
       	return -7;
    	
    }

    @SuppressWarnings("unchecked")
	public ArrayList<Product> list_products() {
    	
        ArrayList<Object> content = null;
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.GET_ITENS, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (ArrayList<Product>)comunication.content.get(0);  	
    }
    
    public boolean sendQuestion(String customer, String product, String msg) {
    	
    	boolean retorno = false;
    	
    	if(products.exists(product)) {
        	Question question = new Question(msg, customer);
        	retorno = products.get_product(product).add_question(question);
    	}
    	
    	return retorno;
    }
    
    public ArrayList<Sell> getBougthItens(String customer){
    	ArrayList<Sell> array = new ArrayList<Sell>();
    	
        // Verifica se o cliente existe
        if(this.customers.exists(customer)) {
            array = this.customers.get_customer(customer).sell;
        }
    	
        return array;
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
    	
    	if(products.get_product(product).add_answer(response)) {
    		return true;
    	}
    	
    	return retorno;
    }
    
    public ArrayList<Sell> getSoldItens(String seller){
    	ArrayList<Sell> array = new ArrayList<Sell>();
    	
        // Verifica se o cliente existe
        if(this.customers.exists(seller)) {
            array = this.sellers.get_seller(seller).sell;
        }
    	
        return array;
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

    public boolean is_founds_right() {
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
    @Override
    public Object handle(Message message) throws Exception{

	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem requisitativa
		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
		Message poderia ser um registro contendo vários campos, para facilitar
	*/
    	Comunication msg = (Comunication) message.getObject();
    	
    	Comunication response = new Comunication();
    	
    	ArrayList<Object> content = new ArrayList<Object>();
    
    	if(msg.channel == EnumChannel.VIEW_TO_CONTROL) {
    		
    		if (msg.service == EnumServices.ADD_ITEM) {
    			// int add_product(String idSeller, String product, Float price, long amount, String description)
    			int var = this.add_product((String)msg.content.get(0), (String)msg.content.get(1), (float)msg.content.get(2),
    					(long)msg.content.get(3), (String)msg.content.get(4));
    			content.add(var);
    			response.service = EnumServices.ADD_ITEM;
    		}
    		
    		else if (msg.service == EnumServices.BUY_ITEM) {
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
    		
    		else if(msg.service == EnumServices.MAKE_QUESTION) {
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
    		
    		else if(msg.service == EnumServices.BOUGHT_ITENS) {
    			//ArrayList<Sell> getBougthItens(String customer)
    			ArrayList<Sell> var = this.getBougthItens((String)msg.content.get(0));
    			content.add(var);
    			response.service = EnumServices.BOUGHT_ITENS;
    		}

    		// Resposta para quando um membro da visão manda um multicast avisando que é novo
    		//dai todos os membros do controle respondem para que ele adicione todos no seu vetor de endereços
    		else if(msg.service == EnumServices.NEW_VIEW_MEMBER) {
    			response.service = EnumServices.NEW_VIEW_MEMBER;
    			content = null;
    		}
    		
    		else if(msg.service == EnumServices.SOLD_ITENS) {
    			//ArrayList<Sell> getBougthItens(String customer)
    			ArrayList<Sell> var = this.getSoldItens((String)msg.content.get(0));
    			content.add(var);
    			response.service = EnumServices.SOLD_ITENS;
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
    	
    	else if(msg.channel == EnumChannel.CONTROL_TO_VIEW) {
    		
    		// Mensagem de quando um membro do controle manda para o grupo da visao falando que ele existe
    		//a mensagem vai acabar chegando para membros do controle que meio que ignoram a mesma
    		if(msg.service == EnumServices.NEW_CONTROL_MEMBER) {
    			response.service = EnumServices.NEW_CONTROL_MEMBER;
    			content = null;
    		}
    		
    		response.channel = EnumChannel.CONTROL_TO_CONTROL;
    		response.content = content;
    	}
    	
    	else if(msg.channel == EnumChannel.MODEL_TO_CONTROL) {
    		
    		// Multicast que o modelo vai dar e todos os controles precisam adicionar o endereço do modelo em questao
    		if(msg.service == EnumServices.NEW_MODEL_MEMBER) {
    			this.enderecosModelo.add(message.getSrc());
    			response.service = EnumServices.NEW_MODEL_MEMBER;
    			content = null;
    		}
    		
    		response.channel = EnumChannel.CONTROL_TO_MODEL;
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
    
    private Comunication sendMessageModelAny(Comunication comunication) {
    	Vector<Address> cluster = this.enderecosModelo;
    	//Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_FIRST);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(false);
        Message newMessage = new Message(null, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = control_modelDispatcher.castMessage(cluster, newMessage, optinsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return responseComunication.getFirst();
    }
    
    private RspList<Comunication> sendMessageModelAll(Comunication comunication){
    	
    	Vector<Address> cluster = this.enderecosModelo;
    	//Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_ALL);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(true);
        //Message newMessage = new Message(cluster, comunication);
        Message newMessage = new Message(null, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = control_modelDispatcher.castMessage(cluster, newMessage, optinsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return responseComunication;
    }
    
    private Comunication sendMessageViewAny(Comunication comunication, ResponseMode mode) {
    	// TODO Address cluster = enderecosControle;
    	Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(mode);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(true);
        Message newMessage = new Message(cluster, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = control_viewDispatcher.castMessage(null, newMessage, optinsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseComunication.getFirst();
    }

}