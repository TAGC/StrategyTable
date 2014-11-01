package tagc.strategytable.element;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory object that creates {@code Element} objects of particular types and
 * returns their class names.
 * 
 * @author David
 */
public class ElementFactory {

	/*
	 * Because elements are immutable, we could maintain a cache of identical
	 * elements to avoid unnecessary instantiation if we wanted.
	 */
	private static final Map<Integer, SoftReference<AddElement>> addElementCache = new HashMap<Integer, SoftReference<AddElement>>();
	private static final Map<Integer, SoftReference<MultElement>> multElementCache = new HashMap<Integer, SoftReference<MultElement>>();
	private static boolean cacheElements = true;

	private ElementFactory() {
		throw new AssertionError("ElementFactory should not be instantiable");
	}

	/**
	 * Sets whether elements should be cached as they're produced. Usage of this
	 * method is intended for performance testing purposes.
	 * 
	 * @param cacheElements
	 *            {@code true} to enable caching, {@code false} to disable
	 *            caching
	 */
	public static void setCachingElements(boolean cacheElements) {
		ElementFactory.cacheElements = cacheElements;
	}

	/**
	 * Returns whether elements are being cached as they're produced. Usage of
	 * this method is intended for performance testing purposes.
	 * 
	 * @return {@code true} if elements are cached as they are produced,
	 *         otherwise {@code false}
	 */
	public static boolean isCachingElements() {
		return ElementFactory.cacheElements;
	}

	public static AddElement createAddElement(int value) {
		AddElement newElement;

		if (cacheElements && addElementCache.containsKey(value)) {
			if ((newElement = addElementCache.get(value).get()) != null) {
				// System.out.println("Cache hit: " + newElement);
				assert newElement.getValue() == value;
				return newElement;
			}
		}

		newElement = new AddElement(value);
		if (cacheElements)
			addElementCache.put(value, new SoftReference<AddElement>(newElement));

		return newElement;
	}

	public static MultElement createMultElement(int value) {
		MultElement newElement;

		if (cacheElements && multElementCache.containsKey(value)) {
			if ((newElement = multElementCache.get(value).get()) != null) {
				// System.out.println("Cache hit: " + newElement);
				assert newElement.getValue() == value;
				return newElement;
			}
		}

		newElement = new MultElement(value);
		if (cacheElements)
			multElementCache.put(value, new SoftReference<MultElement>(newElement));

		return newElement;
	}

	public static IgnoreElementDecorator addIgnoreDecoration(Element decoratee) {
		return new IgnoreElementDecorator(decoratee);
	}
	
	public static ReverseElementDecorator addReverseDecoration(Element decoratee) {
		return new ReverseElementDecorator(decoratee);
	}

	public static Class<? extends AddElement> getAddElementClass() {
		return AddElement.class;
	}

	public static Class<? extends Element> getMultElementClass() {
		return MultElement.class;
	}

	public static Class<? extends Element> getIgnoreElementDecoratorClass() {
		return IgnoreElementDecorator.class;
	}
	
	public static Class<? extends Element> getReverseElementDecoratorClass() {
		return ReverseElementDecorator.class;
	}
}
