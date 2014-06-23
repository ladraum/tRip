package trip.spi.helpers;

import java.lang.reflect.Field;

import lombok.Value;
import trip.spi.*;
import trip.spi.helpers.filter.*;

@Value
public class ProvidableField<T> {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;
	final ProviderContext providerContext;

	public void provide( Object instance, InterfaceProvider provider )
			throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		Object value = provider.load( fieldType, condition, providerContext );
		set( instance, value );
	}

	public void set( Object instance, Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> ProvidableField<T> wrap( Field field ) {
		field.setAccessible( true );
		return new ProvidableField<T>(
				field,
				(Class<T>)field.getType(),
				(Condition<T>)extractInjectionFilterCondition( field ),
				new FieldProviderContext( field ) );
	}

	public static Condition<?> extractInjectionFilterCondition( Field field ) {
		final Name annotation = field.getAnnotation( Name.class );
		if ( annotation == null )
			return new AnyObject<Object>();
		return new NamedObject<Object>( annotation.value() );
	}
}
