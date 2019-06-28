package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.Map.Entry;
//import java.util.Properties;
//import java.io.FileReader;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import system.Comunication;
import system.EnumChannel;
import system.EnumServices;
import system.Offer;
import system.Product;
import system.Question;
import system.Sell;

public class View extends ReceiverAdapter implements RequestHandler{

	
	private static Scanner selectOption;
    private static String customer = "";
    private static String seller = "";

    
    //private static final String VIEW_PROPERTIES = "View.properties";
    //private static FileReader reader;
    //private static final Properties p = new Properties();
    //private static final String ACCEPT_CHAR = p.getProperty("ACCEPT_CHAR");
    
    // GENERAL OPTIONS:
    private static final String ACCEPT_CHAR = "s";
    private static final String CONTINUE_LOOP = "1";
    private static final String DECLINE_CHAR = "n";
    private static final String EMPTY_STRING = "";
    private static final String END_LOOP = "0";
    protected static final String EXIT_MENU = "sair";
    private static final int MAX_OF_ATTEMPTS = 3;

    // ACCESS SYSTEM OPTIONS:
    private static final String CREATE_ACCOUNT = "1";
    private static final String LOGIN_ACCOUNT = "2";
    private static final String EXIT_SYSTEM = "29";

    // CREATE ACCOUNT OPTIONS:
    private static final String LOGIN_COSTUMER = "1";
    private static final String LOGIN_SELLER = "2";

    // CUSTOMER ACCOUNT OPTIONS:
    private static final String LIST_PRODUCTS = "1";
    private static final String SEARCH_PRODUCT = "2";
    private static final String BUYING_LIST = "3";
    // CUSTOMER PRODUCT OPTIONS:
    private static final String MAKE_PURCHASE = "1";
    private static final String MAKE_QUESTION = "2";

    // SELLER ACCOUNT OPTIONS:
    private static final String ADD_PRODUCT = "1";
    private static final String ITEMS_SOLD = "2";
    private static final String ITEMS_FOR_SALE = "3";

    // JGROUPS
    private JChannel view_controlChannel;
    private static MessageDispatcher controlDispatcher;
    private static Vector<Address> enderecosControle;
    

    public View() throws Exception {
        enderecosControle = new Vector<Address>();
        try {
            //reader = new FileReader(VIEW_PROPERTIES);  
            //p.load(reader);
            // TODO corrigir acesso aos recursos
            this.view_controlChannel = new JChannel("view_control.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //this.viewDispatcher = new MessageDispatcher(this.viewChannel, null, null, (RequestHandler) this);
        controlDispatcher = new MessageDispatcher(this.view_controlChannel, null, null, this);
        this.view_controlChannel.setReceiver(this);
        // Tive que colocar o throws para nao ter que dar try catch abaixo
        this.view_controlChannel.connect("ViewControlChannel");
        this.montaGrupo();
        String loop = CONTINUE_LOOP;
        do {
            customer = null;
            seller = null;
            loop = accessSystem();
            if (customer != null) {
                loop = menuCustomer();
            } else if (seller != null) {
                loop = menuSeller();
            }
        } while(loop.equals(CONTINUE_LOOP));
        System.out.println("Adios");
    }
    

    private void montaGrupo() {
    	RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);
        Comunication comunication = new Comunication(EnumChannel.VIEW_TO_CONTROL,EnumServices.NEW_VIEW_MEMBER,null);
        Address cluster = null;
        //Message msg2 = new Message
        Message newMessage = new Message(cluster, comunication);
        RspList<Comunication> list = null;
		try {
			list = controlDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
        for(Rsp<Comunication> x : list) {
            if(x.getValue()!=null && x.getValue().channel == EnumChannel.CONTROL_TO_VIEW)
                enderecosControle.add(x.getSender());
        }
    	return;
    }
    
    
    private static String accessSystem() {
        customer = null;
        seller = null;
        String option = CREATE_ACCOUNT;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            SystemMessage.clearScreen();
            if (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT)) {
                ErrorMessage.invalidOptionMessage();
            }
            System.out.println("=== Bem vindx ao SD e-Commerce ===");
            System.out.println(CREATE_ACCOUNT + " - Para criar conta");
            System.out.println(LOGIN_ACCOUNT + " - Para realizar login");
            System.out.println(EXIT_SYSTEM + " - Sair do sistema");
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(CREATE_ACCOUNT)) {
                loop = createAccount();
            } else if (option.equals(LOGIN_ACCOUNT)) {
                loop = login();
            } else if (option.equals(EXIT_SYSTEM)) {
                return END_LOOP;
            }
        } while (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT) || loop.equals(CONTINUE_LOOP));
        return CONTINUE_LOOP;
    }

    
    private static String createAccount() {
        Boolean userNotExist = true;
        Boolean passwordMatch = true;
        do {
            SystemMessage.clearScreen();
            if (!passwordMatch) {
                ErrorMessage.passwordConfirmErrorMessage();
            }
            if (!userNotExist) {
                ErrorMessage.userAlreadyExistErrorMessage();
            }
            passwordMatch = true;
            System.out.println("========== CRIANDO CONTA ==========");
            SystemMessage.backMessage();
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            if (nickname.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            }
            System.out.println("Nome completo: ");
            String name = selectOption.nextLine();
            System.out.println("Senha: ");
            String password = selectOption.nextLine();
            System.out.println("Confirmar senha: ");
            String confirmPassword = selectOption.nextLine();
            System.out.println("Você é um vendedor?(" + ACCEPT_CHAR + "/" + DECLINE_CHAR + "): ");
            selectOption = new Scanner(System.in);
            String isSellerString = selectOption.nextLine();
            EnumServices isSeller = EnumServices.CREATE_CUSTOMER;
            if (isSellerString.toLowerCase().equals(ACCEPT_CHAR)) {
                isSeller = EnumServices.CREATE_SELLER;
            }
            ArrayList<Object> content = new ArrayList<Object>();
            content.add(nickname);
            content.add(name);
            content.add(password);
            Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, isSeller, content);
            newComunication = sendMessage(newComunication);
            userNotExist = (Boolean)newComunication.content.get(0);
            if (!password.equals(confirmPassword)) {
                passwordMatch = false;
            } else if (userNotExist && passwordMatch) {
                SystemMessage.userCreationSuccessMessage();
                SystemMessage.pressEnterMessage();
            }
        } while (!userNotExist || !passwordMatch);
        return CONTINUE_LOOP;
    }


    /*
     * ============================================================= INICIO =============================================================
     * ================================================ LOGIN CUSTOMER AND LOGIN SELLER =================================================
     * ==================================================================================================================================
     */
    

    private static String login() {
        String option = LOGIN_COSTUMER;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            SystemMessage.clearScreen();
            if (!option.equals(LOGIN_COSTUMER) && !option.equals(LOGIN_SELLER)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("========= ACESSAR CONTA =========");
            SystemMessage.backMessage();
            System.out.println(LOGIN_COSTUMER + " - Acessar conta de consumidor");
            System.out.println(LOGIN_SELLER + " - Acessar conta de vendedor");
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            } else if (option.equals(LOGIN_COSTUMER)) {
                loop = loginCustomer();
            } else if (option.equals(LOGIN_SELLER)) {
                loop = loginSeller();
            }
        } while (!option.equals(LOGIN_COSTUMER) && !option.equals(LOGIN_SELLER) || loop.equals(CONTINUE_LOOP));
        return END_LOOP;
    }
    

    private static String loginCustomer() {
        Boolean confirmLogin = true;
        do {
            SystemMessage.clearScreen();
            if (!confirmLogin) {
                ErrorMessage.loginErrorMessage();
            }
            System.out.println("== ACESSAR CONTA DE CONSUMIDOR ==");
            SystemMessage.backMessage();
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            if (nickname.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            }
            System.out.println("Senha: ");
            selectOption = new Scanner(System.in);
            String password = selectOption.nextLine();
            ArrayList<Object> content = new ArrayList<Object>();
            content.add(nickname);
            content.add(password);
            Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.LOGIN_CUSTOMER, content);
            newComunication = sendMessage(newComunication);
            confirmLogin = (Boolean)newComunication.content.get(0);
            if (confirmLogin) {
                customer = nickname;
            }
        } while (!confirmLogin);
        return END_LOOP;
    }
    

    private static String loginSeller() {
        Boolean confirmLogin = true;
        do {
            SystemMessage.clearScreen();
            if (!confirmLogin) {
                ErrorMessage.loginErrorMessage();
            }
            System.out.println("=== ACESSAR CONTA DE VENDEDOR ===");
            SystemMessage.backMessage();
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            if (nickname.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            }
            System.out.println("Senha: ");
            selectOption = new Scanner(System.in);
            String password = selectOption.nextLine();
            ArrayList<Object> content = new ArrayList<Object>();
            content.clear();
            content.add(nickname);
            content.add(password);
            Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.LOGIN_SELLER, content);
            newComunication = sendMessage(newComunication);
            confirmLogin = (Boolean)newComunication.content.get(0);
            if (confirmLogin) {
                seller = nickname;
            }
        } while (!confirmLogin);
        return END_LOOP;
    }


    /*
     * ============================================================== FIM ===============================================================
     * ================================================ LOGIN CUSTOMER AND LOGIN SELLER =================================================
     * ==================================================================================================================================
     */
    
    /*
     * ============================================================= INICIO =============================================================
     * ================================================= MENU CUSTOMER AND MENU SELLER ==================================================
     * ==================================================================================================================================
     */ 

    private static String menuCustomer() {
        String option = LIST_PRODUCTS;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            SystemMessage.clearScreen();
            if (!option.equals(LIST_PRODUCTS) && !option.equals(SEARCH_PRODUCT) && !option.equals(BUYING_LIST)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("=========== CONSUMIDOR ===========");
            System.out.println("Bem vindo " + customer);
            System.out.println(LIST_PRODUCTS + " - Listar todos produtos");
            System.out.println(SEARCH_PRODUCT + " - Buscar produto");
            System.out.println(BUYING_LIST + " - Listar compras realizadas");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            } else if (option.equals(LIST_PRODUCTS)) {
                loop = listAllProducts();
            } else if (option.equals(SEARCH_PRODUCT)) {
                loop = searchProduct();
            } else if (option.equals(BUYING_LIST)) {
                loop = buyingList();
            }
        } while (!option.equals(LIST_PRODUCTS) && !option.equals(SEARCH_PRODUCT) && !option.equals(BUYING_LIST) || loop.equals(CONTINUE_LOOP));
        return CONTINUE_LOOP;
    }
    

    private static String menuSeller() {
        String option = ADD_PRODUCT;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            SystemMessage.clearScreen();
            if (!option.equals(ADD_PRODUCT) && !option.equals(ITEMS_SOLD) && !option.equals(ITEMS_FOR_SALE)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("============ VENDEDOR ============");
            System.out.println("Bem vindo " + seller);
            System.out.println(ADD_PRODUCT + " - Adicionar produto");
            System.out.println(ITEMS_SOLD + " - Listar itens vendidos");
            System.out.println(ITEMS_FOR_SALE + " - Listar itens à venda");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            } else if (option.equals(ADD_PRODUCT)) {
                loop = addProduct();
            } else if (option.equals(ITEMS_SOLD)) {
                loop = productsSold();
            } else if (option.equals(ITEMS_FOR_SALE)) {
                loop = productsForSale();
            }
        } while (!option.equals(ADD_PRODUCT) && !option.equals(ITEMS_SOLD) && !option.equals(ITEMS_FOR_SALE) || loop.equals(CONTINUE_LOOP));
        return CONTINUE_LOOP;
    }

    /*
     * =============================================================== FIM ==============================================================
     * ================================================= MENU CUSTOMER AND MENU SELLER ==================================================
     * ==================================================================================================================================
     */
    
    /*
     * ============================================================= INICIO =============================================================
     * ======================================================= SELLER FUNCTIONS =========================================================
     * ==================================================================================================================================
     */ 
    private static String addProduct(){
        // int add_product(String idSeller, String product, Float price, long amount, String description)
        ArrayList<Object> content = new ArrayList<Object>();
        float price = 0;
        int attempts = 0;
        int idReturn = 0;
        long amount = 0;
        String loop = CONTINUE_LOOP;
        String productDescription;
        String productName;
        do{
            if(idReturn != 0){
                ErrorMessage.addProductErrorMessage(idReturn);
            }
            content.clear();
            content.add(seller);
            System.out.println("Informe o nome do produto: ");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            productName = selectOption.next();
            if(productName.equals(EXIT_MENU)){
                return CONTINUE_LOOP;
            }
            content.add(productName);
            System.out.println("Preço do produto: ");
            selectOption = new Scanner(System.in);
            price = selectOption.nextFloat();
            content.add(price);
            System.out.println("Quantia a ser vendida: ");
            selectOption = new Scanner(System.in);
            amount = selectOption.nextLong();
            content.add(amount);
            System.out.println("Uma breve descrição do produto: ");
            selectOption = new Scanner(System.in);
            productDescription = selectOption.next();
            content.add(productDescription);
            Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.ADD_ITEM, content);
            while ((loop.equals(CONTINUE_LOOP)) && (attempts < MAX_OF_ATTEMPTS)){
                try {
                    newComunication = sendMessage(newComunication);
                    idReturn = (int)newComunication.content.get(0);
                    loop = END_LOOP;
                } catch(Exception e) {
                    attempts++;
                    loop = CONTINUE_LOOP;
                }
            }
        } while(idReturn != 0);
        return CONTINUE_LOOP;
    }


    private static void printProductsForSaleList(HashMap<String, Product> itemsForSale){
        /*
         * TODO
         */
    }


    private static void printProductsSoldList(ArrayList<Sell> itemsForSale){
        System.out.println("================ LISTA DE PRODUTOS VENDIDOS =================");
        for(int i = 0; i < itemsForSale.size(); i++){
            System.out.println("Produto: " + itemsForSale.get(i).productId);
            System.out.println("Comprador: " + itemsForSale.get(i).customerId);
            System.out.println("Quantia comprada: " + itemsForSale.get(i).quant);
            System.out.println("Valor: " + itemsForSale.get(i).price);
            System.out.println("Total: " + (itemsForSale.get(i).quant * itemsForSale.get(i).price));
            System.out.println("=============================================================");
        }
        SystemMessage.pressEnterMessage();
    }


    private static String productsForSale(){
        /*
         * TODO
         */
        return CONTINUE_LOOP;
    }


    private static String productsSold(){
        // TODO
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(seller);
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.SOLD_ITENS, content);
        newComunication = sendMessage(newComunication);
        ArrayList<Sell> itemsForSale = (ArrayList<Sell>)newComunication.content.get(0);
        if(itemsForSale.size() == 0){
            ErrorMessage.noItemsFound();
            SystemMessage.pressEnterMessage();
            return CONTINUE_LOOP;
        }
        printProductsSoldList(itemsForSale);
        return CONTINUE_LOOP;
    }


    /*
     * ============================================================== FIM ===============================================================
     * ======================================================= SELLER FUNCTIONS =========================================================
     * ==================================================================================================================================
     */ 


    private static String buyingList() {
    	ArrayList<Object> content = new ArrayList<Object>();
    	content.add(customer);
    	Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.BOUGHT_ITENS, content);
        newComunication = sendMessage(newComunication);
        @SuppressWarnings("unchecked")
        ArrayList<Sell> buyingList = (ArrayList<Sell>) newComunication.content.get(0);
        System.out.println("===================== LISTA DE COMPRAS ======================");
        if(buyingList.size() < 0){
            ErrorMessage.noItemsFound();
            SystemMessage.pressEnterMessage();
            return CONTINUE_LOOP;
        }
        for (int i = 0; i < buyingList.size(); i++) {
            System.out.println("=============================================================");
            System.out.print("Produto: " + buyingList.get(i).productId);
            System.out.print("Vendedor: " + buyingList.get(i).sellerId);
            System.out.print("Quantia comprada: " + buyingList.get(i).quant);
            System.out.print("Valor: " + buyingList.get(i).price);
            System.out.println("Total: " + (buyingList.get(i).quant * buyingList.get(i).price));
            System.out.println("=============================================================");
        }
        SystemMessage.pressEnterMessage();
        return CONTINUE_LOOP;
    }
    
    
    private static ArrayList<Object> getListAllProducts() {
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.LIST_ITENS, null);
        newComunication = sendMessage(newComunication);
        @SuppressWarnings("unchecked")
        ArrayList<Object> listOfProducts = (ArrayList<Object>)newComunication.content.get(0);
        if(listOfProducts.size() == 0) {
            ErrorMessage.noItemsFound();
            SystemMessage.pressEnterMessage();
        }
        return listOfProducts;
    }

     
    private static String listAllProducts() {
        SystemMessage.clearScreen();
        ArrayList<Object> listOfProducts = getListAllProducts();
        if(listOfProducts.size() > 0) {
        	shopOptionsProduct(listOfProducts);
        }
        return CONTINUE_LOOP;
    }
    

    private static void printSpecificProductComplete(Product specificProduct) {
        System.out.println(specificProduct.id);
        System.out.println("Descrição: " + specificProduct.description);
        System.out.println("Quantia total disponivel: " + specificProduct.count);
        System.out.println("Ofertas disponiveis: ");
        for (Entry<String, Offer> entry : specificProduct.offers.entrySet()) {
            System.out.print("Nome do vendedor: " + entry.getValue().id);
            System.out.print("\tPreço: " + entry.getValue().price);
            System.out.print("\tQuantia disponivel: " + entry.getValue().amount);
        }
        System.out.println("=========== FIM OFERTAS ===========");
        System.out.println("FAQ: ");
        for (Entry<String, Question> entry : specificProduct.questions.entrySet()) {
            System.out.print("Usuário: " + entry.getValue().userId);
            System.out.print("\tPergunta: " + entry.getValue().id);
        }
        System.out.println("============= FIM FAQ =============");
    }
    

    private static void printSpecificProductWithoutFAQ(Product specificProduct) {
        System.out.println(specificProduct.id);
        System.out.println("Descrição: "+specificProduct.description);
        System.out.println("Quantia total disponivel: "+specificProduct.count);
        System.out.println("Ofertas disponiveis: ");
        for (Entry<String, Offer> entry : specificProduct.offers.entrySet()) {
            System.out.print("Nome do vendedor: "+entry.getValue().id);
            System.out.print("\tPreço: "+entry.getValue().price);
            System.out.print("\tQuantia disponivel: "+entry.getValue().amount);
        }
        System.out.println("=========== FIM OFERTAS ===========");
    }
    

    private static void printSpecificProductWithoutOffers(Product specificProduct) {
        System.out.println(specificProduct.id);
        System.out.println("Descrição: "+specificProduct.description);
        System.out.println("Quantia total disponivel: "+specificProduct.count);
        System.out.println("FAQ: ");
        for (Entry<String, Question> entry : specificProduct.questions.entrySet()) {
            System.out.print("Usuário: "+entry.getValue().userId);
            System.out.print("\tPergunta: "+entry.getValue().id);
        }
        System.out.println("============= FIM FAQ =============");
    }
    
    
    private static void printProductList(ArrayList<Object> listOfProducts) {
        for(int i = 0; i < listOfProducts.size(); i++) {
            String index = String.valueOf(i);
            System.out.println(index+"\t-- " + listOfProducts.get(i));
        }
    }
    

    private static String searchProduct() {
    	/* 
         * Pesquisar produto:
         * ArrayList<Product> search_product(String string)
         */
        ArrayList<Object> content = new ArrayList<Object>();
        int amountBought;
        String option = EMPTY_STRING;
        String loop = CONTINUE_LOOP;
        String productName = EMPTY_STRING;  
        do {
            content.clear();
            System.out.println("Informe o nome do produto que deseja buscar: ");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            }
            productName = option;
            content.add(productName);
        } while(!option.equals(EMPTY_STRING));
        int attempts = 0;
        loop = CONTINUE_LOOP;
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.SEARCH_ITEM, content);
        while ((loop.equals(CONTINUE_LOOP)) && (attempts < MAX_OF_ATTEMPTS)){
        	try {
                newComunication = sendMessage(newComunication);
                loop = END_LOOP;
            } catch(Exception e) {
            	attempts++;
            }
        }

        ArrayList<Object> searchItems = (ArrayList<Object>)newComunication.content.get(0);
        if (searchItems.size() == 0){
            ErrorMessage.noItemsFound();
            SystemMessage.pressEnterMessage();
        } else {
        	printProductList(searchItems);
        	shopOptionsProduct(searchItems);
        }
        return CONTINUE_LOOP;
    }
    
    
    private static void shopOptionsProduct(ArrayList<Object> listOfProducts) {
    	Boolean productIndexExist = true;
        String option = ACCEPT_CHAR;
        String loop = CONTINUE_LOOP;
        do {
            if (!option.equals(ACCEPT_CHAR) && !option.equals(DECLINE_CHAR)) {
                ErrorMessage.invalidOptionMessage();
            } else if(!productIndexExist) {
                ErrorMessage.productIndexOutOfRangeMessage();
            }
            productIndexExist = true;
            printProductList(listOfProducts);
            System.out.println("Deseja comprar algum dos itens?(" + ACCEPT_CHAR + "/" + DECLINE_CHAR + "): ");
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if(option.equals(ACCEPT_CHAR)) {
                System.out.println("Informe o indice que aparece no inicio do produto: ");
                selectOption = new Scanner(System.in);
                int indexProduct = selectOption.nextInt();
                if(indexProduct < 0 || indexProduct >= listOfProducts.size()) {
                    productIndexExist = false;
                } else{
                    Product specificProduct = (Product)listOfProducts.get(indexProduct);
                    loop = specificProductScreen(specificProduct);
                }
            }
        } while(!option.equals(ACCEPT_CHAR) && !option.equals(DECLINE_CHAR) && !productIndexExist || loop.equals(CONTINUE_LOOP));
    }
    

    private static String specificProductPurchase(Product specificProduct) {
        /* 
         * Realizar compra:
         * int purchase(String customer, String seller, String product, int amount)
         */
        ArrayList<Object> content = new ArrayList<Object>();
        int amountBought;
        String option = EMPTY_STRING;
        String loop = CONTINUE_LOOP;
        String sellerName = EMPTY_STRING;  
        do {
            content.clear();
            printSpecificProductWithoutFAQ(specificProduct);
            System.out.println("Informe o nome do vendedor do qual deseja comprar: ");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                return CONTINUE_LOOP;
            }
            sellerName = option;
            content.add(customer);
            content.add(sellerName);
            content.add(specificProduct.id);
            while(loop.equals(CONTINUE_LOOP)){
                try {
                    System.out.println("Informe a quantia que deseja comprar desse vendedor: ");
                    selectOption = new Scanner(System.in);
                    amountBought = selectOption.nextInt();
                    content.add(amountBought);
                    loop = END_LOOP;
                } catch (Exception e) {
                    loop = CONTINUE_LOOP;
                }
            }
        } while(!option.equals(EMPTY_STRING) && loop.equals(CONTINUE_LOOP));
        int attempts = 0;
        loop = CONTINUE_LOOP;
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.BUY_ITEM, content);
        while ((loop.equals(CONTINUE_LOOP)) && (attempts < MAX_OF_ATTEMPTS)){
        	try {
                newComunication = sendMessage(newComunication);
                loop = END_LOOP;
            } catch(Exception e) {
            	attempts++;
            }
        }
        // =============================================
        return CONTINUE_LOOP;
    }
    

    private static String specificProductQuestion(Product specificProduct) {
        /* 
         * Realizar pergunta:
         * boolean sendQuestion(String customer, String product, String msg)
         */
        ArrayList<Object> content = new ArrayList<Object>();
        String option = EMPTY_STRING;
        String question = EMPTY_STRING;
        do {
            content.clear();
            content.add(customer);
            content.add(specificProduct.id);
            printSpecificProductWithoutOffers(specificProduct);
            System.out.println("Escreva sua pergunta: ");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
            }
            question = option;
            content.add(question);
        } while(!option.equals(EMPTY_STRING));
        int attempts = 0;
        String loop = CONTINUE_LOOP;
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.MAKE_QUESTION, content);
        while ((loop.equals(CONTINUE_LOOP)) && (attempts < MAX_OF_ATTEMPTS)){
        	try {
                newComunication = sendMessage(newComunication);
                loop = END_LOOP;
            } catch(Exception e) {
            	attempts++;
            }
        }
        // =============================================
        return CONTINUE_LOOP;
    }
    

    private static String specificProductScreen(Product specificProduct) {
        String option = MAKE_PURCHASE;
        String loop = CONTINUE_LOOP;
        do {
            printSpecificProductComplete(specificProduct);
            System.out.println(MAKE_PURCHASE+" - Realizar compra");
            System.out.println(MAKE_QUESTION+" - Realizar pergunta");
            SystemMessage.backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
            } else if(option.equals(MAKE_PURCHASE)) {
                loop = specificProductPurchase(specificProduct);
            } else if(option.equals(MAKE_QUESTION)) {
                loop = specificProductQuestion(specificProduct);
            }
        } while(!option.equals(MAKE_PURCHASE) && !option.equals(MAKE_QUESTION) || loop.equals(CONTINUE_LOOP));
        return CONTINUE_LOOP;
    }
    

    private static Comunication sendMessage(Comunication comunication) {
    	Vector<Address> cluster = enderecosControle;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_FIRST);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(true);
        Message newMessage = new Message(null, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = controlDispatcher.castMessage(cluster, newMessage, optinsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseComunication.getFirst();
    }


    // responde requisições recebidas
    @Override
    public Object handle(Message message) throws Exception{
        /* 
         * No trabalho, vocês deverão verificar qual o tipo de mensagem requisitativa
         * chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
         * Message poderia ser um registro contendo vários campos, para facilitar
         */
    	Comunication msg = (Comunication) message.getObject();
    	System.out.println("Chegou a mensagem pra visão:" + (Comunication)msg);
    	Comunication response = new Comunication();
    	//ArrayList<Object> content = new ArrayList<Object>();
    	if(msg.channel == EnumChannel.VIEW_TO_CONTROL) {
    		if(msg.service == EnumServices.NEW_VIEW_MEMBER) {
        		response.channel = EnumChannel.VIEW_TO_VIEW;
        		response.service = EnumServices.NEW_VIEW_MEMBER;
        		response.content = null;
        	}	
    	} else if(msg.channel == EnumChannel.CONTROL_TO_VIEW) {
    		if(msg.service == EnumServices.NEW_CONTROL_MEMBER) {
    			enderecosControle.add(message.getSrc());
    			return null;
    		}	
    	}
    	return response;
    }
    
    
}
