package vision;

import java.util.Scanner;

public class Vision {
    
    public static void main(String[] args) {
        System.out.println("=== Bem vindx ao SD e-Commerce ===");
        accessSystem();
        menu();
    }

    private static void accessSystem(){
        String option = "1";
        do{
            if(!option.equals("1") && !option.equals("2")){
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("1 - Para criar conta");
            System.out.println("2 - Para realizar login");
            Scanner selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            selectOption.close();
        } while (!option.equals("1") && !option.equals("2"));

        if(option.equals("1")){
            createAccount();
        } else if(option.equals("2")){
            login();
        }
    }

    private static void createAccount(){
        String option = "1";
        do{
            if(!option.equals("1") && !option.equals("2")){
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("========== CRIAR CONTA ==========");
            System.out.println("1 - Para criar conta consumidor");
            System.out.println("2 - Para criar conta de vendedor");
            Scanner selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            selectOption.close();
            if(option.equals("sair")){
                break;
            }
        } while (!option.equals("1") && !option.equals("2"));

        if(option.equals("sair")){
            accessSystem();
        } else if(option.equals("1")){
            createCustomerAccount();
        } else if(option.equals("2")){
            createSellerAccount();
        }
    }

    private static void createCustomerAccount(){
        Boolean userExist = false;
        Boolean passwordMatch = true;

        do{
            if(!passwordMatch){
                passwordConfirmErrorMessage();
            }
            if(userExist){
                userAlreadyExistErrorMessage();
            }
            System.out.println("== CRIANDO CONTA DE CONSUMIDOR ==");
            System.out.println("Apelido: ");
            //Scanner
            System.out.println("Nome completo: ");
            //Scanner
            System.out.println("Senha: ");
            //Scanner
            System.out.println("Confirmar senha: ");
            //Scanner
            
            if(nickname.equals("sair")){
                break;
            }
            /*
            userExist = control.add_customer(user_nickname, user_fullname, user_password);
            if(!password.equals(passwordConfirm)){
                passwordMatch = false;
            }
            */
        } while(userExist != 0 && !passwordMatch);
        
        /*
        if(nickname.equals("sair")){
            createAccount();
        }
        */
    }

    private static void createSellerAccount(){
        Boolean userExist = false;
        Boolean passwordMatch = true;

        do{
            if(!passwordMatch){
                passwordConfirmErrorMessage();
            }
            if(userExist){
                userAlreadyExistErrorMessage();
            }
            System.out.println("=== CRIANDO CONTA DE VENDEDOR ===");
            System.out.println("Apelido: ");
            //Scanner
            System.out.println("Nome completo: ");
            //Scanner
            System.out.println("Senha: ");
            //Scanner
            System.out.println("Confirmar senha: ");
            /*Scanner
            if(nickname.equals("sair")){
                break;
            }
            userExist = control.checkUserSellerAvailability(userNickname);
            if(!password.equals(passwordConfirm)){
                passwordMatch = false;
            }
            */
        } while(userExist != 0 && !passwordMatch);

        /*
        if(nickname.equals("sair")){
            createAccount();
        }
        model.createSellerAccount(nickname, name, password); 
        */
    }

    private static void login(){
        String option = "1";
        do{
            if(!option.equals("1") && !option.equals("2")){
                System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
            }
            System.out.println("========= ACESSAR CONTA =========");
            System.out.println("1 - Acessar conta de consumidor");
            System.out.println("2 - Acessar conta de vendedor");
            Scanner selectOption = new Scanner(System.in);
            option = selectOption.nextLine();
            selectOption.close();
            if(option.equals("sair")){
                break;
            }
        } while (!option.equals("1") && !option.equals("2"));

        if(option.equals("sair")){
            accessSystem();
        } else if(option.equals("1")){
            loginConsumer();
        } else if(option.equals("2")){
            loginSeller();
        }
    }

    private static void loginConsumer(){
        Boolean confirmLogin = true;
        do{
            if(!confirmLogin){
                loginErrorMessage();
            }
            System.out.println("== ACESSAR CONTA DE CONSUMIDOR ==");
            System.out.println("Apelido: ");
            //Scanner
            System.out.println("Senha: ");
            //Scanner
            /*
            if(nickname.equals("sair")){
                login();
            }
            confirmLogin = control.confirmLoginConsumer(nickname, password);
            */
        } while(!confirmLogin);
    }

    private static void loginErrorMessage(){
        System.out.println("Usuário/senha inválido(s)");
    }

    private static void loginSeller(){
        Boolean confirmLogin = true;
        do{
            if(!confirmLogin){
                loginErrorMessage();
            }
            System.out.println("=== ACESSAR CONTA DE VENDEDOR ===");
            System.out.println("Apelido: ");
            //Scanner
            System.out.println("Senha: ");
            //Scanner
            /*
            if(nickname.equals("sair")){
                login();
            }
            confirmLogin = control.confirmLoginSeller(nickname, password);
            */
        } while(!confirmLogin);
    }

    private static void menu(){
        /*
        PROCURAR O QUE COLOCAR DENTRO DESTE MENU!
        */
        System.out.println("");
    }

    private static void passwordConfirmErrorMessage(){
        System.out.println("As senhas informadas não são iguais!");
    }

    private static void userAlreadyExistErrorMessage(){
        System.out.println("Usuário já existe, por favor escolha um diferente!");
    }

}
