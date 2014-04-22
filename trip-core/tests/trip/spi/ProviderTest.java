package trip.spi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProviderTest {

	final ServiceProvider provider = new ServiceProvider();

	@Test
	public void grantThatInjectTestableResources() throws ServiceProviderException {
		Printable printable = this.provider.load( Printable.class );
		assertThat( printable.toString(), is( "Hello World." ) );
	}
}
