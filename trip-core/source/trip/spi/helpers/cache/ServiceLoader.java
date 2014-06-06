package trip.spi.helpers.cache;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLoader {
	
	public static <T> Iterable<Class<T>> loadImplementationsFor( Class<T> clazz ) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CachedIterator<Class<T>> reader = new LazyClassReader<T>(clazz, cl);
        return new CachedIterable<Class<T>>(reader);
	}
	
	public static <T> Iterable<T> loadFrom( Iterable<Class<T>> interfaces ) {
		CachedIterator<T> instantor = new LazyClassInstantor<T>(interfaces.iterator());
		return new CachedIterable<T>(instantor);
	}
}
