package blah.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import trip.spi.ServiceProviderException;

public class ConverterTest {

	@Test
	public void grantThatCanConvertStringToLongAsExpected() throws ServiceProviderException, ConverterException {
		DefaultConverter<Long> converter = new DefaultConverter<>( Long.class );
		Long convertedValue = converter.convert( "16" );
		assertThat( convertedValue, is( 16l ) );
	}

	@Test
	public void grantThatCanConvertStringToDoubleAsExpected() throws ServiceProviderException, ConverterException {
		DefaultConverter<Double> converter = new DefaultConverter<>( Double.class );
		Double convertedValue = converter.convert( "1.6" );
		assertThat( convertedValue, is( 1.6 ) );
	}

	@Test
	public void grantThatCanConvertStringToDateAsExpected() throws ServiceProviderException, ConverterException {
		DefaultConverter<Date> converter = new DefaultConverter<>( Date.class );
		Date convertedValue = converter.convert( "20140420" );
		assertThat( convertedValue, is( new Date( 1397962800000l ) ) );
	}
}
