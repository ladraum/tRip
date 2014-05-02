package blah.tests;

import trip.spi.Service;

@Service( Converter.class )
public class DoubleConverter implements Converter<Double> {

	@Override
	public Double convert( String string ) {
		return Double.valueOf( string );
	}

}
