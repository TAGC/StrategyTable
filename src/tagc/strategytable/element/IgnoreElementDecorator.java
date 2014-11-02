package tagc.strategytable.element;

class IgnoreElementDecorator extends AbstractElementDecorator {

	public IgnoreElementDecorator(Element decoratee) {
		super(decoratee);
	}
	
	@Override
	public int getValue() {
		throw new AssertionError("This method should not have been called");
	}
	
	@Override
	public String toString() {
		return String.format("[Ignore] " + decoratee.toString());
	}
}
