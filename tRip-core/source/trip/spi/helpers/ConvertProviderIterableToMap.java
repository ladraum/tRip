package trip.spi.helpers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import trip.spi.Provider;

@RequiredArgsConstructor( staticName = "from" )
@SuppressWarnings( "rawtypes" )
public class ConvertProviderIterableToMap {

	final Iterable<Provider> iterable;

	public Map<Class<?>, Provider<?>> convert() {
		Map<Class<?>, Provider<?>> map = new HashMap<>();
		for ( Provider provider : this.iterable ) {
			Class clazz = getGenericClassFrom( provider );
			map.put( clazz, provider );
		}
		return map;
	}

	private Class getGenericClassFrom( Provider provider ) {
		Type[] types = provider.getClass().getGenericInterfaces();
		for ( Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( Provider.class ) )
				return (Class)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}
}
