package tagc.strategytable.element;

class IgnoreElementDecorator extends AbstractElement {
	
	private final Element decoratee;

	public IgnoreElementDecorator(Element decoratee) {
		super(decoratee.getValue());
		this.decoratee = decoratee;
	}
	
	@Override
	public int getValue() {
		throw new AssertionError("This method should not have been called");
	}
	
	@Override
	public boolean isDecorated() {
		return true;
	}

	@Override
	public Element withoutDecoration() {
		return decoratee.withoutDecoration();
	}
	
	@Override
	public String toString() {
		return String.format("[Ignore] " + decoratee.toString());
	}
}
