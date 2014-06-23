package trip.spi;

import trip.spi.helpers.filter.Condition;

public interface InterfaceProvider {

	<T> T load( Class<T> interfaceClazz ) throws ServiceProviderException;

	<T> T load( Class<T> interfaceClazz, String name ) throws ServiceProviderException;

	<T> T load( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException;

	<T> T load( Class<T> interfaceClazz, ProviderContext context ) throws ServiceProviderException;

	<T> T load( Class<T> interfaceClazz, Condition<T> condition, ProviderContext context ) throws ServiceProviderException;

	<T> Iterable<T> loadAll( Class<T> interfaceClazz, String name ) throws ServiceProviderException;

	<T> Iterable<T> loadAll( Class<T> interfaceClazz, Condition<T> condition ) throws ServiceProviderException;

	<T> Iterable<T> loadAll( Class<T> interfaceClazz ) throws ServiceProviderException;

	<T> Class<T> loadClassImplementing( Class<T> interfaceClazz, String named );

	<T> Class<T> loadClassImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition );

	<T> Iterable<Class<T>> loadClassesImplementing( Class<T> interfaceClazz, Condition<Class<T>> condition );

	<T> Iterable<Class<T>> loadClassesImplementing( Class<T> interfaceClazz );

	<T> void provideOn( Iterable<T> iterable ) throws ServiceProviderException;

	void provideOn( Object object ) throws ServiceProviderException;

}