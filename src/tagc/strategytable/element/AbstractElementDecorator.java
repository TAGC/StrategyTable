package tagc.strategytable.element;

public class AbstractElementDecorator implements Element {

	protected final Element decoratee;

	public AbstractElementDecorator(Element decoratee) {
		this.decoratee = decoratee;
	}

	@Override
	public int getValue() {
		return decoratee.getValue();
	}
	
	@Override
	public final boolean isDecorated() {
		return true;
	}

	@Override
	public final int getDecorationLevel() {
		return decoratee.getDecorationLevel()+1;
	}
	
	@Override
	public final Element asDecorationAtLevel(int decorationLevel) {
		if (decorationLevel > getDecorationLevel())
			throw new IllegalArgumentException("This element cannot be represented at decoration level "
					+ decorationLevel);

		if(decorationLevel == getDecorationLevel()) {
			return this;
		} else {
			return decoratee.asDecorationAtLevel(decorationLevel);
		}
	}
}
