package system;


public enum EnumServices {
	
	//Servicos da visao pro controle
	CREATE_CUSTOMER(1),
	CREATE_SELLER(2),
	LOGIN_CUSTOMER(3),
	LOGIN_SELLER(4),
	LIST_ITENS(5),
	MAKE_QUESTION(6),
	BUY_ITEM(7),
	SEND_ANSWER(8),
	SEARCH_ITEM(9),
	ADD_ITEM(10),
	SOLD_ITENS(11),
	BOUGHT_ITENS(12),
	
	//Servicos do controle pro modelo
	GET_ITENS(31),
	GET_CUSTOMERS(32),
	GET_SELLERS(33),
	SAVE_ITEM(34),
	SAVE_CUSTOMER(35),
	SAVE_SELLER(36),
	
	//Servicos para montagem dos subgrupos
	NEW_VIEW_MEMBER(61),
	NEW_CONTROL_MEMBER(62),
	NEW_MODEL_MEMBER(63),
	
	//Servicos do sistema
	TOTAL_FUNDS_BOOL(91),
	TOTAL_FUNDS_INT(92)
	;
	
	
	private final int value;
	EnumServices(int requestValue) {
		value = requestValue;
	}
	
	public int getValue() {
		return value;
	}
			
}

