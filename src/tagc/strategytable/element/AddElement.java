package tagc.strategytable.element;


class AddElement extends AbstractElement {

	public AddElement(int value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return String.format("Add element (%s)", getValue());
	}
}
