package tagc.strategytable.table;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tagc.strategytable.element.Element;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.strategy.DeferStrategy;
import tagc.strategytable.strategy.NullStrategy;
import tagc.strategytable.strategy.Strategy;

public class StrategyTable {

	private final Set<Class<? extends Element>> decoratedElementClassSet;
	private final Map<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>> table;
	private final Map<Class<? extends Element>, Boolean> elementLockStates;
	private final Map<Class<? extends Operation<?, ?>>, Boolean> operationLockStates;

	/**
	 * Constructs and configures a {@code StrategyTable} which can associate
	 * strategies for any element of a type provided within
	 * {@code elementClassSet} and any operation of a type provided within
	 * {@code operationClassSet}.
	 * <p>
	 * A null strategy is initially configured to handle every combination of
	 * element type and operation type.
	 * 
	 * @param baseElementClassSet
	 *            a set containing the base types of {@code Element} for this
	 *            strategy table to handle
	 * @param decoratedElementClassSet
	 *            a set containing the decorated types of {@code Element} for
	 *            this strategy table to handle
	 * @param operationClassSet
	 *            a set containing the types of {@code Operation} for this
	 *            strategy table to handle
	 * @throws NullPointerException
	 *             if any of the class sets are null or the table policy are
	 *             {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code baseElementClassSet} and
	 *             {@code decoratedElementClassSet} are not disjoint
	 */
	public StrategyTable(Set<Class<? extends Element>> baseElementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		this(baseElementClassSet, decoratedElementClassSet, operationClassSet, StrategyTablePolicy.DEFAULT);
	}

	/**
	 * Constructs and configures a {@code StrategyTable} which can associate
	 * strategies for any element of a type provided within
	 * {@code elementClassSet} and any operation of a type provided within
	 * {@code operationClassSet}.
	 * <p>
	 * A default strategy will be initially configured to handle every
	 * combination of element type and operation type. The behaviour of this
	 * strategy depends upon {@code tablePolicy}.
	 * 
	 * @param baseElementClassSet
	 *            a set containing the base types of {@code Element} for this
	 *            strategy table to handle
	 * @param decoratedElementClassSet
	 *            a set containing the decorated types of {@code Element} for
	 *            this strategy table to handle
	 * @param operationClassSet
	 *            a set containing the types of {@code Operation} for this
	 *            strategy table to handle
	 * @param tablePolicy
	 *            the policy that this table should use
	 * @throws NullPointerException
	 *             if any of the class sets are null or the table policy are
	 *             {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code baseElementClassSet} and
	 *             {@code decoratedElementClassSet} are not disjoint
	 * @see StrategyTablePolicy
	 */
	public StrategyTable(Set<Class<? extends Element>> baseElementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet, StrategyTablePolicy tablePolicy) {

		if (tablePolicy == null)
			throw new NullPointerException("The table policy cannot be null");

		if (baseElementClassSet == null)
			throw new NullPointerException("The set of base element types cannot be null");

		if (decoratedElementClassSet == null)
			throw new NullPointerException("The set of decorated element types cannot be null");

		if (operationClassSet == null)
			throw new NullPointerException("The set of operation types cannot be null");

		if (!Collections.disjoint(baseElementClassSet, decoratedElementClassSet))
			throw new IllegalArgumentException("An element type cannot be both base and decorated");

		this.table = new HashMap<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>>();
		this.elementLockStates = new HashMap<Class<? extends Element>, Boolean>();
		this.operationLockStates = new HashMap<Class<? extends Operation<?, ?>>, Boolean>();
		this.decoratedElementClassSet = decoratedElementClassSet;

		final Set<Class<? extends Element>> combinedElementClassSet = new HashSet<Class<? extends Element>>(
				baseElementClassSet);
		combinedElementClassSet.addAll(decoratedElementClassSet);

		for (Class<? extends Element> elementClass : combinedElementClassSet) {
			elementLockStates.put(elementClass, false);

			final Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap;
			strategyMap = new HashMap<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>();

			for (Class<? extends Operation<?, ?>> operationClass : operationClassSet) {
				operationLockStates.put(operationClass, false);

				if (decoratedElementClassSet.contains(elementClass)) {
					strategyMap.put(operationClass, tablePolicy.createDefaultDecoratedStrategy());
				} else {
					strategyMap.put(operationClass, tablePolicy.createDefaultBaseStrategy());
				}
			}

			table.put(elementClass, strategyMap);
		}
	}

	/**
	 * Returns whether the strategy used to handle operations of type
	 * {@code operationType} for elements of the type {@code elementType} has
	 * been locked in. This is the case if strategies have been locked in for
	 * either all operations of type {@code operationType} or all elements of
	 * type {@code elementType}.
	 * 
	 * @param operationType
	 *            the type of operation associated with the strategy to test is
	 *            locked locked for
	 * @param elementType
	 *            the type of element associated with the strategy to test is
	 *            locked
	 * @return {@code true} if and only if {@link #isOperationLocked} {@code ||}
	 *         {@link #isElementLocked}
	 * @throws NullPointerException
	 *             if {@code operationType} or {@code elementType} are
	 *             {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType} or operations of type
	 *             {@code operationType}
	 */
	public boolean isStrategyLocked(Class<? extends Operation<?, ?>> operationType, Class<? extends Element> elementType) {
		return isOperationLocked(operationType) || isElementLocked(elementType);
	}

	/**
	 * Returns whether the types of strategies for operations of type
	 * {@code operationType} have been locked in. If a strategy is locked in,
	 * future operations to change it will fail unless the strategy is
	 * explicitly unlocked first.
	 * 
	 * @param operationType
	 *            the type of operation to test whether strategies have been
	 *            locked for
	 * @return {@code true} if strategies have been locked for operations of
	 *         type {@code operationType}, otherwise {@code false}
	 * @throws NullPointerException
	 *             if {@code operationType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 */
	public boolean isOperationLocked(Class<? extends Operation<?, ?>> operationType) {
		if (operationType == null)
			throw new NullPointerException("The operation type cannot be null");

		if (!operationLockStates.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		return operationLockStates.get(operationType);
	}

	/**
	 * Returns whether the types of strategies for elements of type
	 * {@code elementType} have been locked in. If a strategy is locked in,
	 * future operations to change it will fail unless the strategy is
	 * explicitly unlocked first.
	 * 
	 * @param elementType
	 *            the type of element to test whether strategies have been
	 *            locked for
	 * @return {@code true} if strategies have been locked for elements of type
	 *         {@code elementType}, otherwise {@code false}
	 * @throws NullPointerException
	 *             if {@code operationType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */
	public boolean isElementLocked(Class<? extends Element> elementType) {
		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

		if (!elementLockStates.containsKey(elementType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support elements of type "
							+ elementType.getSimpleName());

		return elementLockStates.get(elementType);
	}

	/**
	 * Sets whether the strategies associated with a particular type of
	 * operation should be locked in or not.
	 * 
	 * @param operationType
	 *            the type of operation to lock or unlock strategies for
	 * @param locked
	 *            {@code true} to lock in the strategies for
	 *            {@code operationType}, {@code false} to unlock them
	 * @throws NullPointerException
	 *             if {@code operationType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 */
	public void setOperationStrategiesLocked(Class<? extends Operation<?, ?>> operationType, boolean locked) {
		if (operationType == null)
			throw new NullPointerException("The operation type cannot be null");

		if (!operationLockStates.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		operationLockStates.put(operationType, locked);
	}

	/**
	 * Sets whether the strategies associated with a particular type of element
	 * should be locked in or not.
	 * 
	 * @param elementType
	 *            the type of element to lock or unlock strategies for
	 * @param locked
	 *            {@code true} to lock in the strategies for {@code elementType}
	 *            , {@code false} to unlock them
	 * @throws NullPointerException
	 *             if {@code elementType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */
	public void setElementStrategiesLocked(Class<? extends Element> elementType, boolean locked) {
		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

		if (!elementLockStates.containsKey(elementType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support elements of type "
							+ elementType.getSimpleName());

		elementLockStates.put(elementType, locked);
	}

	/**
	 * Registers {@code strategy} to be used to handle operations of the type
	 * {@code operationType} for elements of the type {@code elementType}.
	 * <p>
	 * In other words, this method specifies that {@code strategy} should be
	 * executed using an operation {@code o} and an element {@code e} if
	 * {@code o.getClass().equals(operationType)} and
	 * {@code e.getClass().equals(elementType)}.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
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
	 * @return {@code true} if the strategy was successfully registered,
	 *         otherwise {@code false}.
	 * @throws NullPointerException
	 *             if {@code operationType}, {@code elementType} or
	 *             {@code strategy} are {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType} or operations of type
	 *             {@code operationType}
	 * @see #isStrategyLocked(Class, Class)
	 */
	public <T extends Operation<?, ?>> boolean addOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType, Strategy<T> strategy) {

		if (operationType == null)
			throw new NullPointerException("The operation type cannot be null");

		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

		if (strategy == null)
			throw new NullPointerException("The strategy cannot be null");

		if (isStrategyLocked(operationType, elementType))
			return false;

		putOperationStrategy(operationType, elementType, strategy);
		return true;
	}

	/**
	 * Registers {@code strategy} to be used to handle operations of the type
	 * {@code operationType} for every type of element that this strategy table
	 * is configured to work for.
	 * <p>
	 * In other words, this method specifies that {@code strategy} should be
	 * executed using an operation {@code o} and any element {@code e} if
	 * {@code o.getClass().equals(operationType)}.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
	 * 
	 * @param operationType
	 *            the {@code class} of {@code Operation} for {@code strategy} to
	 *            handle
	 * @param strategy
	 *            a {@code Strategy} object to handle execution of an operation
	 *            on an element
	 * @return {@code true} if the strategy registration process succeeded for
	 *         all types of element that this strategy table is configured to
	 *         work for, otherwise {@code false}
	 * @throws NullPointerException
	 *             if {@code operationType} or {@code strategy} are {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 * @see #addOperationStrategy(Class, Class, Strategy)
	 * @see #isStrategyLocked(Class, Class)
	 */
	public <T extends Operation<?, ?>> boolean addOperationStrategies(Class<? extends T> operationType,
			Strategy<T> strategy) {
		boolean totalSuccess = true;

		for (Class<? extends Element> elementType : table.keySet()) {
			final boolean success = addOperationStrategy(operationType, elementType, strategy);
			totalSuccess &= success;
		}

		return totalSuccess;
	}

	/**
	 * Registers a 'null' strategy to be used to handle operations of the type
	 * {@code operationType} for elements of the type {@code elementType}.
	 * <p>
	 * A null strategy conforms to the interface for a {@link Strategy} but
	 * performs no actions when executed. These strategies will leave the state
	 * of an {@code Operation} object unchanged.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
	 * 
	 * @param operationType
	 *            the {@code class} of {@code Operation} for the null strategy
	 *            to handle
	 * @param elementType
	 *            the {@code class} of {@code Element} for the null strategy to
	 *            handle
	 * @return {@code true} if the null strategy was successfully registered,
	 *         otherwise {@code false}.
	 * @throws NullPointerException
	 *             if {@code operationType} or {@code elementType} are
	 *             {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType} or operations of type
	 *             {@code operationType}
	 * @see #isStrategyLocked(Class, Class)
	 */
	public <T extends Operation<?, ?>> boolean addNullOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		if (operationType == null)
			throw new NullPointerException("The operation type cannot be null");

		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

		return putOperationStrategy(operationType, elementType, StrategyTable.<T> createNullStrategy());
	}

	/**
	 * Registers a 'null' strategy to be used to handle operations of the type
	 * {@code operationType} for every type of element that this strategy table
	 * is configured to work for.
	 * <p>
	 * A null strategy conforms to the interface for a {@link Strategy} but
	 * performs no actions when executed. These strategies will leave the state
	 * of an {@code Operation} object unchanged.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
	 * 
	 * @param operationType
	 *            the {@code class} of {@code Operation} for the null strategy
	 *            to handle
	 * @throws NullPointerException
	 *             if {@code operationType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             operations of type {@code operationType}
	 * @return {@code true} if the strategy registration process succeeded for
	 *         all types of element that this strategy table is configured to
	 *         work for, otherwise {@code false}
	 * @see #addNullOperationStrategy(Class, Class)
	 * @see #isStrategyLocked(Class, Class)
	 */
	public <T extends Operation<?, ?>> boolean addNullOperationStrategies(Class<? extends T> operationType) {
		boolean success = true;

		for (Class<? extends Element> elementType : table.keySet()) {
			success = addNullOperationStrategy(operationType, elementType) && success;
		}

		return success;
	}

	/**
	 * Registers a null strategy to be used to handle every operation on
	 * elements of type {@code elementType}.
	 * <p>
	 * In other words, this method specifies that no actions should be performed
	 * by any operation that acts on an element {@code e} if
	 * {@code e.getClass().equals(elementType)}. Later calls to
	 * {@link #addOperationStrategy} can be used to replace null strategies for
	 * certain operations if desired.
	 * <p>
	 * One example of where it may be appropriate to call this method on a type
	 * of element that should be ignored by most or all operations.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
	 * 
	 * @param elementType
	 *            the {@code class} of {@code Element} to be ignored by
	 *            operations
	 * @return {@code true} if the strategy registration process succeeded for
	 *         all types of operations that this strategy table is configured to
	 *         work for, otherwise {@code false}
	 * @throws NullPointerException
	 *             if {@code elementType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */
	public boolean addNullElementStrategies(Class<? extends Element> elementType) {
		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

		boolean success = true;
		for (Class<? extends Operation<?, ?>> operationType : getStrategyMap(elementType).keySet()) {
			success = putOperationStrategy(operationType, elementType, createNullStrategy()) && success;
		}

		return success;
	}

	/**
	 * Registers a defer strategy to be used to handle every operation on
	 * elements of the element decorator type {@code elementDecoratorType}.
	 * <p>
	 * In other words, for any element {@code e} where
	 * {@code e.getClass().equals(elementDecoratorType)}, successful
	 * registration with this method will mean that any operation that attempts
	 * to be applied to {@code e} will be applied to the {@code Element} that
	 * {@code e} wraps instead. Later calls to {@link #addOperationStrategy} can
	 * be used to replace defer strategies for certain operations if desired.
	 * <p>
	 * Strategies will only be successfully registered if the existing strategy
	 * associated with {@code operationType} and {@code elementType} is not
	 * locked in.
	 * 
	 * @param elementDecoratorType
	 *            the {@code class} of the decorator-type {@code Element} to
	 *            have operations deferred to the decoratee of
	 * @return {@code true} if the strategy registration process succeeded for
	 *         all types of operations that this strategy table is configured to
	 *         work for, otherwise {@code false}
	 * @throws NullPointerException
	 *             if {@code elementType} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code elementDecoratorType} has not been registered as
	 *             the type of an element decorator
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 */
	public boolean addDeferElementStrategies(Class<? extends Element> elementDecoratorType) {
		if (elementDecoratorType == null)
			throw new NullPointerException("The element type cannot be null");

		if (!decoratedElementClassSet.contains(elementDecoratorType))
			throw new IllegalArgumentException("Only elements of a decorator type can use defer strategies");

		boolean success = true;
		for (Class<? extends Operation<?, ?>> operationType : getStrategyMap(elementDecoratorType).keySet()) {
			success = putOperationStrategy(operationType, elementDecoratorType, createDeferStrategy()) && success;
		}

		return success;
	}

	private static <T extends Operation<?, ?>> Strategy<T> createNullStrategy() {
		return new NullStrategy<T>();
	}

	private static <T extends Operation<?, ?>> Strategy<T> createDeferStrategy() {
		return new DeferStrategy<T>();
	}

	private Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> getStrategyMap(
			Class<? extends Element> elementType) {
		if (!table.containsKey(elementType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support elements of type "
							+ elementType.getSimpleName());

		return table.get(elementType);
	}

	private <T extends Operation<?, ?>> void putOperationStrategyHelper(
			Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap,
			Class<? extends T> operationType, Strategy<T> strategy) {

		if (!strategyMap.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		strategyMap.put(operationType, strategy);
	}

	private <T extends Operation<?, ?>> boolean putOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType, Strategy<T> strategy) {
		if (isStrategyLocked(operationType, elementType))
			return false;

		putOperationStrategyHelper(getStrategyMap(elementType), operationType, strategy);
		return true;
	}

	/*
	 * We know that this is a safe cast because #putOperationStrategy is
	 * typesafe and is the only way a strategy can be associated with an
	 * operation.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Operation<?, ?>> Strategy<T> getOperationStrategyHelper(
			Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap,
			Class<? extends T> operationType) {

		if (!strategyMap.containsKey(operationType))
			throw new IllegalArgumentException(
					"This strategy table has not been configured to support operations of type "
							+ operationType.getSimpleName());

		return (Strategy<T>) strategyMap.get(operationType);
	}

	private <T extends Operation<?, ?>> Strategy<T> getOperationStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		return getOperationStrategyHelper(getStrategyMap(elementType), operationType);
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
	 * @throws NullPointerException
	 *             if {@code operation} or {@code element} are {@code null}
	 * @throws UnsupportedOperationException
	 *             if there is no explicitly registered {@code Strategy} for the
	 *             types of {@code operation} and {@code element} and this
	 *             strategy table's {@code policy} is set to {@code Strict}.
	 */
	public <T extends Operation<?, ?>> void operate(T operation, Element element) {

		if (operation == null)
			throw new NullPointerException("The operation cannot be null");

		if (element == null)
			throw new NullPointerException("The element cannot be null");

		/*
		 * It's safe to make this cast because #getClass() will return the
		 * runtime type of the operation which we can guarantee the type of.
		 */
		@SuppressWarnings("unchecked")
		final Class<? extends T> operationType = (Class<? extends T>) operation.getClass();
		final Class<? extends Element> elementType = element.getClass();
		final Strategy<T> strategy = getOperationStrategy(operationType, elementType);

		if (strategy == null)
			throw new IllegalArgumentException(
					"There are no strategies defined to work with this type of operation for this element");

		strategy.execute(operation, element, this);
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
	 * @throws NullPointerException
	 *             if {@code operation} or {@code elements} are {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType}
	 * @throws UnsupportedOperationException
	 *             if there is no explicitly registered {@code Strategy} for the
	 *             types of {@code operation} and {@code element} and this
	 *             strategy table's policy is set to
	 *             {@link StrategyTablePolicy#STRICT}
	 */
	public <T extends Operation<?, ?>> void operateOverCollection(T operation, Collection<? extends Element> elements) {
		if (operation == null)
			throw new NullPointerException("The operation cannot be null");

		if (elements == null)
			throw new NullPointerException("The collection of elements cannot be null");

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
	 * @throws NullPointerException
	 *             if {@code operationType} or {@code elementType} are
	 *             {@code null}
	 * @throws IllegalArgumentException
	 *             if this strategy table has not been configured to support
	 *             elements of type {@code elementType} and operations of type
	 *             {@code operationType}
	 */
	public <T extends Operation<?, ?>> Class<? extends Strategy<T>> getStrategy(Class<? extends T> operationType,
			Class<? extends Element> elementType) {

		if (operationType == null)
			throw new NullPointerException("The operation type cannot be null");

		if (elementType == null)
			throw new NullPointerException("The element type cannot be null");

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

		final Strategy<T> strategy = getOperationStrategy(operationType, elementType);
		return (Class<? extends Strategy<T>>) strategy.getClass();
	}

	@Override
	public String toString() {
		String output = "Strategy table\n";
		for (Entry<Class<? extends Element>, Map<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>>> elementMap : table
				.entrySet()) {
			output += "\nElement type: " + elementMap.getKey().getSimpleName() + "\n";

			for (Entry<Class<? extends Operation<?, ?>>, Strategy<? extends Operation<?, ?>>> strategyMap : elementMap
					.getValue().entrySet()) {

				output += String.format("\t* %s -> %s\n", strategyMap.getKey().getSimpleName(), strategyMap.getValue());
			}
		}

		return output;
	}
}
