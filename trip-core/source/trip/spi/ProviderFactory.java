package trip.spi;

public interface ProviderFactory<T> {

	T provide() throws ServiceProviderException;

}
