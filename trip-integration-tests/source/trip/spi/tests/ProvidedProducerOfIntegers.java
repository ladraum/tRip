package trip.spi.tests;

import java.util.concurrent.atomic.AtomicInteger;

import trip.spi.Producer;
import trip.spi.Service;

@Service
public class ProvidedProducerOfIntegers {

	final AtomicInteger counter = new AtomicInteger();

	@Producer
	public Integer produceInteger() {
		return counter.incrementAndGet();
	}
}
