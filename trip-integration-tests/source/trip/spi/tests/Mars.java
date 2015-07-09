package trip.spi.tests;

import trip.spi.Singleton;

@Singleton( exposedAs = World.class )
public class Mars implements World {

	@Override
	public String getWorld() {
		return "Mars";
	}
}
