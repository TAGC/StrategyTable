package tagc.strategytable.element;


class AddElement extends AbstractBaseElement {

	public AddElement(int value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return String.format("Add element (%s)", getValue());
	}
}
