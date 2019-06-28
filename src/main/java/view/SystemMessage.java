package view;

import java.util.Scanner;

public class SystemMessage(){


    public static void backMessage() {
        System.out.println("Para voltar ao menu anterior digite: " + View.EXIT_MENU);
    }


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    public static void userCreationSuccessMessage() {
        System.out.println("Usuário criado com sucesso!");
    }


    public static void pressEnterMessage() {
        System.out.println("PRESSIONE ENTER PRA SAIR");
        Scanner selectOption = new Scanner(System.in);
        String hold = selectOption.nextLine();
    }


}