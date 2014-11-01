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

	/**
	 * Registers {@code strategy} to be used to handle operations of the type
	 * {@code operationType} for elements of the type {@code elementType}.
	 * <p>
	 * In other words, this method specifies that the {@link Strategy} object
	 * {@code strategy} should be executed using an operation {@code o} and an
	 * element {@code e} if {@code o.getClass().equals(operationType)} and
	 * {@code e.getClass().equals(elementType)}.
	 * 
	 * @param operationType
	 *            the {@code class} of {@code Operation} for {@code strategy} to
	 *            handle
	 * @param elementType
	 *            the {@code class} of {@code Element} for {@code strategy} to
	 *            handle
	 * @param strategy
	 *            a {@code Strategy} object to handle execution of an operation
	 *            on an element
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */
	public <I, O> void addOperationStrategy(Class<? extends Operation<I, O>> operationType,
			Class<? extends Element> elementType, Strategy<? extends Operation<I, O>> strategy) {

		@SuppressWarnings("rawtypes")
		final Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> elementMap = getElementMap(elementType);
		elementMap.put(operationType, strategy);
	}

	/**
	 * Registers a 'null' strategy to be used to handle operations of the type
	 * {@code operationType} for elements of the type {@code elementType}.
	 * <p>
	 * A null strategy conforms to the interface for a {@link Strategy} but
	 * performs no actions when executed. These strategies will leave the state
	 * of an {@code Operation} object unchanged.
	 * 
	 * @param operationType
	 *            the {@code class} of {@code Operation} for the null strategy
	 *            to handle
	 * @param elementType
	 *            the {@code class} of {@code Element} for the null strategy to
	 *            handle
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */

	public <I, O> void addNullOperationStrategy(Class<? extends Operation<I, O>> operationType,
			Class<? extends Element> elementType) {

		@SuppressWarnings("rawtypes")
		final Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> elementMap = getElementMap(elementType);
		elementMap.put(operationType, new NullStrategy<Operation<I, O>>());
	}

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> getElementMap(
			Class<? extends Element> elementType) {
		Map<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> elementMap = table.get(elementType);

		if (elementMap == null)
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support elements of type " + elementType);

		return elementMap;
	}

	/**
	 * Handles the execution of {@code operation} on {@code element} based on
	 * the appropriate registered {@link Strategy} (if any) and this strategy
	 * table's {@code policy}.
	 * 
	 * @param operation
	 *            the operation to perform on {@code element}
	 * @param element
	 *            the {@code Element} object to have {@code operation} applied
	 *            to
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 * @throws UnsupportedOperationException
	 *             if there is no explicitly registered {@code Strategy} for the
	 *             types of {@code operation} and {@code element} and this
	 *             strategy table's {@code policy} is set to {@code Strict}.
	 */
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
			output += "Element type: " + elementMap.getKey().getSimpleName() + "\n";

			for (@SuppressWarnings("rawtypes")
			Entry<Class<? extends Operation>, Strategy<? extends Operation<?, ?>>> strategyMap : elementMap.getValue()
					.entrySet()) {

				output += String.format("\t%s -> %s\n", strategyMap.getKey().getSimpleName(), strategyMap.getValue());
			}
		}

		return output;
	}
}
