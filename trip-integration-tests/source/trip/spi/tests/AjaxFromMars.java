package trip.spi.tests;

import trip.spi.Name;
import trip.spi.Provided;
import trip.spi.Singleton;

@Name( "ajax" )
@Singleton( exposedAs = Hero.class )
public class AjaxFromMars implements Hero, World {

	@Provided
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
