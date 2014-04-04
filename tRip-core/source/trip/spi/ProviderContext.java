package trip.spi;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ProviderContext {

	final Map<Class<?>, Iterable<?>> injectables = new HashMap<>();

	public <T> T providerFor( Class<T> interfaceClazz ) throws ServiceProviderException {
		for ( T provided : providersFor(interfaceClazz) )
			return provided;
		return null;
	}

	public <T> Iterable<T> providersFor( Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<T> iterable = getInjectableFromInterface(interfaceClazz);
		return iterable;
	}

	@SuppressWarnings("unchecked")
	protected <T> Iterable<T> getInjectableFromInterface(Class<T> interfaceClazz) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)injectables.get(interfaceClazz);
		if ( iterable == null ) {
			iterable = loadServiceProvidersFor(interfaceClazz);
			provideFor(interfaceClazz, iterable);
		}
		return iterable;
	}
	
	public <T> void provideFor( Class<T> interfaceClazz, T object) {
		provideFor(interfaceClazz, new SingleObjectIterable<T>(object));
	}

	protected <T> void provideFor( Class<T> interfaceClazz, Iterable<T> iterable) {
		injectables.put(interfaceClazz, iterable);
	}

	protected <T> Iterable<T> loadServiceProvidersFor( Class<T> interfaceClazz ) throws ServiceProviderException {
		assertIsInterface( interfaceClazz );
		return ServiceLoader.load( interfaceClazz );
	}

	protected <T> void assertIsInterface( Class<T> interfaceClazz ) throws ServiceProviderException {
		if ( !Modifier.isInterface( interfaceClazz.getModifiers() ) )
			throw new ServiceProviderException("The class " + interfaceClazz + " should be an interface.");
	}
}
