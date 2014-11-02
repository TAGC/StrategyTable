package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;

public class BypassStrategy<T extends Operation<?, ?>> implements Strategy<T> {
	
	@Override
	public void execute(T operation, Element element, StrategyTable table) {
		if (!element.isDecorated())
			throw new IllegalArgumentException("This strategy cannot be applied to elements of type: "
					+ element.getClass().getSimpleName());

		table.operate(operation, element.withoutDecoration());
	}

	@Override
	public String toString() {
		return "Bypass strategy - redirects the operation to operate directly on the decoratee";
	}
}
