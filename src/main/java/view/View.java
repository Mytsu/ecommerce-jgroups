package view;

import java.util.Scanner;
import model.*;
import system.User;

public class View {

    private static Scanner selectOption;
    final static String EXIT_SYSTEM = "29";
    
    public static void main(String[] args) {
        System.out.println("=== Bem vindx ao SD e-Commerce ===");
        accessSystem();
        menu();
    }

    public View() {
        accessSystem();
        //menu();
    }

    private static void accessSystem() {
        String option = "1";
        String loop = "1";
        do{
            loop = "1";
            clearScreen();
            if(!option.equals("1") && !option.equals("2")){
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("=== Bem vindx ao SD e-Commerce ===");
            System.out.println("1 - Para criar conta");
            System.out.println("2 - Para realizar login");
            System.out.println( EXIT_SYSTEM + " - Sair do sistema");
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();

            if(option.equals("1")){
                loop = createAccount();
            } else if(option.equals("2")){
                loop = login();
            }
        } while (!option.equals("1") && !option.equals("2") && !option.equals(EXIT_SYSTEM) || loop == "0");
        System.out.println("Adios");
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }

    private static String createAccount() {
        int userExist = 0;
        User newUser;
        Boolean passwordMatch = true;
        do {
            if(!passwordMatch){
                passwordConfirmErrorMessage();
            }
            if(userExist < 0) { // TODO colocar ID de erro
                userAlreadyExistErrorMessage();
            }
            passwordMatch = true;
            System.out.println("========== CRIANDO CONTA ==========");
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            if(nickname.equals("sair")){
                return "0";
            }
            System.out.println("Nome completo: ");
            String name = selectOption.nextLine();
            System.out.println("Senha: ");
            String password = selectOption.nextLine();
            System.out.println("Confirmar senha: ");
            String confirmPassword = selectOption.nextLine();
            System.out.println("Você é um vendedor?(s/n): ");
            selectOption = new Scanner(System.in);
            String isSellerString = selectOption.nextLine();
            Boolean isSeller = false;
            if(isSellerString.toLowerCase().equals("s")){
                isSeller = true;
            }
            /*
            userExist = control.add_customer(user_nickname, user_fullname, user_password);
            */
            if(!password.equals(confirmPassword)) {
                passwordMatch = false;
            } else if(userExist == 0 && passwordMatch) {
                newUser = new User(nickname, name, password);
                // TODO ENVIAR PARA CRIAR CONTA DO USUARIO NO CONTROLE ATRAVES DO JGROUPS UTILIZANDO DESPACHANTE
            }
        } while(userExist != 0 || !passwordMatch);
        return "0";
    }

    private static String login() {
        String option = "1";
        do{
            if(!option.equals("1") && !option.equals("2")) {
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("========= ACESSAR CONTA =========");
            System.out.println("1 - Acessar conta de consumidor");
            System.out.println("2 - Acessar conta de vendedor");
            selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            if(option.equals("sair")){
                return "0";
            } else if(option.equals("1")){
                option = loginCustomer();
            } else if(option.equals("2")){
                option = loginSeller();
            }
        } while (!option.equals("1") && !option.equals("2"));
        return "0";
    }

    private static String loginCustomer() {
        Boolean confirmLogin = true;
        do{
            if(!confirmLogin){
                loginErrorMessage();
            }
            System.out.println("== ACESSAR CONTA DE CONSUMIDOR ==");
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            System.out.println("Senha: ");
            selectOption = new Scanner(System.in);
            String password = selectOption.nextLine();
            if(nickname.equals("sair")) {
                return "0";
            }
            /*
            confirmLogin = control.confirmLoginConsumer(nickname, password);
            */
        } while(!confirmLogin);
        return "0";
    }

    private static void loginErrorMessage() {
        System.out.println("Usuário/senha inválido(s)");
    }

    private static String loginSeller() {
        Boolean confirmLogin = true;
        do{
            if(!confirmLogin){
                loginErrorMessage();
            }
            System.out.println("=== ACESSAR CONTA DE VENDEDOR ===");
            System.out.println("Apelido: ");
            selectOption = new Scanner(System.in);
            String nickname = selectOption.nextLine();
            System.out.println("Senha: ");
            selectOption = new Scanner(System.in);
            String password = selectOption.nextLine();
            if(nickname.equals("sair")){
                return "0";
            }
            /*
            confirmLogin = control.confirmLoginSeller(nickname, password);
            */
        } while(!confirmLogin);
        return "0";
    }

    private static void menu() {
        /*
        PROCURAR O QUE COLOCAR DENTRO DESTE MENU!
        */
        System.out.println("");
    }

    private static void passwordConfirmErrorMessage() {
        System.out.println("As senhas informadas não são iguais!");
    }

    private static void userAlreadyExistErrorMessage() {
        System.out.println("Usuário já existe, por favor escolha um diferente!");
    }

}
