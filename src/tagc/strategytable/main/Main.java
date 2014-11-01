package tagc.strategytable.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tagc.strategytable.element.Element;
import tagc.strategytable.element.ElementFactory;
import tagc.strategytable.operation.CountElementOperation;
import tagc.strategytable.operation.FindTotalOperation;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.operation.PureOperation;
import tagc.strategytable.strategy.AddTotalOperationStrategy;
import tagc.strategytable.strategy.CountElementOperationStrategy;
import tagc.strategytable.strategy.MultTotalOperationStrategy;
import tagc.strategytable.table.StrategyTable;
import tagc.strategytable.table.StrategyTablePolicy;

public class Main {

	public static void main(String[] args) {
		demonstrate();
	}

	private static void demonstrate() {
		final Set<Class<? extends Element>> baseElementClassSet = getBaseElementClassSet();
		final Set<Class<? extends Element>> decoratedElementClassSet = getDecoratedElementClassSet();
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = getOperationClassSet();
		final StrategyTable strategyTable = setupStrategyTable(baseElementClassSet, decoratedElementClassSet,
				operationClassSet);

		System.out.println(strategyTable);

		final List<Element> elements = new ArrayList<Element>();
		elements.add(ElementFactory.createAddElement(5));
		elements.add(ElementFactory.createMultElement(10));
		elements.add(ElementFactory.createAddElement(-20));
		elements.add(ElementFactory.addIgnoreDecoration(ElementFactory.createMultElement(10)));

		final PureOperation<Integer> totalOperation = new FindTotalOperation();
		final PureOperation<Integer> countOperation = new CountElementOperation();
		strategyTable.operateOverCollection(totalOperation, elements);
		strategyTable.operateOverCollection(countOperation, elements);

		System.out.println("\nOperation results");
		System.out.println("Total: " + totalOperation.get());
		System.out.println("Element count: " + countOperation.get());
	}

	private static StrategyTable setupStrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet, decoratedElementClassSet,
				operationClassSet, StrategyTablePolicy.DEFAULT);

		/*
		 * Specifies all operations to ignore IgnoreElementDecorator elements
		 * and lock that in.
		 */
		strategyTable.addNullElementStrategies(ElementFactory.getIgnoreElementDecoratorClass());
		strategyTable.setElementStrategiesLocked(ElementFactory.getIgnoreElementDecoratorClass(), true);

		/*
		 * Specifies strategies for 'FindTotalOperation' operations.
		 */
		strategyTable.addOperationStrategy(FindTotalOperation.class, ElementFactory.getAddElementClass(),
				new AddTotalOperationStrategy());
		strategyTable.addOperationStrategy(FindTotalOperation.class, ElementFactory.getMultElementClass(),
				new MultTotalOperationStrategy());

		/*
		 * Specifies strategies for 'CountElementOperation' operations.
		 */
		strategyTable.addOperationStrategies(CountElementOperation.class, new CountElementOperationStrategy());

		return strategyTable;
	}

	private static Set<Class<? extends Operation<?, ?>>> getOperationClassSet() {
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = new HashSet<Class<? extends Operation<?, ?>>>();

		operationClassSet.add(FindTotalOperation.class);
		operationClassSet.add(CountElementOperation.class);

		return operationClassSet;
	}

	private static Set<Class<? extends Element>> getBaseElementClassSet() {
		final Set<Class<? extends Element>> baseElementClassSet = new HashSet<Class<? extends Element>>();

		baseElementClassSet.add(ElementFactory.getAddElementClass());
		baseElementClassSet.add(ElementFactory.getMultElementClass());
		return baseElementClassSet;
	}

	private static Set<Class<? extends Element>> getDecoratedElementClassSet() {
		final Set<Class<? extends Element>> decoratedElementClassSet = new HashSet<Class<? extends Element>>();

		decoratedElementClassSet.add(ElementFactory.getIgnoreElementDecoratorClass());
		return decoratedElementClassSet;
	}
}
