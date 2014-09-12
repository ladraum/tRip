package trip.spi.tests;

import lombok.experimental.Delegate;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = Hero.class, name = "batman" )
public class Batman implements Hero, World {

	@Delegate
	@Provided( name = "ajax" )
	Hero ajax;

	@Override
	public String getWorld() {
		return ajax.getClass().getSimpleName();
	}
}
