package tagc.strategytable.table;

import tagc.strategytable.operation.Operation;
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
		public <T extends Operation<?, ?>> Strategy<T> createDefaultStrategy() {
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
	DEFAULT {
		@Override
		public <T extends Operation<?, ?>> Strategy<T> createDefaultStrategy() {
			return new NullStrategy<T>();
		}
	};

	public abstract <T extends Operation<?, ?>> Strategy<T> createDefaultStrategy();
}
