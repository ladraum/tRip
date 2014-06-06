package trip.spi.helpers.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LazyClassInstantor<T> implements Iterator<T> {
	
	final Iterator<Class<T>> reader;
	List<T> cache = new ArrayList<T>();

	@Override
	public boolean hasNext() {
		return reader.hasNext();
	}

	@Override
	public T next() {
		try {
			Class<T> clazz = reader.next();
			T instance = clazz.newInstance();
			cache.add(instance);
			return instance;
		} catch (InstantiationException | IllegalAccessException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public void remove() {
		reader.remove();
	}
}
