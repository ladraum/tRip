package trip.spi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
	public void grantThatGenerateNewHelloFoo() throws ServiceProviderException {
		HelloWorld helloWorld = this.provider.load( HelloWorld.class, "foo" );
		assertEquals( "Fooo!!!", helloWorld.toString() );
	}

	@Test
	public void grantThatCouldRetrieveAjaxFromMars() throws ServiceProviderException {
		Hero hero = this.provider.load( Hero.class, "ajax" );
		assertNotNull( "No 'Hero' implementations found", hero );
		assertEquals( "Expected 'Hero' should be 'AjaxFromMars' instance",
				hero.getClass(), AjaxFromMars.class );
		AjaxFromMars ajax = (AjaxFromMars)hero;
		assertEquals( "'ajax' doesn't provide the expected string", "Mars", ajax.getWorld() );
	}

	@Test
	public void grantThatCouldRetrieveBatman() throws ServiceProviderException {
		Hero hero = this.provider.load( Hero.class, "batman" );
		assertNotNull( hero );
		assertEquals( Batman.class, hero.getClass() );
		Batman batman = (Batman)hero;
		assertEquals( "'batman' doesn't provide the expected string", "AjaxFromMars", batman.getWorld() );
	}
}
