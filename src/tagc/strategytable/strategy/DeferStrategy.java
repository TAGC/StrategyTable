package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;

public class DeferStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element, StrategyTable table) {
		if (!element.isDecorated())
			throw new IllegalArgumentException("This strategy cannot be applied to elements of type: "
					+ element.getClass().getSimpleName());
		
		table.operate(operation, element.withoutDecoration());
	}

	@Override
	public String toString() {
		return "Defer strategy - used by decorators to re-apply decorations on their decoratees";
	}
}
