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
	
	GET_ITENS(11),
	GET_CUSTOMERS(12),
	GET_SELLERS(13),	
	
	TOTAL_FUNDS(99)
	;
	
	
	private final int value;
	EnumServices(int requestValue){
		value = requestValue;
	}
	
	public int getValue() {
		return value;
	}
			
}

