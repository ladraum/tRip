package trip.spi.tests;

import trip.spi.Service;

@Service
public class Mars implements World {

	@Override
	public String getWorld() {
		return "Mars";
	}

}
