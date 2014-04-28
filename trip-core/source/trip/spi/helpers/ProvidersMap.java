package trip.spi.helpers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Delegate;
import lombok.experimental.ExtensionMethod;
import trip.spi.ProviderFactory;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;

@ExtensionMethod( Filter.class )
public class ProvidersMap implements Map<Class<?>, List<ProviderFactory<?>>> {

	@Delegate
	final Map<Class<?>, List<ProviderFactory<?>>> map = new HashMap<>();

	@SuppressWarnings("rawtypes")
	public static ProvidersMap from( Iterable<ProviderFactory> iterable ) {
		ProvidersMap providers = new ProvidersMap();
		for ( ProviderFactory<?> provider : iterable ) {
			Class<?> clazz = getGenericClassFrom( provider );
			providers.memorizeProviderForClazz(provider, clazz);
		}
		return providers;
	}

	private static Class<?> getGenericClassFrom( ProviderFactory<?> provider ) {
		Type[] types = provider.getClass().getGenericInterfaces();
		for ( Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProviderFactory.class ) )
				return (Class<?>)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}

	public void memorizeProviderForClazz( ProviderFactory<?> provider, Class<?> clazz ) {
		List<ProviderFactory<?>> iterable = map.get( clazz );
		if ( iterable == null ) {
			iterable = new ArrayList<>();
			map.put( clazz, iterable );
		}
		iterable.add( provider );
	}

	public ProviderFactory<?> get( Class<?> clazz, Condition<?> condition ) {
		List<ProviderFactory<?>> list = get( clazz );
		if ( list == null )
			return null;
		return (ProviderFactory<?>)list.first(condition);
	}
}
