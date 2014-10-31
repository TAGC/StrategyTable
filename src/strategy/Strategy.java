package strategy;

import operation.Operation;
import element.Element;

/**
 * Strategies encapsulate a particular method for applying an {@link Operation}
 * to a given type of {@link Element}.
 * 
 * @author David
 * @param <T>
 *            the specific type of Operation to handle
 */
public interface Strategy<T extends Operation<?, ?>> {

	/**
	 * Handles the execution of an operation on a particular kind of element.
	 * 
	 * @param operation
	 *            the {@code Operation} to perform on the element
	 * @param element
	 *            the {@code Element} to apply the operation on
	 */
	void execute(T operation, Element element);
}
