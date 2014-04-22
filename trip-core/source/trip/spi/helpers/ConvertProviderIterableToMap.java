package trip.spi.helpers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import trip.spi.ProviderFactory;

@RequiredArgsConstructor( staticName = "from" )
@SuppressWarnings( "rawtypes" )
public class ConvertProviderIterableToMap {

	final Iterable<ProviderFactory> iterable;

	public Map<Class<?>, ProviderFactory<?>> convert() {
		Map<Class<?>, ProviderFactory<?>> map = new HashMap<>();
		for ( ProviderFactory provider : this.iterable ) {
			Class clazz = getGenericClassFrom( provider );
			map.put( clazz, provider );
		}
		return map;
	}

	private Class getGenericClassFrom( ProviderFactory provider ) {
		Type[] types = provider.getClass().getGenericInterfaces();
		for ( Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProviderFactory.class ) )
				return (Class)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}
}
