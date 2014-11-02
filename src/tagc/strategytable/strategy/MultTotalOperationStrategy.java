package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.FindTotalOperation;
import tagc.strategytable.table.StrategyTable;

public class MultTotalOperationStrategy implements Strategy<FindTotalOperation> {

	@Override
	public void execute(FindTotalOperation operation, Element element, int decorationLevel, StrategyTable table) {
		operation.store(operation.get()*element.getValue());
	}
	
	@Override
	public String toString() {
		return "Multiplication strategy";
	}
}
