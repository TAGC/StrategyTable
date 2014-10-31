package operation;

/**
 * This is a convenience interface for {@link Operation} objects that store and
 * return data of the same type.
 * 
 * @author David
 * 
 * @param <T>
 *            the type of data that can be passed and retrieved from this
 *            operation
 */
public interface PureOperation<T> extends Operation<T, T> {

}
