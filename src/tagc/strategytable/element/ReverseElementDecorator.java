package tagc.strategytable.element;

class ReverseElementDecorator extends AbstractElement {

	private final Element decoratee;

	public ReverseElementDecorator(Element decoratee) {
		super(decoratee.getValue());
		this.decoratee = decoratee;
	}
	
	@Override
	public int getValue() {
		return -decoratee.getValue();
	}
	
	@Override
	public boolean isDecorated() {
		return true;
	}

	@Override
	public Element withoutDecoration() {
		return decoratee;
	}
	
	@Override
	public String toString() {
		return String.format("[Reverse] " + decoratee.toString());
	}
}
