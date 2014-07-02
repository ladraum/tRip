package trip.spi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class SingletonProvidedProducerTest {

	final ServiceProvider provider = new ServiceProvider();

	@Test
	public void grantThatProduceThreeDifferentNumbers() throws ServiceProviderException {
		assertThat( provider.load( Integer.class ), is( 1 ) );
		assertThat( provider.load( Integer.class ), is( 2 ) );
		assertThat( provider.load( Integer.class ), is( 3 ) );
	}
}
