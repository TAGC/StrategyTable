package tagc.strategytable.table;

import tagc.strategytable.operation.Operation;
import tagc.strategytable.strategy.DeferStrategy;
import tagc.strategytable.strategy.NullStrategy;
import tagc.strategytable.strategy.Strategy;
import tagc.strategytable.strategy.UnimplementedStrategy;

/**
 * A {@code StrategyTablePolicy} encapsulates a state of a {@link StrategyTable}
 * that determines how it handles cases such as operations being applied to
 * elements that no strategy has been explicitly configured to handle.
 * 
 * @author David
 */
public enum StrategyTablePolicy {
	/**
	 * This policy ensures that applying any operation to an element that no
	 * strategy has been explicitly declared to handle will result in an
	 * {@code UnsupportedOperationException} being thrown.
	 * 
	 * @author David
	 */
	STRICT {
		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultBaseStrategy() {
			return new UnimplementedStrategy<T>();
		}

		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultDecoratedStrategy() {
			return new UnimplementedStrategy<T>();
		}
	},
	/**
	 * This policy ensures that applying any operation to an element that no
	 * strategy has been explicitly declared to handle will result in no action
	 * being taken.
	 * 
	 * @author David
	 */
	IGNORE {
		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultBaseStrategy() {
			return new NullStrategy<T>();
		}

		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultDecoratedStrategy() {
			return new NullStrategy<T>();
		}
	},
	/**
	 * This policy will ensure that the particular action is taken when applying
	 * an operation to an element that no strategy has been explicitly declared
	 * to handle is based upon whether the element is decorated or not:
	 * <p>
	 * <ul>
	 * <li>{@link #createDefaultBaseStrategy} will return a strategy that does
	 * nothing</li>
	 * <li>{@link #createDefaultDecoratedStrategy} will return a strategy that
	 * defers application of an operation to its decoratee</li>
	 * </ul>
	 * 
	 * @author David
	 */
	DEFAULT {
		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultBaseStrategy() {
			return new NullStrategy<T>();
		}

		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultDecoratedStrategy() {
			return new DeferStrategy<T>();
		}
	};

	/**
	 * Returns the strategy that should by default be used to handle
	 * applications of operations to elements of a base type that no other
	 * strategies have been explicitly registered to handle.
	 * 
	 * @return the default strategy for base elements
	 */
	public abstract <T extends Operation<?, ?>> Strategy<T> createDefaultBaseStrategy();

	/**
	 * Returns the strategy that should by default be used to handle
	 * applications of operations to elements of a decorated type that no other
	 * strategies have been explicitly registered to handle.
	 * 
	 * @return the default strategy for decorated elements
	 */
	public abstract <T extends Operation<?, ?>> Strategy<T> createDefaultDecoratedStrategy();
}
