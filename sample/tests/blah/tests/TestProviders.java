package blah.tests;

import trip.spi.Producer;

public class TestProviders {

	@Producer( name = "date-format" )
	public String createDateFormat() {
		return "yyyyMMddHHmmss";
	}
}
