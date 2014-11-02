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
	 * Returns the number of decorations wrapped around the base element at the
	 * bottom of the decoration stack, including this if this is a decoration.
	 * <p>
	 * If this is a base (undecorated) element, this method returns 0.
	 * 
	 * @return the decoration level
	 */
	int getDecorationLevel();

	/**
	 * Returns a representation of this element with the decoration at level
	 * {@code decorationLevel}.
	 * <p>
	 * If {@code decorationLevel} is 0, this returns the undecorated version of
	 * this element. If {@code decorationLevel == } {@link #getDecorationLevel},
	 * this returns this element.
	 * 
	 * @param decorationLevel
	 *            the decoration level
	 * @return the representation of this element at the given decoration level
	 * @throws IllegalArgumentException
	 *             if {@code decorationLevel > } {@link #getDecorationLevel}
	 */
	Element asDecorationAtLevel(int decorationLevel);
}
