package strategy;

import operation.Operation;
import element.Element;

public class NullStrategy implements Strategy<Operation<?, ?>> {

	@Override
	public void execute(Operation<?, ?> operation, Element element) {
		// Do nothing.
	}

}
