package element;

/**
 * Elements are objects that are instantiated with an integer value and an
 * {@link ElementType} that can be passed to other objects.
 * 
 * @author David
 */
public interface Element {

	/**
	 * Returns the value that the element was instantiated with.
	 * 
	 * @return this element's integer value
	 */
	int getValue();

	/**
	 * Returns which type of element this is, which can be used to determine how
	 * this node's value should be interpreted and used.
	 * 
	 * @return the type of this element
	 */
	ElementType getType();
}
