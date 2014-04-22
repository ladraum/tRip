package trip.spi.tests;

import trip.spi.inject.Provides;

@Provides
public class Mars implements World {

	@Override
	public String getWorld() {
		return "Mars";
	}

}
