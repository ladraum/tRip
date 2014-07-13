package trip.spi.tests;

import trip.spi.Producer;
import trip.spi.Stateless;

@Stateless
public class StatelessProvidedProducerOfShorts {

	volatile short counter;

	@Producer
	public Short produceShort() {
		return counter++;
	}
}
