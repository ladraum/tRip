package trip.spi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import trip.spi.helpers.ProvidersMap;
import trip.spi.helpers.SingleObjectIterable;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;
import trip.spi.helpers.filter.NamedProvider;

@ExtensionMethod( Filter.class )
public class ServiceProvider {

	final Map<Class<?>, Iterable<?>> injectables;
	final ProvidersMap providers;

	public ServiceProvider() {
		this.injectables = createDefaultInjectables();
		this.providers = loadAllProviders();
	}

	protected HashMap<Class<?>, Iterable<?>> createDefaultInjectables() {
		val injectables = new HashMap<Class<?>, Iterable<?>>();
		injectables.put( getClass(), new SingleObjectIterable<ServiceProvider>( this ));
		return injectables;
	}

	protected ProvidersMap loadAllProviders() {
		try {
			return ProvidersMap.from( loadAll( ProviderFactory.class ) );
		} catch ( ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	public <T> T load( Class<T> interfaceClazz ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>() );
	}

	public <T> T load( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedProvider<T>( name ) );
	}

	@SuppressWarnings("unchecked")
	public <T> T load( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		ProviderFactory<?> provider = getProviderFor(interfaceClazz, condition);
		if ( provider != null )
			return (T) provider.provide();
		return (T)loadAll( interfaceClazz, condition ).first( condition );
	}

	private <T> ProviderFactory<?> getProviderFor(Class<T> interfaceClazz,
			Condition<T> condition) {
		if ( this.providers == null )
			return null;
		return this.providers.get( interfaceClazz, condition);
	}
	
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz ) throws ServiceProviderException {
		return loadAll( interfaceClazz, new AnyObject<T>() );
	}
	
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return loadAll( interfaceClazz, new NamedProvider<T>( name ) );
	}

	@SuppressWarnings( "unchecked" )
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)this.injectables.get( interfaceClazz );
		if ( iterable == null ) {
			iterable = loadServiceProvidersFor( interfaceClazz, condition );
			provideFor( interfaceClazz, iterable );
			provideOn( iterable );
		}
		return (Iterable<T>)iterable.filter(condition);
	}

	protected <T> Iterable<T> loadServiceProvidersFor(
			Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		return ServiceLoader.load( interfaceClazz );
	}

	public <T> void provideFor( Class<T> interfaceClazz, ProviderFactory<T> provider ) {
		this.providers.memorizeProviderForClazz(provider, interfaceClazz);
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

	@SuppressWarnings("unchecked")
	protected <T> void injectOnField( Object object, Field field ) throws IllegalAccessException, ServiceProviderException {
		field.setAccessible( true );
		Condition<T> condition = (Condition<T>)extractInjectionFilterCondition( field ); 
		Class<T> fieldType = (Class<T>)field.getType();
		Object fieldValue = load( fieldType, condition );
		if ( fieldValue != null )
			provideOn( fieldValue );
		field.set( object, fieldValue );
	}

	protected Condition<?> extractInjectionFilterCondition( Field field ) {
		Name annotation = field.getAnnotation( Name.class );
		if ( annotation == null )
			return new AnyObject<Object>();
		return new NamedProvider<Object>( annotation.value() );
	}
}
