package trip.spi.helpers;

import java.lang.reflect.Field;

import lombok.Value;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.ChainedCondition;
import trip.spi.helpers.filter.ClassAssignableFrom;
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

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static <T> ProvidableField<T> wrap( final Field field ) {
		field.setAccessible( true );
		Provided provided = field.getAnnotation( Provided.class );
		Class expectedClass = provided.exposedAs().equals( Provided.class )
			? field.getType() : provided.exposedAs();
		return new ProvidableField<T>(
			field, (Class<T>)expectedClass,
				(Condition<T>)extractInjectionFilterCondition( field ),
				new FieldProviderContext( field ) );
	}

	public static Condition<?> extractInjectionFilterCondition( final Field field ) {
		final ChainedCondition conditions = new ChainedCondition();
		conditions.add( new ClassAssignableFrom( field.getType() ) );

		final Provided annotation = field.getAnnotation( Provided.class );
		if ( !annotation.name().isEmpty() )
			conditions.add( new NamedObject<>( annotation.name() ) );

		return conditions;
	}
}
