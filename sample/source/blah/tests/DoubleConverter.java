package blah.tests;

import trip.spi.Singleton;

@Singleton( Converter.class )
public class DoubleConverter implements Converter<Double> {

	@Override
	public Double convert( String string ) {
		return Double.valueOf( string );
	}

}
