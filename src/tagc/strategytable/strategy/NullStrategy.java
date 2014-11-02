package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.table.StrategyTable;


public class NullStrategy<T extends Operation<?, ?>> implements Strategy<T> {

	@Override
	public void execute(T operation, Element element, int decorationLevel, StrategyTable table) {
		// Do nothing.
	}
	
	@Override
	public String toString() {
		return "Null strategy - performs no action when executed";
	}
}
