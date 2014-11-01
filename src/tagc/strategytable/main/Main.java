package tagc.strategytable.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tagc.strategytable.element.AddElement;
import tagc.strategytable.element.Element;
import tagc.strategytable.element.IgnoreElementDecorator;
import tagc.strategytable.element.MultElement;
import tagc.strategytable.operation.CountElementOperation;
import tagc.strategytable.operation.FindTotalOperation;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.operation.PureOperation;
import tagc.strategytable.strategy.AddTotalOperationStrategy;
import tagc.strategytable.strategy.CountElementOperationStrategy;
import tagc.strategytable.strategy.MultTotalOperationStrategy;
import tagc.strategytable.table.StrategyTable;

public class Main {

	public static void main(String[] args) {
		demonstrate();
	}

	private static void demonstrate() {
		final Set<Class<? extends Element>> elementClassSet = getElementClassSet();
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = getOperationClassSet();
		final StrategyTable strategyTable = setupStrategyTable(elementClassSet, operationClassSet);

		System.out.println(strategyTable);

		System.out.println("Configured strategies");
		for (Class<? extends Element> elementClass : elementClassSet) {
			for (Class<? extends Operation<?, ?>> operationClass : operationClassSet) {
				System.out.printf("%s + %s = %s\n", elementClass.getSimpleName(), operationClass.getSimpleName(),
						strategyTable.getStrategyType(operationClass, elementClass).getSimpleName());
			}
		}

		final List<Element> elements = new ArrayList<Element>();
		elements.add(new AddElement(5));
		elements.add(new MultElement(10));
		elements.add(new AddElement(-20));
		elements.add(new IgnoreElementDecorator(new MultElement(-5)));

		final PureOperation<Integer> totalOperation = new FindTotalOperation();
		final PureOperation<Integer> countOperation = new CountElementOperation();
		strategyTable.operateOverCollection(totalOperation, elements);
		strategyTable.operateOverCollection(countOperation, elements);
		
		System.out.println("\nOperation results");
		System.out.println("Total: " + totalOperation.get());
		System.out.println("Element count: " + countOperation.get());
	}

	private static StrategyTable setupStrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet, operationClassSet);

		/*
		 * Total finding operation.
		 */
		strategyTable.addOperationStrategy(FindTotalOperation.class, AddElement.class, new AddTotalOperationStrategy());

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

	private static Set<Class<? extends Operation<?, ?>>> getOperationClassSet() {
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = new HashSet<Class<? extends Operation<?, ?>>>();

		operationClassSet.add(FindTotalOperation.class);
		operationClassSet.add(CountElementOperation.class);

		return operationClassSet;
	}

	private static Set<Class<? extends Element>> getElementClassSet() {
		final Set<Class<? extends Element>> elementClassSet = new HashSet<Class<? extends Element>>();

		elementClassSet.add(AddElement.class);
		elementClassSet.add(MultElement.class);
		elementClassSet.add(IgnoreElementDecorator.class);

		return elementClassSet;
	}
}
