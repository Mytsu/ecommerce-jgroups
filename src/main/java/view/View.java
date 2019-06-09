package view;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map.Entry;
//import java.util.Properties;
//import java.io.FileReader;

import javax.swing.text.AbstractDocument.Content;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import system.Comunication;
import system.EnumChannel;
import system.EnumServices;
import system.Offer;
import system.Product;
import system.Question;

public class View {

    /**
	 * Não sei se o arquivo .properties está funcionando
     * 
     * FAZENDO: listAllProducts
	 */
	
	private static Scanner selectOption;
    private static String customer = null;
    private static String seller = null;

    
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
    private static final String EXIT_MENU = "sair";
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


    private JChannel controlChannel;
    private JChannel viewChannel;
    //private static MessageDispatcher viewDispatcher;
    private static MessageDispatcher controlDispatcher;

    public View() {
        try {
            //reader = new FileReader(VIEW_PROPERTIES);  
            //p.load(reader);
            // TODO corrigir acesso aos recursos
            this.controlChannel = new JChannel("control.xml");
            this.viewChannel = new JChannel("view.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //this.viewDispatcher = new MessageDispatcher(this.viewChannel, null, null, (RequestHandler) this);
        controlDispatcher = new MessageDispatcher(this.viewChannel, null, null, (RequestHandler) this);

        this.controlChannel.setReceiver((Receiver) this);
        this.viewChannel.setReceiver((Receiver) this);

        do {
            accessSystem();
            if (customer != null) {
                menuCustomer();
            } else if (seller != null) {
                menuSeller();
            }
        } while(customer.equals(null) && seller.equals(null));
        System.out.println("Adios");
    }

    private static void accessSystem() {
        customer = null;
        seller = null;
        String option = CREATE_ACCOUNT;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            clearScreen();
            if (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT)) {
                invalidOptionMessage();
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
            }
        } while (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT) && !option.equals(EXIT_SYSTEM)
                || loop == END_LOOP);
    }

    private static void backMessage() {
        System.out.print("Para voltar ao menu anterior digite: " + EXIT_MENU);
    }

    private static String buyingList() {
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices., content);
        newComunication = sendMessage(newComunication);
        System.out.println("");
        a
        return CONTINUE_LOOP;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static String createAccount() {
        Boolean userNotExist = true;
        Boolean passwordMatch = true;
        do {
            clearScreen();
            if (!passwordMatch) {
                passwordConfirmErrorMessage();
            }
            if (!userNotExist) {
                userAlreadyExistErrorMessage();
            }
            passwordMatch = true;
            System.out.println("========== CRIANDO CONTA ==========");
            backMessage();
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
                userCreationSuccessMessage();
            }
        } while (!userNotExist || !passwordMatch);
        return CONTINUE_LOOP;
    }

    private static void invalidOptionMessage() {
        System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
    }

    private static String listAllProducts() {
        clearScreen();
        Boolean productIndexExist = true;
        ArrayList<Object> listOfProducts = printAndStoreProductList();
        if(listOfProducts.size() > 0) {
            String option = ACCEPT_CHAR;
            String loop = CONTINUE_LOOP;
            do {
                if (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT)) {
                    invalidOptionMessage();
                } else if(!productIndexExist) {
                    productIndexOutOfRangeMessage();
                }
                productIndexExist = true;
                listOfProducts = printAndStoreProductList();
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
            } while(!option.equals(ACCEPT_CHAR) && !option.equals(DECLINE_CHAR) && !productIndexExist || loop == END_LOOP);
        } else{
            listOfProductsEmptyMessage();
        }
        return CONTINUE_LOOP;
    }

    private static void listOfProductsEmptyMessage() {
        System.out.println("Nenhum produto foi anunciado até o momento!");
    }

    private static String login() {
        String option = LOGIN_COSTUMER;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            clearScreen();
            if (!option.equals(LOGIN_COSTUMER) && !option.equals(LOGIN_SELLER)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("========= ACESSAR CONTA =========");
            backMessage();
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
        } while (!option.equals(LOGIN_COSTUMER) && !option.equals(LOGIN_SELLER) || loop == END_LOOP);
        return END_LOOP;
    }

    private static String loginCustomer() {
        Boolean confirmLogin = true;
        do {
            clearScreen();
            if (!confirmLogin) {
                loginErrorMessage();
            }
            System.out.println("== ACESSAR CONTA DE CONSUMIDOR ==");
            backMessage();
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

    private static void loginErrorMessage() {
        System.out.println("Usuário/senha inválido(s)");
    }

    private static String loginSeller() {
        Boolean confirmLogin = true;
        do {
            clearScreen();
            if (!confirmLogin) {
                loginErrorMessage();
            }
            System.out.println("=== ACESSAR CONTA DE VENDEDOR ===");
            backMessage();
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

    private static void menuCustomer() {
        String option = LOGIN_COSTUMER;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            clearScreen();
            if (!option.equals(LOGIN_COSTUMER) && !option.equals(LOGIN_SELLER)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("=========== CONSUMIDOR ===========");
            System.out.println("Bem vindo " + customer);
            System.out.println(LIST_PRODUCTS + " - Listar todos produtos");
            System.out.println(SEARCH_PRODUCT + " - Buscar produto");
            System.out.println(BUYING_LIST + " - Listar compras realizadas");
            backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
            } else if (option.equals(LIST_PRODUCTS)) {
                loop = listAllProducts();
            } else if (option.equals(SEARCH_PRODUCT)) {
                loop = searchProduct();
            } else if (option.equals(BUYING_LIST)) {
                loop = buyingList();
            }
        } while (!option.equals(LIST_PRODUCTS) && !option.equals(SEARCH_PRODUCT) && !option.equals(BUYING_LIST) || loop == END_LOOP);
    }

    private static void menuSeller() {
        /*
         * PROCURAR O QUE COLOCAR DENTRO DESTE MENU!
         */
        System.out.println("");
    }

    private static void passwordConfirmErrorMessage() {
        System.out.println("As senhas informadas não são iguais!");
    }

    private static ArrayList<Object> printAndStoreProductList() {
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.LIST_ITENS, null);
        newComunication = sendMessage(newComunication);
        ArrayList<Object> listOfProducts = (ArrayList<Object>)newComunication.content;
        for(int i = 0; i < listOfProducts.size(); i++) {
            String index = String.valueOf(i);
            System.out.println(index+"\t-- " + listOfProducts.get(i));
        }
        return listOfProducts;
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

    private static void productIndexOutOfRangeMessage() {
        System.out.println("Index do produto não existe!");
    }

    private static String searchProduct() {
        System.out.println("");
        a
        return CONTINUE_LOOP;
    }

    private static Comunication sendMessage(Comunication comunication) {
        Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_FIRST);
        optinsResponse.setAnycasting(false);
        Message newMessage = new Message(cluster, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = controlDispatcher.castMessage(null, newMessage, optinsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseComunication.getFirst();
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
            backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
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
        } while(!option.equals(EMPTY_STRING) && loop.equals(END_LOOP));
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.BUY_ITEM, content);
        newComunication = sendMessage(newComunication);
        a
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
            backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
            }
            question = option;
            content.add(question);
        } while(!option.equals(EMPTY_STRING));
        Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.MAKE_QUESTION, content);
        newComunication = sendMessage(newComunication);
        a
        return CONTINUE_LOOP;
    }

    private static String specificProductScreen(Product specificProduct) {
        String option = MAKE_PURCHASE;
        String loop = CONTINUE_LOOP;
        do {
            printSpecificProductComplete(specificProduct);
            System.out.println(MAKE_PURCHASE+" - Realizar compra");
            System.out.println(MAKE_QUESTION+" - Realizar pergunta");
            backMessage();
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if (option.equals(EXIT_MENU)) {
                break;
            } else if(option.equals(MAKE_PURCHASE)) {
                loop = specificProductPurchase(specificProduct);
            } else if(option.equals(MAKE_QUESTION)) {
                loop = specificProductQuestion(specificProduct);
            }
        } while(!option.equals(MAKE_PURCHASE) && !option.equals(MAKE_QUESTION) || loop.equals(END_LOOP));
        return CONTINUE_LOOP;
    }

    private static void userAlreadyExistErrorMessage() {
        System.out.println("Usuário já existe, por favor escolha um diferente!");
    }

    private static void userCreationSuccessMessage() {
        System.out.println("Usuário criado com sucesso!");
    }

}
