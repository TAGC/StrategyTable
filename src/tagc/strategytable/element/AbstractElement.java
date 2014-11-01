package tagc.strategytable.element;

public abstract class AbstractElement implements Element {

	private final int value;

	public AbstractElement(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tagc.strategytable.element.Element#isDecorated()
	 * 
	 * Decorator implementations of Element should override this method to
	 * return true.
	 */
	@Override
	public boolean isDecorated() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tagc.strategytable.element.Element#withoutDecoration()
	 * 
	 * Decorator implementations of Element should override this method to
	 * return a reference to their decoratee.
	 */
	@Override
	public Element withoutDecoration() {
		return this;
	}
}
