package system;


public enum EnumRequest {

	CREATE_CUSTOMER(1),
	CREATE_SELLER(2),
	LOGIN_CUSTOMER(3),
	LOGIN_SELLER(4),
	LIST_ITENS(5),
	MAKE_QUESTION(6),
	BUY_ITEM(7),
	SEND_ANSWER(8),
	SEARCH_ITEM(9),
	
	GET_ITENS(10),
	GET_CUSTOMERS(11),
	GET_SELLERS(12),
	
	
	TOTAL_FUNDS(99)
	;
	
	
	private final int value;
	EnumRequest(int requestValue){
		value = requestValue;
	}
	
	public int getValue() {
		return value;
	}
			
}

