package trip.spi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import trip.spi.helpers.ConvertProviderIterableToMap;
import trip.spi.helpers.SingleObjectIterable;

public class ProviderService {

	final Map<Class<?>, Iterable<?>> injectables = new HashMap<>();
	final Map<Class<?>, Provider<?>> providers = loadAllProviders();

	private Map<Class<?>, Provider<?>> loadAllProviders() {
		try {
			return ConvertProviderIterableToMap
					.from( loadAll( Provider.class ) )
					.convert();
		} catch ( ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public <T> T load( Class<T> interfaceClazz ) throws ServiceProviderException {
		Provider<T> provider = (Provider<T>)this.providers.get( interfaceClazz );
		if ( provider != null )
			return provider.provide();
		for ( T provided : loadAll( interfaceClazz ) )
			return provided;
		return null;
	}

	@SuppressWarnings( "unchecked" )
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)this.injectables.get( interfaceClazz );
		if ( iterable == null ) {
			iterable = loadServiceProvidersFor( interfaceClazz );
			provideFor( interfaceClazz, iterable );
			provideOn( iterable );
		}
		return iterable;
	}

	protected <T> Iterable<T> loadServiceProvidersFor( Class<T> interfaceClazz ) throws ServiceProviderException {
		assertIsInterface( interfaceClazz );
		return ServiceLoader.load( interfaceClazz );
	}

	protected <T> void assertIsInterface( Class<T> interfaceClazz ) throws ServiceProviderException {
		if ( !Modifier.isInterface( interfaceClazz.getModifiers() ) )
			throw new ServiceProviderException( "The class " + interfaceClazz + " should be an interface." );
	}

	public <T> void provideFor( Class<T> interfaceClazz, Provider<T> provider ) {
		this.providers.put( interfaceClazz, provider );
	}

	public <T> void provideFor( Class<T> interfaceClazz, T object ) {
		provideFor( interfaceClazz, new SingleObjectIterable<T>( object ) );
	}

	protected <T> void provideFor( Class<T> interfaceClazz, Iterable<T> iterable ) {
		this.injectables.put( interfaceClazz, iterable );
	}

	protected <T> void provideOn( Iterable<T> iterable ) throws ServiceProviderException {
		for ( T object : iterable )
			provideOn( object );
	}

	public void provideOn( Object object ) throws ServiceProviderException {
		try {
			Class<? extends Object> clazz = object.getClass();
			while ( !Object.class.equals( clazz ) ) {
				provideOn( object, clazz );
				clazz = clazz.getSuperclass();
			}
		} catch ( IllegalAccessException cause ) {
			throw new ServiceProviderException( cause );
		}
	}

	protected void provideOn( Object object, Class<? extends Object> clazz ) throws IllegalAccessException, ServiceProviderException {
		for ( Field field : clazz.getDeclaredFields() )
			if ( field.isAnnotationPresent( Provided.class ) )
				injectOnField( object, field );
	}

	protected void injectOnField( Object object, Field field ) throws IllegalAccessException, ServiceProviderException {
		field.setAccessible( true );
		Class<?> fieldType = field.getType();
		Object fieldValue = load( fieldType );
		if ( fieldValue != null )
			provideOn( fieldValue );
		field.set( object, fieldValue );
	}
}
