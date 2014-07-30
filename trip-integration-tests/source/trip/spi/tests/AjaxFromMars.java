package trip.spi.tests;

import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = Hero.class, name = "ajax" )
public class AjaxFromMars implements Hero, World {

	@Provided
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
