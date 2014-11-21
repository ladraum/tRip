package trip.spi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import trip.spi.Producer;
import trip.spi.ProviderContext;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class GeneratedCodeAndMetaINFTest {

	final ServiceProvider provider = new ServiceProvider();

	// @Test
	// public void grantThatGenerateNewHelloWorld() throws
	// ServiceProviderException {
	// final HelloWorld helloWorld = this.provider.load( HelloWorld.class );
	// assertEquals( "Helllooooo", helloWorld.toString() );
	// }

	@Test
	public void grantThatGenerateNewHelloFoo() throws ServiceProviderException {
		final HelloWorld helloWorld = this.provider.load( HelloWorld.class, "foo" );
		assertEquals( "Fooo!!!", helloWorld.toString() );
	}

	@Test
	public void grantThatCouldRetrieveAjaxFromMars() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, "ajax" );
		assertNotNull( "No 'Hero' implementations found", hero );
		assertEquals( "Expected 'Hero' should be 'AjaxFromMars' instance",
				hero.getClass(), AjaxFromMars.class );
		final AjaxFromMars ajax = (AjaxFromMars)hero;
		assertEquals( "'ajax' doesn't provide the expected string", "Mars", ajax.getWorld() );
	}

	@Test
	public void grantThatCouldRetrieveBatman() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, "batman" );
		assertNotNull( hero );
		assertEquals( Batman.class, hero.getClass() );
		final Batman batman = (Batman)hero;
		assertEquals( "'batman' doesn't provide the expected string", "Mars", batman.getWorld() );
	}

	@Producer
	public String produceAGenericString( final ProviderContext context ) {
		return null;
	}
}
