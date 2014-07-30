package trip.spi.tests;

import trip.spi.Producer;

public class HelloWorldProvider {

	@Producer
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}

	@Producer( name = "foo" )
	public HelloWorld createHelloFooo() {
		return new HelloFoo();
	}
}
