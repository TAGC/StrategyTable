package tagc.strategytable.element;

abstract class AbstractBaseElement implements Element {

	private final int value;

	public AbstractBaseElement(int value) {
		this.value = value;
	}

	@Override
	public final int getValue() {
		return value;
	}
	
	@Override
	public final boolean isDecorated() {
		return false;
	}

	@Override
	public final int getDecorationLevel() {
		return 0;
	}
	
	@Override
	public final Element asDecorationAtLevel(int decorationLevel) {
		if (decorationLevel > 0)
			throw new IllegalArgumentException("This element cannot be represented at decoration level "
					+ decorationLevel);

		return this;
	}
}
