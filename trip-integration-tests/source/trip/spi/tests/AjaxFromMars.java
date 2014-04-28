package trip.spi.tests;

import trip.spi.Name;
import trip.spi.Provided;
import trip.spi.Service;

@Name( "ajax" )
@Service( Hero.class )
public class AjaxFromMars implements Hero, World {

	@Provided
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
