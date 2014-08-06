package trip.spi;

import java.util.HashMap;
import java.util.Map;
// import java.util.ServiceLoader;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import trip.spi.helpers.EmptyProviderContext;
import trip.spi.helpers.KeyValueProviderContext;
import trip.spi.helpers.ProvidableClass;
import trip.spi.helpers.ProviderFactoryMap;
import trip.spi.helpers.SingleObjectIterable;
import trip.spi.helpers.cache.CachedIterable;
import trip.spi.helpers.cache.ServiceLoader;
import trip.spi.helpers.filter.AnyClass;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;
import trip.spi.helpers.filter.NamedClass;
import trip.spi.helpers.filter.NamedObject;

@ExtensionMethod( Filter.class )
public class ServiceProvider {

	final Map<Class<?>, ProvidableClass<?>> providableClassCache = new HashMap<Class<?>, ProvidableClass<?>>();
	final Map<Class<?>, Iterable<Class<?>>> implementedClasses = new HashMap<Class<?>, Iterable<Class<?>>>();
	final Map<Class<?>, Iterable<?>> injectables;
	final ProviderFactoryMap providers;

	public ServiceProvider() {
		this.injectables = createDefaultInjectables();
		this.providers = loadAllProviders();
	}

	protected HashMap<Class<?>, Iterable<?>> createDefaultInjectables() {
		val injectables = new HashMap<Class<?>, Iterable<?>>();
		injectables.put( getClass(), new SingleObjectIterable<ServiceProvider>( this ) );
		return injectables;
	}

	protected ProviderFactoryMap loadAllProviders() {
		try {
			return ProviderFactoryMap.from( loadAll( ProviderFactory.class ) );
		} catch ( final ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	public <T> T load( final Class<T> interfaceClazz ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>() );
	}

	public <T> T load( final Class<T> interfaceClazz, final String name ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final Condition<T> condition ) throws ServiceProviderException {
		return load( interfaceClazz, condition, new EmptyProviderContext() );
	}

	public <T> T load( final Class<T> interfaceClazz, final ProviderContext context ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), context );
	}

	public <T> T load( final Class<T> interfaceClazz, final Map<String, Object> contextData ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final String name, final Map<String, Object> contextData )
			throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final Condition<T> condition, final ProviderContext context )
			throws ServiceProviderException {
		final T produced = produceFromFactory( interfaceClazz, condition, context );
		if ( produced != null )
			return produced;
		return loadAll( interfaceClazz, condition ).first( condition );
	}

	@SuppressWarnings( "unchecked" )
	private <T> ProviderFactory<T> getProviderFor( final Class<T> interfaceClazz,
			final Condition<T> condition ) {
		if ( this.providers == null )
			return null;
		return (ProviderFactory<T>)this.providers.get( interfaceClazz, condition );
	}

	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz, final String name ) throws ServiceProviderException {
		return loadAll( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz, final Condition<T> condition ) throws ServiceProviderException {
		return loadAll( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( "unchecked" )
	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)this.injectables.get( interfaceClazz );
		if ( iterable == null ) {
			iterable = loadAllServicesImplementingTheInterface( interfaceClazz );
		}
		return iterable;
	}

	public <T> Iterable<T> loadAllServicesImplementingTheInterface( final Class<T> interfaceClazz )
			throws ServiceProviderException {
		final CachedIterable<T> iterable = loadServiceProvidersFor( interfaceClazz );
		provideFor( interfaceClazz, iterable );
		provideOn( iterable );
		return iterable;
	}

	protected <T> CachedIterable<T> loadServiceProvidersFor(
			final Class<T> interfaceClazz ) throws ServiceProviderException {
		final Iterable<Class<T>> iterableInterfaces = loadClassesImplementing( interfaceClazz );
		return ServiceLoader.loadFrom( iterableInterfaces );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz ) {
		return loadClassImplementing( interfaceClazz, new AnyClass<T>() );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz, final String named ) {
		return loadClassImplementing( interfaceClazz, new NamedClass<T>( named ) );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz, final Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).first( condition );
	}

	public <T> Iterable<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz, final Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public <T> Iterable<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz ) {
		Iterable<Class<T>> implementations = (Iterable)implementedClasses.get( interfaceClazz );
		if ( implementations == null ) {
			implementations = ServiceLoader.loadImplementationsFor( interfaceClazz );
			implementedClasses.put( (Class)interfaceClazz, (Iterable)implementations );
		}
		return implementations;
	}

	public <T> void provideFor( final Class<T> interfaceClazz, final ProviderFactory<T> provider ) {
		this.providers.memorizeProviderForClazz( provider, interfaceClazz );
	}

	public <T> void provideFor( final Class<T> interfaceClazz, final T object ) {
		provideFor( interfaceClazz, new SingleObjectIterable<T>( object ) );
	}

	protected <T> void provideFor( final Class<T> interfaceClazz, final Iterable<T> iterable ) {
		this.injectables.put( interfaceClazz, iterable );
	}

	public <T> void provideOn( final Iterable<T> iterable ) throws ServiceProviderException {
		for ( final T object : iterable )
			provideOn( object );
	}

	public void provideOn( final Object object ) throws ServiceProviderException {
		try {
			final ProvidableClass<?> providableClass = retrieveProvidableClass( object.getClass() );
			providableClass.provide( object, this );
		} catch ( IllegalArgumentException | IllegalAccessException cause ) {
			throw new ServiceProviderException( cause );
		}
	}

	private ProvidableClass<?> retrieveProvidableClass( final Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null ) {
			providableClass = ProvidableClass.wrap( targetClazz );
			providableClassCache.put( targetClazz, providableClass );
		}
		return providableClass;
	}

	private <T> T produceFromFactory( final Class<T> interfaceClazz, final Condition<T> condition, final ProviderContext context )
			throws ServiceProviderException {
		final ProviderFactory<T> provider = getProviderFor( interfaceClazz, condition );
		if ( provider != null )
			return provider.provide( context );
		return null;
	}
}
