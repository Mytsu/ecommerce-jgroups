package view;

public class ErrorMessage {
	
	
	public static void invalidOptionMessage() {
        System.out.println("Opção inválida!\nPor favor entre com uma opção válida");
    }
	
	
	public static void loginErrorMessage() {
        System.out.println("Usuário/senha inválido(s)");
    }
	
	
	public static void noItemsFound(){
		System.out.println("Nenhum item foi encontrado!");
	}
	
	
	public static void passwordConfirmErrorMessage() {
        System.out.println("As senhas informadas não são iguais!");
    }

	
	public static void productIndexOutOfRangeMessage() {
        System.out.println("Index do produto não existe!");
    }
	
	
	public static void userAlreadyExistErrorMessage() {
        System.out.println("Usuário já existe, por favor escolha um diferente!");
    }
	
	
}
