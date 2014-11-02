package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;

public class SubstituteStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element, int decorationLevel, StrategyTable table) {
		if (!element.isDecorated())
			throw new IllegalArgumentException("This strategy cannot be applied to elements of type: "
					+ element.getClass().getSimpleName());
		
		table.operate(operation, element, decorationLevel-1);
	}

	@Override
	public String toString() {
		return "Substitute strategy - uses the strategy meant for the decoratee but applies the decorator in its place";
	}
}
