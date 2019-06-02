package view;

import java.util.ArrayList;
import java.util.Scanner;

import system.*;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import system.User;

public class View {

    private static Scanner selectOption;
    private static String customer = null;
    private static String seller = null;

    private final static String ACCEPT_CHAR = "s";
    private final static String CONTINUE_LOOP = "1";
    private final static String CREATE_ACCOUNT = "1";
    private final static String DECLINE_CHAR = "n";
    private final static String END_LOOP = "0";
    private final static String EXIT_SYSTEM = "29";
    private final static String EXIT_MENU = "sair";
    private final static String LOGIN_ACCOUNT = "2";
    private final static String LOGIN_COSTUMER = "1";
    private final static String LOGIN_SELLER = "2";

    private JChannel controlChannel;
    private JChannel viewChannel;
    private static MessageDispatcher viewDispatcher;
    private static MessageDispatcher controlDispatcher;

    public View() {
        try {
            this.controlChannel = new JChannel("control.xml");
            this.viewChannel = new JChannel("view.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.viewDispatcher = new MessageDispatcher(this.viewChannel, null, null, (RequestHandler) this);
        this.controlDispatcher = new MessageDispatcher(this.viewChannel, null, null, (RequestHandler) this);

        this.controlChannel.setReceiver((Receiver) this);
        this.viewChannel.setReceiver((Receiver) this);

        accessSystem();
        if (customer != null) {
            menuCustomer();
        } else if (seller != null) {
            menuSeller();
        }
        System.out.println("Adios");
    }

    private static void accessSystem() {
        String option = CREATE_ACCOUNT;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
            clearScreen();
            if (!option.equals(CREATE_ACCOUNT) && !option.equals(LOGIN_ACCOUNT)) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
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

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static String createAccount() {
        Boolean userNotExist = true;
        User newUser = null;
        Boolean passwordMatch = true;
        do {
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
            newComunication = sendMessage(newComunication, isSeller);
            userNotExist = (Boolean)newComunication.content.get(0);
            if (!password.equals(confirmPassword)) {
                passwordMatch = false;
            } else if (userNotExist && passwordMatch) {
                userCreationSuccessMessage();
            }
        } while (!userNotExist || !passwordMatch);
        return CONTINUE_LOOP;
    }

    private static String login() {
        String option = LOGIN_COSTUMER;
        String loop = CONTINUE_LOOP;
        do {
            loop = CONTINUE_LOOP;
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
            newComunication = sendMessage(newComunication, EnumServices.LOGIN_CUSTOMER);
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
            content.add(nickname);
            content.add(password);
            Comunication newComunication = new Comunication(EnumChannel.VIEW_TO_CONTROL, EnumServices.LOGIN_SELLER, content);
            newComunication = sendMessage(newComunication, EnumServices.LOGIN_SELLER);
            confirmLogin = (Boolean)newComunication.content.get(0);
            if (confirmLogin) {
                seller = nickname;
            }
        } while (!confirmLogin);
        return END_LOOP;
    }

    private static void menuCustomer() {
        /*
         * PROCURAR O QUE COLOCAR DENTRO DESTE MENU!
         */
        System.out.println("");
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

    private static Comunication sendMessage(Comunication comunication, EnumServices enumService) {
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

    private static void userAlreadyExistErrorMessage() {
        System.out.println("Usuário já existe, por favor escolha um diferente!");
    }

    private static void userCreationSuccessMessage() {
        System.out.println("Usuário criado com sucesso!");
    }

}
