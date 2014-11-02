package tagc.strategytable.element;

class ReverseElementDecorator extends AbstractElementDecorator {

	public ReverseElementDecorator(Element decoratee) {
		super(decoratee);
	}
	
	@Override
	public int getValue() {
		return -decoratee.getValue();
	}
	
	@Override
	public String toString() {
		return String.format("[Reverse] " + decoratee.toString());
	}
}
