package strategy;

import operation.Operation;
import element.Element;


public class NullStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element) {
		// Do nothing.
	}
	
	@Override
	public String toString() {
		return "Generic null strategy";
	}
}
