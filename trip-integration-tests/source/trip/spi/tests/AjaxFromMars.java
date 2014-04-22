package trip.spi.tests;

import trip.spi.Provided;
import trip.spi.inject.Provides;

@Provides( Hero.class )
public class AjaxFromMars implements Hero, World {

	@Provided
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
