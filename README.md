# tRip - Low coupling module development
tRip intent to be a practical and lightweight tool to provide modularity and low coupling on your source code.

## Who is it for?
tRip is useful *for OOP developers* when:
- Low coupling development is a "must have"
- You need modules closed to modifications, but opened for extensions
- You'd like someone-else provide my classes dependencies ( Inversion of Control )

tRip is useful *for pragmatic developers* when:
- Manually dependency configuration through configuration files is annoying
- Creation of boilerplate classes to provide injection is unneeded repetitive

tRip is useful *for JVM library developers* when:
- Spring Beans is overwhelming
- Java CDI API is awesome, but I don't need a full featured container to run it
- Other CI warm-up time is too long
- You have experimenting other IoC/DI implementations but there's no dependency discovery algorithm and I should manually provide its producers

## Getting started
Imagine a cenario where you should calculate the month payment. And you have different stora and source's of data for any customer you should apply this calculation algorithm. To achieve this goal, we've defined the ```Storage``` and the ```SourceOfData``` interfaces. Both should have at least one implementation for each customer on class path.

```java
public interface Storage {
	void store( Object data );
}

public interface SourceOfData {
	List<Object> retrieve( Map<String, Object> filter );
}
```

Bellow we provide a sample Storage implementation that stores data into a java.util.List object. The same could be also applied to the SourceOfData interface, but was ommited to keep the sample simple.
```java
@Singleton
public class ListStorage implements Storage {

	final List<Object> storage = new ArrayList<Object>();

	public void store( Object data ) {
		storage.add( data );
	}
}
```

Let's see how we could make a totally decoupled calculation job.
```
@Stateless
public class MonthPaymentCalculationJob implements Runnable {

	@Provided
	Storage storage;

	@Provided
	SourceOfData source;

	public void run() {
		Map<String, Object> filter = createFilter();
		List<Object> data = source.retrieve( filter );
		applyValidations( data );
		storage.store( data );
	}

	// another methods
}

public class Main {
	// main method
	public void main( String[] args ) {
		final ServiceProvider provider = new ServiceProvider();
		final Runnable job = provider.load( Runnable.class );
		new Thread( job ).start();
	}
}
```
That's it, the ServiceProvider is able to find the interface implementations and inject the needed data into your services.

## Configuring project for maven
Configuring a maven project is easy. Just include the following repository and dependency configuration on its respecitve places on your pom.xml.

```xml
...
	<repository>
		<id>skullabs-release-repository</id>
		<name>Texo Release Repository</name>
		<url>http://skullabs.io:8081/content/repositories/PublicRelease/</url>
	</repository>
...
	<dependency>
		<groupId>io.skullabs.trip</groupId>
		<version>1.0-SNAPSHOT</version>
		<artifactId>trip-core</artifactId>
	</dependency>
	<!-- Only needed in compilation time. It scans your
		classes for Singletons and Stateless services
		and store a META INF for further usage in runtime
		making your warm-up faster. -->
	<dependency>
		<groupId>io.skullabs.trip</groupId>
		<version>1.0-SNAPSHOT</version>
		<artifactId>trip-processor</artifactId>
		<scope>provided</scope>
	</dependency>
...
```

### License
rTip is Apache 2.0 licensed.
