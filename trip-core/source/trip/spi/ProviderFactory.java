package trip.spi;

public interface ProviderFactory<T> {

	T provide( ProviderContext context ) throws ServiceProviderException;

}
