package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.Singleton;
import trip.spi.Stateless;

@RequiredArgsConstructor
public class NamedObject<T> implements Condition<T> {

	final String name;

	@Override
	public boolean check( T object ) {
		val clazz = object.getClass();
		return doesClassAnnotationsMatchesTheName( clazz );
	}

	public boolean doesClassAnnotationsMatchesTheName( Class<?> clazz ) {
		final Singleton singleton = clazz.getAnnotation( Singleton.class );
		if ( singleton != null )
			return name.equals( singleton.name() );
		final Stateless stateless = clazz.getAnnotation( Stateless.class );
		if ( stateless != null )
			return name.equals( stateless.name() );
		return false;
	}
}
