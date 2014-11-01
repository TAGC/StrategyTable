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

	private ElementFactory() {
		throw new AssertionError("ElementFactory should not be instantiable");
	}

	public static AddElement createAddElement(int value) {
		AddElement newElement;
		
		if(addElementCache.containsKey(value)) {
			if((newElement = addElementCache.get(value).get()) != null) {
				System.out.println("Cache hit: " + newElement);
				assert newElement.getValue() == value;
				return newElement;
			}
		}
		
		newElement = new AddElement(value);
		addElementCache.put(value, new SoftReference<AddElement>(newElement));
		return newElement;
	}

	public static MultElement createMultElement(int value) {
		MultElement newElement;
		
		if(multElementCache.containsKey(value)) {
			if((newElement = multElementCache.get(value).get()) != null) {
				System.out.println("Cache hit: " + newElement);
				assert newElement.getValue() == value;
				return newElement;
			}
		}
		
		newElement = new MultElement(value);
		multElementCache.put(value, new SoftReference<MultElement>(newElement));
		return newElement;
	}

	public static IgnoreElementDecorator addIgnoreDecoration(Element decoratee) {
		return new IgnoreElementDecorator(decoratee);
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
}
