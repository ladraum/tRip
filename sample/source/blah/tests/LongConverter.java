package blah.tests;

import trip.spi.Singleton;

@Singleton( exposedAs = Converter.class )
public class LongConverter implements Converter<Long> {

	@Override
	public Long convert( String string ) {
		return Long.valueOf( string );
	}

}
