package tagc.strategytable.element;

class IgnoreElementDecorator extends AbstractElement {
	
	private final Element decoratee;

	public IgnoreElementDecorator(Element decoratee) {
		super(decoratee.getValue());
		this.decoratee = decoratee;
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
		return String.format("[Ignore element decorator] " + decoratee.toString());
	}
}
