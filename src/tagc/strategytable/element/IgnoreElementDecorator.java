package tagc.strategytable.element;

public class IgnoreElementDecorator extends AbstractElement {

	public IgnoreElementDecorator(Element decoratee) {
		super(decoratee.getValue());
	}
}
