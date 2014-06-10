package trip.spi.helpers.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedIterable<T> implements Iterable<T> {

	final Iterator<T> cachedProducer;
	Iterable<T> cache;

	@Override
	public Iterator<T> iterator() {
		if ( cache == null )
			cache = createCache();
		return cache.iterator();
	}

	public Iterable<T> createCache() {
		List<T> cache = new ArrayList<T>();
		while( cachedProducer.hasNext() )
			cache.add( cachedProducer.next() );
		return cache;
	}
}
