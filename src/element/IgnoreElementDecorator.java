package element;

public class IgnoreElementDecorator extends AbstractElement {

	public IgnoreElementDecorator(Element decoratee) {
		super(decoratee.getValue(), ElementType.IGNORE_ELEMENT_DECORATOR);
	}
}
