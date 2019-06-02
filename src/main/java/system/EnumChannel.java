package system;

public enum EnumChannel {

	VIEW_TO_CONTROL(1),
	CONTROL_TO_VIEW(2),
	CONTROL_TO_MODEL(3),
	MODEL_TO_CONTROL(4);
	
	
	private final int value;
	EnumChannel(int channelValue){
		value = channelValue;
	}
	
	public int getValor() {
		return value;
	}
			
}
