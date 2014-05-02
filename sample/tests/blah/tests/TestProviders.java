package blah.tests;

import trip.spi.Name;
import trip.spi.Producer;

public class TestProviders {

	@Producer
	@Name( "date-format" )
	public String createDateFormat() {
		return "yyyyMMdd";
	}
}
