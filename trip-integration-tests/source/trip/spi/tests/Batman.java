package trip.spi.tests;

import lombok.Delegate;
import trip.spi.Name;
import trip.spi.Provided;
import trip.spi.Service;

@Service( Hero.class )
@Name( "batman" )
public class Batman implements Hero, World {

	@Delegate
	@Name( "ajax" )
	@Provided
	Hero ajax;

	@Override
	public String getWorld() {
		return ajax.getClass().getSimpleName();
	}
}
