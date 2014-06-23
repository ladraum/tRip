package trip.spi;

import java.util.HashMap;
import java.util.Map;
// import java.util.ServiceLoader;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import trip.spi.helpers.*;
import trip.spi.helpers.cache.ServiceLoader;
import trip.spi.helpers.filter.*;

@ExtensionMethod( Filter.class )
public class ServiceProvider implements InterfaceProvider {

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
		} catch ( ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#load(java.lang.Class)
	 */
	@Override
	public <T> T load( Class<T> interfaceClazz ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#load(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T load( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#load(java.lang.Class,
	 * trip.spi.helpers.filter.Condition)
	 */
	@Override
	public <T> T load( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		return load( interfaceClazz, condition, new EmptyProviderContext() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#load(java.lang.Class,
	 * trip.spi.ProviderContext)
	 */
	@Override
	public <T> T load( Class<T> interfaceClazz, ProviderContext context ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), context );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#load(java.lang.Class,
	 * trip.spi.helpers.filter.Condition, trip.spi.ProviderContext)
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T load( Class<T> interfaceClazz, Condition<T> condition, ProviderContext context ) throws ServiceProviderException {
		ProviderFactory<?> provider = getProviderFor( interfaceClazz, condition );
		if ( provider != null )
			return (T)provider.provide( context );
		return loadAll( interfaceClazz, condition ).first( condition );
	}

	private <T> ProviderFactory<?> getProviderFor( Class<T> interfaceClazz,
			Condition<T> condition ) {
		if ( this.providers == null )
			return null;
		return this.providers.get( interfaceClazz, condition );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadAll(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz, String name ) throws ServiceProviderException {
		return loadAll( interfaceClazz, new NamedObject<T>( name ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadAll(java.lang.Class,
	 * trip.spi.helpers.filter.Condition)
	 */
	@Override
	public <T> Iterable<T> loadAll( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException {
		return loadAll( interfaceClazz ).filter( condition );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadAll(java.lang.Class)
	 */
	@Override
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

	protected <T> Iterable<T> loadServiceProvidersFor(
			Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<Class<T>> iterableInterfaces = loadClassesImplementing( interfaceClazz );
		return ServiceLoader.loadFrom( iterableInterfaces );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadClassImplementing(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public <T> Class<T> loadClassImplementing( Class<T> interfaceClazz, String named ) {
		return loadClassImplementing( interfaceClazz, new NamedClass<T>( named ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadClassImplementing(java.lang.Class,
	 * trip.spi.helpers.filter.Condition)
	 */
	@Override
	public <T> Class<T> loadClassImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).first( condition );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadClassesImplementing(java.lang.Class,
	 * trip.spi.helpers.filter.Condition)
	 */
	@Override
	public <T> Iterable<Class<T>> loadClassesImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).filter( condition );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#loadClassesImplementing(java.lang.Class)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#provideOn(java.lang.Iterable)
	 */
	@Override
	public <T> void provideOn( Iterable<T> iterable ) throws ServiceProviderException {
		for ( T object : iterable )
			provideOn( object );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see trip.spi.InterfaceProvider#provideOn(java.lang.Object)
	 */
	@Override
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
}
