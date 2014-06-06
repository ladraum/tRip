package trip.spi.helpers.cache;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedIterable<T> implements Iterable<T> {

	final CachedIterator<T> cachedProducer;
	Iterable<T> cache;

	@Override
	public Iterator<T> iterator() {
		if ( cache == null ) {
			cache = cachedProducer.getCache();
			return cachedProducer;
		}
		return cache.iterator();
	}
}
