package tagc.strategytable.operation;

import tagc.strategytable.element.Element;

/**
 * Operations encapsulate pieces of functionality that can be performed using
 * {@link Element} objects.
 * 
 * @author David
 * 
 * @param <I>
 *            the type of data that can be passed to this type of operation
 * @param <O>
 *            the type of data that can be retrieved from this type of operation
 */
public interface Operation<I, O> {

	/**
	 * Stores an item of data with this operation.
	 * 
	 * @param data
	 *            an item of data to store
	 */
	void store(I data);

	/**
	 * Returns the last-stored item of data or {@code null} if no data has yet
	 * been stored.
	 * 
	 * @return this operation's last-set data
	 */
	O get();
}
