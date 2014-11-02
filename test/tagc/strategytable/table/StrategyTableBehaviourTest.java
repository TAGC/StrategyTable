package tagc.strategytable.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tagc.strategytable.element.Element;
import tagc.strategytable.element.ElementFactory;
import tagc.strategytable.operation.CountElementOperation;
import tagc.strategytable.operation.FindTotalOperation;
import tagc.strategytable.operation.Operation;
import tagc.strategytable.operation.PureOperation;
import tagc.strategytable.strategy.CountElementOperationStrategy;
import tagc.strategytable.strategy.Strategy;

public class StrategyTableBehaviourTest {
	private static final Set<Class<? extends Element>> BASE_ELEMENTS = new HashSet<Class<? extends Element>>(
			Arrays.asList(ElementFactory.getAddElementClass(), ElementFactory.getMultElementClass()));

	private static final Set<Class<? extends Element>> DECORATIONS = new HashSet<Class<? extends Element>>(
			Arrays.asList(ElementFactory.getIgnoreElementDecoratorClass(),
					ElementFactory.getReverseElementDecoratorClass()));

	private static final Set<Class<? extends Operation<?, ?>>> OPERATIONS = new HashSet<Class<? extends Operation<?, ?>>>(
			Arrays.asList(FindTotalOperation.class, CountElementOperation.class));

	private static final Strategy<UnregisteredOperation> UNREGISTED_OPERATION_STRATEGY = new Strategy<UnregisteredOperation>() {
		@Override
		public void execute(UnregisteredOperation operation, Element element, int decorationLevel, StrategyTable table) {

		}
	};

	private static class UnregisteredOperation implements PureOperation<Object> {
		@Override
		public void store(Object data) {
		}

		@Override
		public Object get() {
			return null;
		}
	}

	private static class UnregisteredElement implements Element {
		@Override
		public int getValue() {
			return 0;
		}

		@Override
		public boolean isDecorated() {
			return false;
		}

		@Override
		public int getDecorationLevel() {
			return 0;
		}

		@Override
		public Element asDecorationAtLevel(int decorationLevel) {
			return null;
		}
	};

	private StrategyTable table;

	// ================================================================================
	// Null Pointer Exception Tests
	// ================================================================================

	@Test(expected = NullPointerException.class)
	public void creatingTableWithNullBaseElementClassSetShouldBeIllegal() {
		table = new StrategyTable(null, DECORATIONS, OPERATIONS);
	}

	@Test(expected = NullPointerException.class)
	public void creatingTableWithNullDecoratedElementClassSetShouldBeIllegal() {
		table = new StrategyTable(BASE_ELEMENTS, null, OPERATIONS);
	}

	@Test(expected = NullPointerException.class)
	public void creatingTableWithNullOperationClassSetShouldBeIllegal() {
		table = new StrategyTable(BASE_ELEMENTS, DECORATIONS, null);
	}

	@Test(expected = NullPointerException.class)
	public void registerStrategyWithNullOperationTypeShouldBeIllegal() {
		table.registerOperationStrategies(null, new CountElementOperationStrategy());
	}

	@Test(expected = NullPointerException.class)
	public void getStrategyWithNullOperationTypeShouldBeIllegal() {
		table.getOperationStrategy(null, ElementFactory.getAddElementClass());
	}

	@Test(expected = NullPointerException.class)
	public void getStrategyWithNullElementTypeShouldBeIllegal() {
		table.getOperationStrategy(FindTotalOperation.class, null);
	}

	@Test(expected = NullPointerException.class)
	public void operateWithNullOperationShouldBeIllegal() {
		final Element e = ElementFactory.createAddElement(1);
		table.operate(null, e);
	}

	@Test(expected = NullPointerException.class)
	public void operateWithNullElementShouldBeIllegal() {
		final PureOperation<?> o = new FindTotalOperation();
		table.operate(o, null);
	}

	// ================================================================================
	// Illegal argument exception tests
	// ================================================================================

	@Test(expected = IllegalArgumentException.class)
	public void creatingTableWithOverlappingElementClassSetsShouldBeIllegal() {
		Set<Class<? extends Element>> badElements = new HashSet<Class<? extends Element>>(BASE_ELEMENTS);
		badElements.add(ElementFactory.getIgnoreElementDecoratorClass());
		table = new StrategyTable(badElements, DECORATIONS, OPERATIONS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringStrategyForUnknownOperationShouldBeIllegal() {
		table.registerOperationStrategies(UnregisteredOperation.class, UNREGISTED_OPERATION_STRATEGY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringStrategyForUnknownElementShouldBeIllegal() {
		table.registerOperationStrategy(CountElementOperation.class, UnregisteredElement.class,
				new CountElementOperationStrategy());
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringSubstituteStrategyForBaseElementShouldBeIllegal() {
		table.registerSubstituteElementStrategies(ElementFactory.getAddElementClass());
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringBypassStrategyForBaseElementShouldBeIllegal() {
		table.registerBypassElementStrategies(ElementFactory.getAddElementClass());
	}

	// ================================================================================
	// Assertion checks
	// ================================================================================

	@Test
	public void overwritingUnlockedStrategyShouldSucceed() {
		assertTrue(table.registerOperationStrategies(CountElementOperation.class, new CountElementOperationStrategy()));
		assertTrue(table.registerNullOperationStrategy(CountElementOperation.class,
				ElementFactory.getIgnoreElementDecoratorClass()));
	}

	@Test
	public void overwritingLockedStrategyShouldFail() {
		assertTrue(table.registerOperationStrategies(CountElementOperation.class, new CountElementOperationStrategy()));
		table.setOperationStrategiesLocked(CountElementOperation.class, true);
		assertFalse(table.registerNullOperationStrategy(CountElementOperation.class,
				ElementFactory.getIgnoreElementDecoratorClass()));
	}

	// ================================================================================
	// Setup/teardown
	// ================================================================================

	@Before
	public void setUp() {
		table = new StrategyTable(BASE_ELEMENTS, DECORATIONS, OPERATIONS);
	}

	@After
	public void tearDown() {
		table = null;
	}
}
