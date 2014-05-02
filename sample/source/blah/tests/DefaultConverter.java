package blah.tests;

import lombok.Delegate;
import lombok.experimental.ExtensionMethod;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.Condition;

@SuppressWarnings( "unchecked" )
@ExtensionMethod( Commons.class )
public class DefaultConverter<T> {

	@Delegate
	Converter<T> converter;

	public DefaultConverter( Class<T> targetClass ) throws ServiceProviderException {
		this.converter = extractDefaultConverterFor( targetClass );
	}

	@SuppressWarnings( "rawtypes" )
	private Converter<T> extractDefaultConverterFor( Class<T> targetClass ) throws ServiceProviderException {
		Condition<Converter> matcher = new GenericTypeMatcher<>( targetClass );
		return new ServiceProvider().load( Converter.class, matcher );
	}
}
