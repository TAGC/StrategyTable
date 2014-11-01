package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.CountElementOperation;
import tagc.strategytable.table.StrategyTable;

public class CountElementOperationStrategy implements Strategy<CountElementOperation> {

	@Override
	public void execute(CountElementOperation operation, Element element, StrategyTable table) {
		operation.store(operation.get()+1);
	}
	
	@Override
	public String toString() {
		return "Counting strategy";
	}
}
