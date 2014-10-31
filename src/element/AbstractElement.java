package element;


public abstract class AbstractElement implements Element {
	
	private final int value;

	public AbstractElement(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
}
