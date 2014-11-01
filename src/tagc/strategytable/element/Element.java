package tagc.strategytable.element;

/**
 * Elements are objects that are instantiated with an integer value that can be
 * passed to other objects. They can also declare whether they are decorated and
 * return references to undecorated versions of themselves.
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
	 * Returns whether this element is decorated or not i.e. whether this
	 * instance of {@code Element} is an element decorator or a base type of
	 * element.
	 * 
	 * @return {@code true} if this is a decorated element, otherwise
	 *         {@code false}
	 */
	boolean isDecorated();

	/**
	 * Returns a version of this element without any decoration. If this is an
	 * undecorated node, the element returns a reference to itself.
	 * 
	 * @return an undecorated version of this node
	 */
	Element withoutDecoration();

}
