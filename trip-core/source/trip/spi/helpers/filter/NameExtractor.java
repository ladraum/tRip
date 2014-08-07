package trip.spi.helpers.filter;

import trip.spi.Singleton;
import trip.spi.Stateless;

public class NameExtractor {

	public static boolean doesClassAnnotationsMatchesTheName( Class<?> clazz, String name ) {
		return name.equals( retrieveDefinedNameFrom( clazz ) );
	}

	public static String retrieveDefinedNameFrom( Class<?> clazz ) {
		final Singleton singleton = clazz.getAnnotation( Singleton.class );
		if ( singleton != null )
			return singleton.name();
		final Stateless stateless = clazz.getAnnotation( Stateless.class );
		if ( stateless != null )
			return stateless.name();
		return null;
	}
}
