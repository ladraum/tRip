package trip.spi.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class SourceCodeGenerationTest {

	final ServiceProvider provider = new ServiceProvider();

	@Test
	public void grantThatGenerateNewHelloWorld() throws ServiceProviderException {
		HelloWorld helloWorld = this.provider.load( HelloWorld.class );
		assertEquals( "Helllooooo", helloWorld.toString() );
	}

	@Test
	public void grantThatCouldRetrieveAjaxFromMars() throws ServiceProviderException {
		Hero hero = this.provider.load( Hero.class );
		assertEquals( hero.getClass(), AjaxFromMars.class );
		AjaxFromMars ajax = (AjaxFromMars)hero;
		assertEquals( "Mars", ajax.getWorld() );
	}
}
