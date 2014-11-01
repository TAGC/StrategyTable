package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;


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
