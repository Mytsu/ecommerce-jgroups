package system;


public enum EnumServices {

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
	
	GET_ITENS(13),
	GET_CUSTOMERS(14),
	GET_SELLERS(15),
	SAVE_ITEM(16),
	SAVE_CUSTOMER(17),
	SAVE_SELLER(18),
	
	TOTAL_FUNDS_BOOL(99),
	TOTAL_FUNDS_INT(100)
	;
	
	
	private final int value;
	EnumServices(int requestValue) {
		value = requestValue;
	}
	
	public int getValue() {
		return value;
	}
			
}

