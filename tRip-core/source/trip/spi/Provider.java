package trip.spi;

import java.lang.reflect.Field;

public class Provider {

	final ProviderContext context = new ProviderContext();
	
	public <T> void provideFor( Class<T> interfaceClazz, T object ) {
		context.provideFor(interfaceClazz, object);
	}

	public <T> T load( Class<T> interfaceClazz ) throws ServiceProviderException {
		T provided = context.providerFor(interfaceClazz);
		provideOn(provided);
		return provided;
	}
	
	public <T> Iterable<T> loadAll( Class<T> interfaceClass ) throws ServiceProviderException {
		Iterable<T> providers = context.providersFor(interfaceClass);
		for ( T provider : providers )
			provideOn(provider);
		return providers;
	}

	public void provideOn( Object object ) throws ServiceProviderException {
		try {
			Class<? extends Object> clazz = object.getClass();
			while ( !Object.class.equals( clazz ) ) {
				provideOn( object, clazz );
				clazz = clazz.getSuperclass();
			}
		} catch ( IllegalAccessException cause ) {
			throw new ServiceProviderException(cause);
		}
	}

	protected void provideOn(Object object, Class<? extends Object> clazz) throws IllegalAccessException, ServiceProviderException {
		for ( Field field : clazz.getDeclaredFields() )
			if ( field.isAnnotationPresent( Provided.class ) )
				injectOnField(object, field);
	}

	protected void injectOnField(Object object, Field field) throws IllegalAccessException, ServiceProviderException {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			Object fieldValue = context.providerFor( fieldType );
			if ( fieldValue != null )
				provideOn( fieldValue );
			field.set( object, fieldValue );
	}
}
