package element;


public abstract class AbstractElement implements Element {
	
	private final int value;
	private final ElementType type;

	public AbstractElement(int value, ElementType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public ElementType getType() {
		return type;
	}
}
