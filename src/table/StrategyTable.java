package table;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import operation.Operation;
import strategy.NullStrategy;
import strategy.Strategy;
import element.Element;

public class StrategyTable {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends Element>, Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>> table;

	@SuppressWarnings("rawtypes")
	public StrategyTable(Set<Class<? extends Element>> elementClassSet) {
		table = new HashMap<Class<? extends Element>, Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>>();

		for (Class<? extends Element> elementClass : elementClassSet) {
			table.put(elementClass, new HashMap<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>());
		}
	}

	@SuppressWarnings("rawtypes")
	public <I, O> void addOperationStrategy(Class<? extends Operation<I, O>> operationType,
			Class<? extends Element> elementType, Strategy<? extends Operation<I, O>> strategy) {

		Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> elementMap = table.get(elementType);

		if (elementMap == null) {
			elementMap = new HashMap<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>();
		}

		elementMap.put(operationType, strategy);
	}

	@SuppressWarnings("rawtypes")
	public <I, O> void addNullOperationStrategy(Class<? extends Operation<I, O>> operationType,
			Class<? extends Element> elementType) {

		Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> elementMap = table.get(elementType);

		if (elementMap == null) {
			elementMap = new HashMap<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>();
		}

		elementMap.put(operationType, new NullStrategy<Operation<I, O>>());
	}

	public <I, O> void operate(Operation<I, O> operation, Element element) {
		@SuppressWarnings("rawtypes")
		Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> map = table.get(element.getClass());

		if (map == null)
			throw new IllegalArgumentException("There are no operations defined to work with this type of element");

		/*
		 * We know that this is a safe cast because #addOperationStrategy is
		 * typesafe and is the only way a strategy can be associated with an
		 * operation.
		 */
		@SuppressWarnings("unchecked")
		Strategy<Operation<I, O>> strategy = (Strategy<Operation<I, O>>) map.get(operation.getClass());

		if (strategy == null)
			throw new IllegalArgumentException(
					"There are no strategies defined to work with this type of operation for this element");

		strategy.execute(operation, element);
	}

	@Override
	public String toString() {
		String output = "";
		for (@SuppressWarnings("rawtypes")
		Entry<Class<? extends Element>, Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>>> elementMap : table
				.entrySet()) {
			output += "Element type: " + elementMap.getKey() + "\n";

			for (@SuppressWarnings("rawtypes")
			Entry<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> strategyMap : elementMap.getValue()
					.entrySet()) {

				output += String.format("\t%s -> %s\n", strategyMap.getKey(), strategyMap.getValue());
			}
		}

		return output;
	}
}
