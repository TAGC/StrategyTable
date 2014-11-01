package tagc.strategytable.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.strategy.NullStrategy;
import tagc.strategytable.strategy.Strategy;

public class StrategyTable {

	private final Map<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>> table;

	public StrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		table = new HashMap<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>>();

		for (Class<? extends Element> elementClass : elementClassSet) {
			final Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap;
			strategyMap = new HashMap<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>();

			for (Class<? extends Operation<?, ?>> operationClass : operationClassSet) {
				strategyMap.put(operationClass, createNullStrategy());
			}

			table.put(elementClass, strategyMap);
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
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 */
	public <T extends Operation<?, ?>> void addOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType, Strategy<T> strategy) {

		final Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap = getStrategyMap(elementType);
		putOperationStrategy(strategyMap, operationType, strategy);
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
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 */
	public <T extends Operation<?, ?>> void addNullOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		final Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap = getStrategyMap(elementType);
		putOperationStrategy(strategyMap, operationType, StrategyTable.<T> createNullStrategy());
	}

	private static <T extends Operation<?, ?>> Strategy<T> createNullStrategy() {
		return new NullStrategy<T>();
	}

	private Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> getStrategyMap(
			Class<? extends Element> elementType) {
		if (!table.containsKey(elementType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support elements of type "
							+ elementType.getSimpleName());

		return table.get(elementType);
	}

	private <T extends Operation<?, ?>> void putOperationStrategy(
			Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap,
			Class<? extends T> operationType, Strategy<T> strategy) {

		if (!strategyMap.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		strategyMap.put(operationType, strategy);
	}

	/*
	 * We know that this is a safe cast because #putOperationStrategy is
	 * typesafe and is the only way a strategy can be associated with an
	 * operation.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Operation<?, ?>> Strategy<T> getOperationStrategy(
			Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap,
			Class<? extends T> operationType) {

		if (!strategyMap.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		return (Strategy<T>) strategyMap.get(operationType);
	}

	private <T extends Operation<?, ?>> Strategy<T> getOperationElementStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		return getOperationStrategy(getStrategyMap(elementType), operationType);
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
	public <T extends Operation<?, ?>> void operate(T operation, Element element) {

		/*
		 * It's safe to make this cast because #getClass() will return the
		 * runtime type of the operation which we can guarantee the type of.
		 */
		@SuppressWarnings("unchecked")
		final Class<? extends T> operationType = (Class<? extends T>) operation.getClass();
		final Class<? extends Element> elementType = element.getClass();
		final Strategy<T> strategy = getOperationElementStrategy(operationType, elementType);

		if (strategy == null)
			throw new IllegalArgumentException(
					"There are no strategies defined to work with this type of operation for this element");

		strategy.execute(operation, element);
	}

	/**
	 * Handles the execution of {@code operation} over a collection of
	 * {@code Element} objects in sequence based on the appropriate registered
	 * {@link Strategy} (if any) and this strategy table's {@code policy}.
	 * 
	 * @param operation
	 *            the operation to perform on {@code element}
	 * @param elements
	 *            the collection of {@code Element} objects to have
	 *            {@code operation} applied to
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 * @throws UnsupportedOperationException
	 *             if there is no explicitly registered {@code Strategy} for the
	 *             types of {@code operation} and {@code element} and this
	 *             strategy table's {@code policy} is set to {@code Strict}.
	 */
	public <T extends Operation<?, ?>> void operateOverCollection(T operation, Collection<? extends Element> elements) {
		for (Element e : elements) {
			operate(operation, e);
		}
	}

	/**
	 * Returns the class of the {@code Strategy} object that is specified to
	 * handle a given type of {@code Operation} and {@code Element}.
	 * 
	 * @param operationType
	 *            the type of {@code Operation}
	 * @param elementType
	 *            the type of {@code Element}
	 * @return the runtime type of the {@code Strategy} object corresponding to
	 *         {@code operationType} and {@code elementType}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 */
	public <T extends Operation<?, ?>> Class<? extends Strategy<T>> getStrategyType(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		return getStrategyTypeHelper(operationType, elementType);
	}

	/*
	 * It's safe to make this cast because we know #getOperationElementStrategy
	 * will return a Strategy<T> and getClass() returns the runtime type of the
	 * strategy object.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Operation<?, ?>> Class<? extends Strategy<T>> getStrategyTypeHelper(
			Class<? extends T> operationType, Class<? extends Element> elementType) {

		final Strategy<T> strategy = getOperationElementStrategy(operationType, elementType);
		return (Class<? extends Strategy<T>>) strategy.getClass();
	}

	@Override
	public String toString() {
		String output = "";
		for (Entry<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>> elementMap : table
				.entrySet()) {
			output += "Element type: " + elementMap.getKey().getSimpleName() + "\n";

			for (Entry<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap : elementMap
					.getValue().entrySet()) {

				output += String.format("\t%s -> %s\n", strategyMap.getKey().getSimpleName(), strategyMap.getValue());
			}
		}

		return output;
	}
}
