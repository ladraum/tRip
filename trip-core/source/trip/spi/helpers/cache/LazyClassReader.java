package trip.spi.helpers.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;

import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
   
@RequiredArgsConstructor
public class LazyClassReader<S> implements Iterator<Class<S>> {

    private static final String PREFIX = "META-INF/services/";
    private static final int NOT_FOUND = -1;
	
    @Getter
    final List<Class<S>> cache = new ArrayList<Class<S>>();

    @Getter( lazy=true )
	private final Enumeration<URL> resources = readAllServiceResources();

	final Class<S> serviceClass;
    final ClassLoader loader;
	Iterator<String> currentResourceLines;

	Enumeration<URL> readAllServiceResources() {
		try {
			String fullName = PREFIX + serviceClass.getCanonicalName();
			return loader.getResources(fullName);
		} catch (IOException cause) {
			throw new ServiceConfigurationError(serviceClass.getCanonicalName() + ": " + cause.getMessage(), cause);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			if ( currentResourceLines == null || !currentResourceLines.hasNext() )
				readNextResourceFile();
			return currentResourceLines != null && currentResourceLines.hasNext();
		} catch ( IOException cause ) {
			throw new IllegalStateException(cause);
		}
	}

	void readNextResourceFile() throws IOException{
		if ( getResources().hasMoreElements() ) {
			URL nextElement = getResources().nextElement();
			currentResourceLines = readLines(nextElement);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<S> next() {
		try {
			String classCanonicalName = currentResourceLines.next();
			Class<S> clazz = (Class<S>) Class.forName(classCanonicalName, false, loader);
			cache.add(clazz);
			return clazz;
		} catch (ClassNotFoundException cause) {
			throw new IllegalStateException( cause );
		}
	}

	@Override
	public void remove() {}

	Iterator<String> readLines( URL url ) throws IOException {
		@Cleanup InputStream inputStream = url.openStream();
		@Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ( (line = readNextLine(reader)) != null )
			lines.add(line);
		return lines.iterator();
	}

	String readNextLine( BufferedReader reader ) throws IOException {
		String ln = reader.readLine();
		if ( ln != null && !isValidClassName(ln) )
			throw new IOException("Invalid class name: " + ln);
		return ln;
	}

	boolean isValidClassName( String className ) {
		return className.indexOf(' ') == NOT_FOUND
			&& className.indexOf('\t') == NOT_FOUND;
	}
}