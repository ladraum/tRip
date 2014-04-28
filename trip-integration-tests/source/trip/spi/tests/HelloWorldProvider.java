package trip.spi.tests;

import trip.spi.Name;
import trip.spi.Producer;

public class HelloWorldProvider {

	@Producer
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}

	@Producer
	@Name( "foo" )
	public HelloWorld createHelloFooo() {
		return new HelloFoo();
	}
}
