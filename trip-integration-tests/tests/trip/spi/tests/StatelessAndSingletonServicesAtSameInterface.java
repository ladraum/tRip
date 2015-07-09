package trip.spi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import lombok.val;

import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class StatelessAndSingletonServicesAtSameInterface {

	final ServiceProvider provider = new ServiceProvider();

	@Test
	public void ensureThatCouldFoundBothImplementations() throws ServiceProviderException {
		val services = provider.loadAll( Runnable.class );
		val list = new ArrayList<Runnable>();
		for ( val service : services )
			list.add( service );
		assertThat( list.size(), is( 2 ) );
	}
}
