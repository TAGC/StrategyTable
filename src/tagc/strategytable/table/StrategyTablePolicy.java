package tagc.strategytable.table;

import tagc.strategytable.operation.Operation;
import tagc.strategytable.strategy.NullStrategy;
import tagc.strategytable.strategy.SubstituteStrategy;
import tagc.strategytable.strategy.Strategy;
import tagc.strategytable.strategy.BypassStrategy;
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
	 * being taken. In this case, base element types and decorated element types
	 * are treated equally; the default strategy in both cases will be to take
	 * no action.
	 * 
	 * @author David
	 */
	NULL {
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
	 * applies the decorator to its decoratee's corresponding strategy. In other
	 * words, the ultimately chosen strategy will be based on the
	 * <i>decoratee's</i> type but the element used with the strategy is the
	 * <i>decorator</i></li> itself.
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
			return new SubstituteStrategy<T>();
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
	 * applies the decorator's decoratee to the decoratee's corresponding
	 * strategy. In other words, the ultimately chosen strategy will be based on
	 * the <i>decoratee's</i> type and the decoratee will be applied to the
	 * strategy as well.</li>
	 * </ul>
	 * 
	 * @author David
	 */
	BYPASS {
		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultBaseStrategy() {
			return new NullStrategy<T>();
		}

		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultDecoratedStrategy() {
			return new BypassStrategy<T>();
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
