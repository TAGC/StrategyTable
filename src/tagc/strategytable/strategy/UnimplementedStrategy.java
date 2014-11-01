package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;

public class UnimplementedStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element, StrategyTable table) {
		throw new UnsupportedOperationException(String.format(
				"No strategy configured for applying operations of type %s to elements of type %s\n", operation
						.getClass().getSimpleName(), element.getClass().getSimpleName()));
	}

	@Override
	public String toString() {
		return "Unimplemented strategy - throws an exception when executed";
	}
}
