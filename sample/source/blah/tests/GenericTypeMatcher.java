package blah.tests;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import trip.spi.helpers.filter.Condition;

@RequiredArgsConstructor
@SuppressWarnings( "rawtypes" )
@ExtensionMethod( Commons.class )
public class GenericTypeMatcher<T> implements Condition<Converter> {

	final Class<T> targetClass;

	@Override
	public boolean check( Converter converter ) {
		return converter.getClass()
				.extractGenericTypeFromFirstInterface()
				.equals( this.targetClass );
	}
}
