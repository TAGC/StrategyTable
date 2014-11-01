package tagc.strategytable.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ElementPerformanceTest {

	// On my machine, elapsed time:
	// - WITH caching    ~250 ms
	// - WITHOUT caching ~4000 ms
	// 16x speed up with caching
	private static void createElements(boolean caching) {
		final int ELEMENT_COUNT = 10000000;
		final long startTime = System.nanoTime();
		final Random random = new Random();

		// Assume that almost all elements are instantiated with values between
		// 0 and 100.
		final int maxNumber = 10;

		List<Element> elements = new ArrayList<Element>();
		ElementFactory.setCachingElements(caching);
		for (int i = 0; i < ELEMENT_COUNT / 2; i++) {
			elements.add(ElementFactory.createAddElement(random.nextInt(maxNumber)));
			elements.add(ElementFactory.createMultElement(random.nextInt(maxNumber)));
		}

		final long executionTime = System.nanoTime() - startTime;
		System.out.printf("Cache: %s, Execution time: %s ms", (caching ? "Enabled" : "Disabled"),
				TimeUnit.MILLISECONDS.convert(executionTime, TimeUnit.NANOSECONDS));
	}

	public static void main(String[] args) {
		createElements(false);
	}
}
