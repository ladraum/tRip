package trip.spi.helpers;

import java.lang.reflect.Field;

import lombok.Value;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.NamedObject;

@Value
public class ProvidableField<T> {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;
	final ProviderContext providerContext;

	public void provide( final Object instance, final ServiceProvider provider )
			throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		final Object value = provider.load( fieldType, condition, providerContext );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> ProvidableField<T> wrap( final Field field ) {
		field.setAccessible( true );
		return new ProvidableField<T>(
				field,
				(Class<T>)field.getType(),
				(Condition<T>)extractInjectionFilterCondition( field ),
				new FieldProviderContext( field ) );
	}

	public static Condition<?> extractInjectionFilterCondition( final Field field ) {
		final Provided annotation = field.getAnnotation( Provided.class );
		if ( annotation.name().isEmpty() )
			return new AnyObject<Object>();
		return new NamedObject<Object>( annotation.name() );
	}
}
