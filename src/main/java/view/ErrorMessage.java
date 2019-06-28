package view;

public class ErrorMessage {
	
	
	public static void addProductErrorMessage(int idError) {
        if(idError == -1){
            System.out.println("A quantia informada deve ser maior que ZERO!\nPor favor, ajuste o valor!");
        } else if(idError == -2){
            System.out.println("O preço informado deve ser maior que ZERO!\nPor favor, ajuste o valor!");
        } else if(idError <= -3){
            System.out.println("Sistema temporariamente indisponivel\nPor favor, tente mais tarde!");
        }
    }


    public static void invalidOptionMessage() {
        System.out.println("Opção inválida!\nPor favor, entre com uma opção válida!");
    }
	
	
	public static void loginErrorMessage() {
        System.out.println("Usuário/senha inválido(s)!");
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
