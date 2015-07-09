package trip.spi.helpers;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public interface ProvidableField {

	public void provide( final Object instance, final ServiceProvider provider )
		throws ServiceProviderException, IllegalArgumentException, IllegalAccessException;
}