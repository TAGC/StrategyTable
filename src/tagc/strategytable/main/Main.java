package tagc.strategytable.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern EXECUTE_PATTERN = Pattern.compile("^\\s*go\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern QUIT_PATTERN = Pattern.compile("^\\s*quit\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern INFO_PATTERN = Pattern.compile("^\\s*info\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern USE_ILLEGAL_STRATEGY_TABLE = Pattern.compile("^\\s*st illegal\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern USE_NULL_STRATEGY_TABLE = Pattern.compile("^\\s*st no defer\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern USE_DEFAULT_STRATEGY_TABLE = Pattern.compile("^\\s*st default\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern USE_BYPASS_STRATEGY_TABLE = Pattern.compile("^\\s*st bypass\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern CREATE_ADD_ELEMENT_PATTERN = Pattern.compile("^add\\s*(?<value>\\d+)\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern CREATE_MULT_ELEMENT_PATTERN = Pattern.compile("^mult\\s*(?<value>\\d+)\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern INSTALL_IGNORE_DECORATOR_PATTERN = Pattern.compile("^ignore\\s*(?<index>\\d+)\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern INSTALL_REVERSE_DECORATOR_PATTERN = Pattern.compile("^reverse\\s*(?<index>\\d+)\\s*$",
			Pattern.CASE_INSENSITIVE);

	private static final Scanner SCANNER = new Scanner(System.in);

	private enum StrategyTableType {
		ILLEGAL {
			@Override
			StrategyTable createTable(Set<Class<? extends Element>> baseElementClassSet,
					Set<Class<? extends Element>> decoratedElementClassSet,
					Set<Class<? extends Operation<?, ?>>> operationClassSet) {
				return setupIllegalStrategyTable(baseElementClassSet, decoratedElementClassSet, operationClassSet);
			}
		},
		NULL {
			@Override
			StrategyTable createTable(Set<Class<? extends Element>> baseElementClassSet,
					Set<Class<? extends Element>> decoratedElementClassSet,
					Set<Class<? extends Operation<?, ?>>> operationClassSet) {
				return setupNullStrategyTable(baseElementClassSet, decoratedElementClassSet, operationClassSet);
			}
		},
		DEFAULT {
			@Override
			StrategyTable createTable(Set<Class<? extends Element>> baseElementClassSet,
					Set<Class<? extends Element>> decoratedElementClassSet,
					Set<Class<? extends Operation<?, ?>>> operationClassSet) {
				return setupDefaultStrategyTable(baseElementClassSet, decoratedElementClassSet, operationClassSet);
			}
		},
		BYPASS {
			@Override
			StrategyTable createTable(Set<Class<? extends Element>> baseElementClassSet,
					Set<Class<? extends Element>> decoratedElementClassSet,
					Set<Class<? extends Operation<?, ?>>> operationClassSet) {
				return setupBypassStrategyTable(baseElementClassSet, decoratedElementClassSet, operationClassSet);
			}
		};

		abstract StrategyTable createTable(Set<Class<? extends Element>> baseElementClassSet,
				Set<Class<? extends Element>> decoratedElementClassSet,
				Set<Class<? extends Operation<?, ?>>> operationClassSet);
	}

	public static void main(String[] args) {
		demonstrate();
	}

	private static void demonstrate() {
		System.out.println("This is a demonstration of a new design pattern I've been trying to develop"
				+ " that I'm calling Strategy Table.\nIt's meant to act primarily as an augmented version"
				+ " of the Visitor pattern that allows both the element class hiearchy and the operation"
				+ "/functionality\nclass hierarchy to vary without the need to modify any existing code"
				+ " within elements or operations.\nThis overcomes a limitation of the Visitor pattern in"
				+ " which any change to the element hierarchy would necessitate modifying the Visitor"
				+ " interface and all concrete visitors.");

		System.out.println("\nThis demonstration consists of an element hierarchy consisting of two base"
				+ " element types and one element decorator:\n" + "\tAddElement (base)\n" + "\tMultElement (base)\n"
				+ "\tIgnoreElementDecorator (decorator)\n"
				+ "An element in this case is an immutable object that can store an integer value.");

		System.out.println("\nTo demonstrate this design pattern, you can construct a collection of"
				+ " these elements and choose a type of strategy table to employ them on.\nAfter"
				+ " setting up the element collection and choosing a type of strategy table, you"
				+ " can execute the demonstration. The commands are:\n"
				+ "\t'add <<integer value>>' to add a new AddElement with the given value\n"
				+ "\t'mult <<integer value>>' to add a new MultElement with the given value\n"
				+ "\t'ignore <<index>>' to decorate the element at the given index with an IgnoreElementDecorator\n"
				+ "\t'reverse <<index>>' to decorate the element at the given index with a ReverseElementDecorator\n"
				+ "\t'st illegal' to choose the illegal strategy table\n"
				+ "\t'st null' to choose the null strategy table\n"
				+ "\t'st default' to choose the default strategy table\n"
				+ "\t'st bypass' to choose the bypass strategy table\n"
				+ "\t'info' to print out the current setup\n"
				+ "\t'go' to run the demonstration with the current setup\n" + "\t'quit' to close this application\n"
				+ "All commands are case insensitive.");

		System.out.println("\nPlease enter your first command:");
		interpretInput(SCANNER.nextLine(), new ArrayList<Element>(), StrategyTableType.DEFAULT);
	}

	// ================================================================================
	// User Interface
	// ================================================================================

	private static void interpretInput(String input, List<Element> elements, StrategyTableType tableType) {
		Matcher m;

		if ((m = QUIT_PATTERN.matcher(input)) != null && m.matches()) {
			System.out.println("Exiting...");
			System.exit(0);
		}

		else if ((m = INFO_PATTERN.matcher(input)) != null && m.matches()) {
			printInfo(elements, tableType);
			interpretInput(SCANNER.nextLine(), elements, tableType);
		}

		else if ((m = EXECUTE_PATTERN.matcher(input)) != null && m.matches()) {
			System.out.println("Executing...");
			execute(elements, tableType);
		}

		else if ((m = USE_ILLEGAL_STRATEGY_TABLE.matcher(input)) != null && m.matches()) {
			final StrategyTableType newTableType = StrategyTableType.ILLEGAL;
			System.out.println("Setting strategy table: " + newTableType);
			interpretInput(SCANNER.nextLine(), elements, newTableType);
		}
		
		else if ((m = USE_NULL_STRATEGY_TABLE.matcher(input)) != null && m.matches()) {
			final StrategyTableType newTableType = StrategyTableType.NULL;
			System.out.println("Setting strategy table: " + newTableType);
			interpretInput(SCANNER.nextLine(), elements, newTableType);
		}

		else if ((m = USE_DEFAULT_STRATEGY_TABLE.matcher(input)) != null && m.matches()) {
			final StrategyTableType newTableType = StrategyTableType.DEFAULT;
			System.out.println("Setting strategy table: " + newTableType);
			interpretInput(SCANNER.nextLine(), elements, newTableType);
		}

		else if ((m = USE_BYPASS_STRATEGY_TABLE.matcher(input)) != null && m.matches()) {
			final StrategyTableType newTableType = StrategyTableType.BYPASS;
			System.out.println("Setting strategy table: " + newTableType);
			interpretInput(SCANNER.nextLine(), elements, newTableType);
		}

		else if ((m = CREATE_ADD_ELEMENT_PATTERN.matcher(input)) != null && m.matches()) {
			final int value = Integer.parseInt(m.group("value"));
			final Element newElement = ElementFactory.createAddElement(value);
			elements.add(newElement);
			System.out.println("Added " + newElement + "\nNew elements: " + elements);
			interpretInput(SCANNER.nextLine(), elements, tableType);
		}

		else if ((m = CREATE_MULT_ELEMENT_PATTERN.matcher(input)) != null && m.matches()) {
			final int value = Integer.parseInt(m.group("value"));
			final Element newElement = ElementFactory.createMultElement(value);
			elements.add(newElement);
			System.out.println("Added " + newElement + "\nNew elements: " + elements);
			interpretInput(SCANNER.nextLine(), elements, tableType);
		}

		else if ((m = INSTALL_IGNORE_DECORATOR_PATTERN.matcher(input)) != null && m.matches()) {
			final int index = Integer.parseInt(m.group("index"));
			if (index < 0 || index >= elements.size()) {
				System.out.printf("%d is not a valid index (elements: %s)\n", index, elements);
				interpretInput(SCANNER.nextLine(), elements, tableType);
			} else {
				Element e = elements.remove(index);
				elements.add(index, ElementFactory.addIgnoreDecoration(e));
				System.out.println("Added ignore decoration to " + e + "\nNew elements: " + elements);
				interpretInput(SCANNER.nextLine(), elements, tableType);
			}
		}

		else if ((m = INSTALL_REVERSE_DECORATOR_PATTERN.matcher(input)) != null && m.matches()) {
			final int index = Integer.parseInt(m.group("index"));
			if (index < 0 || index >= elements.size()) {
				System.out.printf("%d is not a valid index (elements: %s)\n", index, elements);
				interpretInput(SCANNER.nextLine(), elements, tableType);
			} else {
				Element e = elements.remove(index);
				elements.add(index, ElementFactory.addReverseDecoration(e));
				System.out.println("Added reverse decoration to " + e + "\nNew elements: " + elements);
				interpretInput(SCANNER.nextLine(), elements, tableType);
			}
		}

		else {
			System.out.println("Unrecognised command: " + input);
			interpretInput(SCANNER.nextLine(), elements, tableType);
		}
	}

	private static void printInfo(List<Element> elements, StrategyTableType tableType) {
		String info = String.format("Element count: %s\nElements: %s\nCurrent strategy table type: %s\n",
				elements.size(), elements, tableType);
		System.out.println(info);
	}

	// ================================================================================
	// Execution
	// ================================================================================

	private static void execute(List<Element> elements, StrategyTableType tableType) {
		final Set<Class<? extends Element>> baseElementClassSet = getBaseElementClassSet();
		final Set<Class<? extends Element>> decoratedElementClassSet = getDecoratedElementClassSet();
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = getOperationClassSet();
		final StrategyTable table = tableType.createTable(baseElementClassSet, decoratedElementClassSet,
				operationClassSet);

		printInfo(elements, tableType);
		System.out.println(table);

		final PureOperation<Integer> totalOperation = new FindTotalOperation();
		final PureOperation<Integer> countOperation = new CountElementOperation();
		table.operateOverCollection(totalOperation, elements);
		table.operateOverCollection(countOperation, elements);

		System.out.println("\nOperation results");
		System.out.println("Total: " + totalOperation.get());
		System.out.println("Element count: " + countOperation.get());

		System.exit(0);
	}

	// ================================================================================
	// Element/Operation Types
	// ================================================================================

	private static Set<Class<? extends Element>> getBaseElementClassSet() {
		final Set<Class<? extends Element>> baseElementClassSet = new HashSet<Class<? extends Element>>();

		baseElementClassSet.add(ElementFactory.getAddElementClass());
		baseElementClassSet.add(ElementFactory.getMultElementClass());
		return baseElementClassSet;
	}

	private static Set<Class<? extends Element>> getDecoratedElementClassSet() {
		final Set<Class<? extends Element>> decoratedElementClassSet = new HashSet<Class<? extends Element>>();

		decoratedElementClassSet.add(ElementFactory.getIgnoreElementDecoratorClass());
		decoratedElementClassSet.add(ElementFactory.getReverseElementDecoratorClass());
		return decoratedElementClassSet;
	}

	private static Set<Class<? extends Operation<?, ?>>> getOperationClassSet() {
		final Set<Class<? extends Operation<?, ?>>> operationClassSet = new HashSet<Class<? extends Operation<?, ?>>>();

		operationClassSet.add(FindTotalOperation.class);
		operationClassSet.add(CountElementOperation.class);

		return operationClassSet;
	}

	// ================================================================================
	// Strategy Table Configurations
	// ================================================================================

	private static StrategyTable setupIllegalStrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet, decoratedElementClassSet,
				operationClassSet, StrategyTablePolicy.STRICT);

		/*
		 * Locks in current strategy which is illegally left unspecified.
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getIgnoreElementDecoratorClass(), true);

		/*
		 * We omit explicit registration of strategies for
		 * ReverseElementDecorator. This is illegal.
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getReverseElementDecoratorClass(), true);

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

	private static StrategyTable setupNullStrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet, decoratedElementClassSet,
				operationClassSet, StrategyTablePolicy.NULL);

		/*
		 * Locks in current strategy but does not specify a strategy to use
		 * (which is legal in this case).
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getIgnoreElementDecoratorClass(), true);

		/*
		 * We omit explicit registration of strategies for
		 * ReverseElementDecorator. This will cause operations to ignore
		 * elements wrapped in this decorator.
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getReverseElementDecoratorClass(), true);

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

	private static StrategyTable setupDefaultStrategyTable(Set<Class<? extends Element>> elementClassSet,
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
		 * We omit explicit registration of strategies for
		 * ReverseElementDecorator. This will cause strategies to be applied to
		 * the wrapped element by default, as we would like.
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getReverseElementDecoratorClass(), true);

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

	private static StrategyTable setupBypassStrategyTable(Set<Class<? extends Element>> elementClassSet,
			Set<Class<? extends Element>> decoratedElementClassSet,
			Set<Class<? extends Operation<?, ?>>> operationClassSet) {
		final StrategyTable strategyTable = new StrategyTable(elementClassSet, decoratedElementClassSet,
				operationClassSet, StrategyTablePolicy.BYPASS);

		/*
		 * We omit explicit registration of strategies for
		 * IgnoreElementDecorator and ReverseElementDecorator. This will cause
		 * strategies to be applied to the wrapped element using the strategy
		 * appropriate for the wrapped element i.e. the decorator will be
		 * bypassed completely.
		 */
		strategyTable.setElementStrategiesLocked(ElementFactory.getIgnoreElementDecoratorClass(), true);
		strategyTable.setElementStrategiesLocked(ElementFactory.getReverseElementDecoratorClass(), true);

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

}
