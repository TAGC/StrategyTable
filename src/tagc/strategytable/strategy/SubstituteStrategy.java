package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;

public class SubstituteStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element, StrategyTable table) {
		if (!element.isDecorated())
			throw new IllegalArgumentException("This strategy cannot be applied to elements of type: "
					+ element.getClass().getSimpleName());

		/*
		 * We know this is a safe cast because we are guaranteed that the type
		 * of the operation is T and #getClass returns the runtime type of the
		 * operation, which must be Class<? extends T>
		 */
		@SuppressWarnings("unchecked")
		final Class<? extends T> operationType = (Class<? extends T>) operation.getClass();
		final Strategy<T> strategy = table.getOperationStrategy(operationType, element.withoutDecoration().getClass());
		strategy.execute(operation, element, table);
	}

	@Override
	public String toString() {
		return "Substitute strategy - uses the strategy meant for the decoratee but applies the decorator in its place";
	}
}
