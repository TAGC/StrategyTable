package tagc.strategytable.strategy;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.FindTotalOperation;

public class AddTotalOperationStrategy implements Strategy<FindTotalOperation> {

	@Override
	public void execute(FindTotalOperation operation, Element element) {
		operation.store(operation.get()+element.getValue());
	}
	
	@Override
	public String toString() {
		return "Addition strategy";
	}
}
