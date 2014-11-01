package tagc.strategytable.element;

class MultElement extends AbstractElement {

	public MultElement(int value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return String.format("Mult element (%s)", getValue());
	}
}
