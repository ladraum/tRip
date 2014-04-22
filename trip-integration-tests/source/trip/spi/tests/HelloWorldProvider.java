package trip.spi.tests;

import trip.spi.Producer;

public class HelloWorldProvider {

	@Producer
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}
}
