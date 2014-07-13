package trip.spi.tests;

import trip.spi.Singleton;

@Singleton
public class Mars implements World {

	@Override
	public String getWorld() {
		return "Mars";
	}

}
