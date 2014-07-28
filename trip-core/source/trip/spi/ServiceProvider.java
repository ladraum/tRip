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
			return ProviderFactoryMap.from( loadSingletons( ProviderFactory.class ) );
		} catch ( ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	public <T> T load( Class<T> interfaceClazz ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>() );
	}

	public <T> T load( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> T load( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		return load( interfaceClazz, condition, new EmptyProviderContext() );
	}

	public <T> T load( Class<T> interfaceClazz, ProviderContext context ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), context );
	}

	public <T> T load( Class<T> interfaceClazz, Map<String, Object> contextData ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( Class<T> interfaceClazz, String name, Map<String, Object> contextData ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( Class<T> interfaceClazz, Condition<T> condition, ProviderContext context ) throws ServiceProviderException {
		T produced = produceFromFactory( interfaceClazz, condition, context );
		if ( produced != null )
			return produced;
		return loadSingletons( interfaceClazz, condition ).first( condition );
	}

	@SuppressWarnings( "unchecked" )
	private <T> ProviderFactory<T> getProviderFor( Class<T> interfaceClazz,
			Condition<T> condition ) {
		if ( this.providers == null )
			return null;
		return (ProviderFactory<T>)this.providers.get( interfaceClazz, condition );
	}

	public <T> Iterable<T> loadSingletons( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return loadSingletons( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> Iterable<T> loadSingletons( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		return loadSingletons( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( "unchecked" )
	public <T> Iterable<T> loadSingletons( Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)this.injectables.get( interfaceClazz );
		if ( iterable == null ) {
			iterable = loadAllServicesImplementingTheInterface( interfaceClazz );
		}
		return iterable;
	}

	public <T> Iterable<T> loadAllServicesImplementingTheInterface( Class<T> interfaceClazz )
			throws ServiceProviderException {
		final CachedIterable<T> iterable = loadServiceProvidersFor( interfaceClazz );
		provideFor( interfaceClazz, iterable );
		provideOn( iterable );
		return iterable;
	}

	protected <T> CachedIterable<T> loadServiceProvidersFor(
			Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<Class<T>> iterableInterfaces = loadClassesImplementing( interfaceClazz );
		return ServiceLoader.loadFrom( iterableInterfaces );
	}

	public <T> Class<T> loadClassImplementing( Class<T> interfaceClazz ) {
		return loadClassImplementing( interfaceClazz, new AnyClass<T>() );
	}

	public <T> Class<T> loadClassImplementing( Class<T> interfaceClazz, String named ) {
		return loadClassImplementing( interfaceClazz, new NamedClass<T>( named ) );
	}

	public <T> Class<T> loadClassImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).first( condition );
	}

	public <T> Iterable<Class<T>> loadClassesImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public <T> Iterable<Class<T>> loadClassesImplementing( Class<T> interfaceClazz ) {
		Iterable<Class<T>> implementations = (Iterable)implementedClasses.get( interfaceClazz );
		if ( implementations == null ) {
			implementations = ServiceLoader.loadImplementationsFor( interfaceClazz );
			implementedClasses.put( (Class)interfaceClazz, (Iterable)implementations );
		}
		return implementations;
	}

	public <T> void provideFor( Class<T> interfaceClazz, ProviderFactory<T> provider ) {
		this.providers.memorizeProviderForClazz( provider, interfaceClazz );
	}

	public <T> void provideFor( Class<T> interfaceClazz, T object ) {
		provideFor( interfaceClazz, new SingleObjectIterable<T>( object ) );
	}

	protected <T> void provideFor( Class<T> interfaceClazz, Iterable<T> iterable ) {
		this.injectables.put( interfaceClazz, iterable );
	}

	public <T> void provideOn( Iterable<T> iterable ) throws ServiceProviderException {
		for ( T object : iterable )
			provideOn( object );
	}

	public void provideOn( Object object ) throws ServiceProviderException {
		try {
			final ProvidableClass<?> providableClass = retrieveProvidableClass( object.getClass() );
			providableClass.provide( object, this );
		} catch ( IllegalArgumentException | IllegalAccessException cause ) {
			throw new ServiceProviderException( cause );
		}
	}

	private ProvidableClass<?> retrieveProvidableClass( Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null ) {
			providableClass = ProvidableClass.wrap( targetClazz );
			providableClassCache.put( targetClazz, providableClass );
		}
		return providableClass;
	}

	private <T> T produceFromFactory( Class<T> interfaceClazz, Condition<T> condition, ProviderContext context )
			throws ServiceProviderException {
		final ProviderFactory<T> provider = getProviderFor( interfaceClazz, condition );
		if ( provider != null )
			return provider.provide( context );
		return null;
	}
}
