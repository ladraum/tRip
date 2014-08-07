package trip.spi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class InjectionOfServiceDefinedByItsExposedTypeTest {

	final ServiceProvider provider = new ServiceProvider();

	@Provided( exposedAs = Bean.class )
	SerializableBean bean;

	@Test
	public void ensureThatAreAbleToLoadSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		Bean loaded = provider.load( Bean.class );
		assertThat( loaded, is( SerializableBean.class ) );
	}

	@Test
	public void ensureThatCouldProvideSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		provider.provideOn( this );
		assertNotNull( bean );
		assertThat( bean, is( SerializableBean.class ) );
	}
}
