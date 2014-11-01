package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import operation.CountElementOperation;
import operation.FindTotalOperation;
import operation.PureOperation;
import strategy.AddTotalOperationStrategy;
import strategy.CountElementOperationStrategy;
import strategy.MultTotalOperationStrategy;
import table.StrategyTable;
import element.AddElement;
import element.Element;
import element.IgnoreElementDecorator;
import element.MultElement;

public class Main {

	public static void main(String[] args) {
		demonstrate();
	}

	private static void demonstrate() {
		final Set<Class<? extends Element>> elementClassSet = getElementClassSet();
		final StrategyTable strategyTable = setupStrategyTable(elementClassSet);
		
		System.out.println(strategyTable);
		
		final List<Element> elements = new ArrayList<Element>();
		elements.add(new AddElement(5));
		elements.add(new MultElement(10));
		elements.add(new AddElement(-20));
		
		final PureOperation<Integer> totalOperation = new FindTotalOperation();
		final PureOperation<Integer> countOperation = new CountElementOperation();
		for(Element e : elements) {
			strategyTable.operate(totalOperation, e);
			strategyTable.operate(countOperation, e);
		}
		
		System.out.println("Total: " + totalOperation.get());
		System.out.println("Element count: " + countOperation.get());
	}
	
	private static StrategyTable setupStrategyTable(Set<Class<? extends Element>> elementClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet);

		/*
		 * Total finding operation.
		 */
		strategyTable.addOperationStrategy(FindTotalOperation.class, AddElement.class,
				new AddTotalOperationStrategy());

		strategyTable.addOperationStrategy(FindTotalOperation.class, MultElement.class,
				new MultTotalOperationStrategy());

		strategyTable.addNullOperationStrategy(FindTotalOperation.class, IgnoreElementDecorator.class);
		
		/*
		 * Counting operation.
		 */
		strategyTable.addOperationStrategy(CountElementOperation.class, AddElement.class,
				new CountElementOperationStrategy());
		
		strategyTable.addOperationStrategy(CountElementOperation.class, MultElement.class,
				new CountElementOperationStrategy());
		
		strategyTable.addNullOperationStrategy(CountElementOperation.class, IgnoreElementDecorator.class);
		
		return strategyTable;
	}
	
	private static Set<Class<? extends Element>> getElementClassSet() {
		final Set<Class<? extends Element>> elementClassSet = new HashSet<Class<? extends Element>>();

		elementClassSet.add(AddElement.class);
		elementClassSet.add(MultElement.class);
		elementClassSet.add(IgnoreElementDecorator.class);

		return elementClassSet;
	}
}
