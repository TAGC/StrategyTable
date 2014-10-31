package strategy;

import operation.FindTotalOperation;
import element.Element;

public class AddTotalOperationStrategy implements Strategy<FindTotalOperation> {

	@Override
	public void execute(FindTotalOperation operation, Element element) {
		operation.store(operation.get()+element.getValue());
	}
}
