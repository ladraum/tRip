package trip.spi.helpers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Delegate;
import lombok.experimental.ExtensionMethod;
import trip.spi.ProducerFactory;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;

@ExtensionMethod( Filter.class )
public class ProducerFactoryMap implements Map<Class<?>, List<ProducerFactory<?>>> {

	@Delegate
	final Map<Class<?>, List<ProducerFactory<?>>> map = new HashMap<>();

	@SuppressWarnings("rawtypes")
	public static ProducerFactoryMap from( Iterable<ProducerFactory> iterable ) {
		ProducerFactoryMap providers = new ProducerFactoryMap();
		for ( ProducerFactory<?> provider : iterable ) {
			Class<?> clazz = getGenericClassFrom( provider );
			providers.memorizeProviderForClazz(provider, clazz);
		}
		return providers;
	}

	private static Class<?> getGenericClassFrom( ProducerFactory<?> provider ) {
		Type[] types = provider.getClass().getGenericInterfaces();
		for ( Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProducerFactory.class ) )
				return (Class<?>)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}

	public void memorizeProviderForClazz( ProducerFactory<?> provider, Class<?> clazz ) {
		List<ProducerFactory<?>> iterable = map.get( clazz );
		if ( iterable == null ) {
			iterable = new ArrayList<>();
			map.put( clazz, iterable );
		}
		iterable.add( provider );
	}

	public ProducerFactory<?> get( Class<?> clazz, Condition<?> condition ) {
		List<ProducerFactory<?>> list = get( clazz );
		if ( list == null )
			return null;
		return (ProducerFactory<?>)list.first(condition);
	}
}
