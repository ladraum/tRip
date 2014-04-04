package trip.spi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import trip.spi.Provider;
import trip.spi.ServiceProviderException;

public class ProviderTest {

	final Provider provider = new Provider();

	@Test
	public void grantThatInjectTestableResources() throws ServiceProviderException {
		Printable printable = provider.load( Printable.class );
		assertThat( printable.toString() , is( "Hello World" ) );
	}
}
