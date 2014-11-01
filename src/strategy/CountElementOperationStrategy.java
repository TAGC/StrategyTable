package strategy;

import operation.CountElementOperation;
import element.Element;

public class CountElementOperationStrategy implements Strategy<CountElementOperation> {

	@Override
	public void execute(CountElementOperation operation, Element element) {
		operation.store(operation.get()+1);
	}
	
	@Override
	public String toString() {
		return "Counting strategy";
	}
}
