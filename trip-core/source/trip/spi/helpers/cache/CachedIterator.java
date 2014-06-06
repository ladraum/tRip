package trip.spi.helpers.cache;

import java.util.Iterator;

public interface CachedIterator<T> extends Iterator<T> {

	Iterable<T> getCache();
}
