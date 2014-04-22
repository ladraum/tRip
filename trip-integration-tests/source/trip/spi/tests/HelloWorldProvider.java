package trip.spi.tests;

import trip.spi.inject.Provides;

public class HelloWorldProvider {

	@Provides
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}
}
