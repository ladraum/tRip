package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import trip.spi.Singleton;
import trip.spi.Stateless;

@RequiredArgsConstructor
public class NamedClass<T> implements Condition<Class<T>> {

	final String name;

	@Override
	public boolean check(Class<T> clazz) {
		final Singleton singleton = clazz.getAnnotation( Singleton.class );
		if ( singleton != null )
			return name.equals( singleton.name() );
		final Stateless stateless = clazz.getAnnotation( Stateless.class );
		if ( stateless != null )
			return name.equals( stateless.name() );
		return false;
	}
}
