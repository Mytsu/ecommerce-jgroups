package system;


public enum EnumServices {
	
	//Servicos da visao pro controle
	CREATE_CUSTOMER,
	CREATE_SELLER,
	LOGIN_CUSTOMER,
	LOGIN_SELLER,
	LIST_ITENS,
	MAKE_QUESTION,
	BUY_ITEM,
	SEND_ANSWER,
	SEARCH_ITEM,
	ADD_ITEM,
	SOLD_ITENS,
	BOUGHT_ITENS,
	
	//Servicos do controle pro modelo
	GET_ITENS,
	GET_CUSTOMERS,
	GET_SELLERS,
	GET_ITEM,
	GET_CUSTOMER,
	GET_SELLER,
	SAVE_ITEM,
	SAVE_CUSTOMER,
	SAVE_SELLER,
	
	// Servicos da compra de um produto
	ITEM_EXIST,
	CUSTOMER_EXIST,
	SELLER_EXIST,
	HAS_ENOUGHT_ITEM,
	HAS_ENOUGHT_FUNDS,
	
	ADD_FUNDS_SELLER,
	ADD_FUNDS_CUSTOMER,
	DEDUCE_AMOUNT_PRODUCT,
	ADD_SELL_CUSTOMER,
	ADD_SELL_SELLER,
	
	// Servicos de login
	CONFIRM_LOGIN_CUSTOMER,
	CONFIRM_LOGIN_SELLER,
	
	// Servicos de pergunta
	SAVE_QUESTION,
	SAVE_ANSWER,	
	
	//Servicos para montagem dos subgrupos
	NEW_VIEW_MEMBER,
	NEW_CONTROL_MEMBER,
	NEW_MODEL_MEMBER,
	
	//Servicos do sistema
	TOTAL_FUNDS_BOOL,
	TOTAL_FUNDS_INT;

	/*
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
	GET_ITEM(34),
	GET_CUSTOMER(35),
	GET_SELLER(36),
	SAVE_ITEM(37),
	SAVE_CUSTOMER(38),
	SAVE_SELLER(39),
	
	// Servicos da compra de um produto
	ITEM_EXIST(40),
	CUSTOMER_EXIST(41),
	SELLER_EXIST(42),
	HAS_ENOUGHT_ITEM(43),
	HAS_ENOUGHT_FUNDS(44),
	
	ADD_FUNDS_SELLER(45),
	ADD_FUNDS_CUSTOMER(46),
	DEDUCE_AMOUNT_PRODUCT(47),
	ADD_SELL_CUSTOMER(48),
	ADD_SELL_SELLER(49),
	
	// Servicos de login
	CONFIRM_LOGIN_CUSTOMER(50),
	CONFIRM_LOGIN_SELLER(51),
	
	// Servicos de pergunta
	SAVE_QUESTION(52),
	SAVE_ANSWER(53),	
	
	//Servicos para montagem dos subgrupos
	NEW_VIEW_MEMBER(61),
	NEW_CONTROL_MEMBER(62),
	NEW_MODEL_MEMBER(63),
	
	//Servicos do sistema
	TOTAL_FUNDS_BOOL(91),
	TOTAL_FUNDS_INT(92);
	
	private final int value;
	EnumServices(int requestValue) {
		value = requestValue;
	}
	
	public int getValue() {
		return value;
	}
	*/
			
}

