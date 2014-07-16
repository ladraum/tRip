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

## Usage
You can provide interface implementations:
```java
public interface Storage {
	void store( Object data );
}

@Singleton
public class IndexedStorage implements Storage {
	Map<Long, List<Object>> indexById = new HashMap<>();

	public void store( long id, Object data ) {
		List<Object> dataForId = indexById.get( id );
		// XXX: apply validations and check for null data
		dataForId.add( data );
	}
}
```

You can create managed services that will receive interface implementations:
```java
@Stateless
public class MonthPaymentCalculation {

	@Provided Storage storage;
	
	public void calculate( Emploeye employee ) {
		// XXX: do some calculation
		storage.store( employee.getId(), employee );
	}
}
```

You can run your software without repetitive injection definitions. Just load your main class and let tRip provide implementations for you:
```java
public class Main {

	static final int THIRD_FIVE_HOURS = 35;
	static final int ACME_COMPANY = 13;

	public void main( String[] args ){
		final ServiceProvider provider = new ServiceProvider();
		MonthPaymentCalculation paymentCalculation = provider.load( MonthPaymentCalculation.class );
		final Emploeye employee = Emploeye.thatWorks( THIRD_FIVE_HOURS ).forCompany( ACME_COMPANY );
		paymentCalculation.calculate( employee );
	}
}
```
Please read the [detailed Getting Started guide](https://github.com/Skullabs/tRip/wiki/tRip:-detailed-Getting-Started-guide) for more details and features.

## Main Features
tRip was designed to:
- Take care of Singleton's and Stateless' services for your
- Allow you to create modules and extensions to your software without change one LOC in the core implementation
- Fast warm-up: tRip already knows what to provide in compilation phase, there's no need to look into the entire class-path for provided classes
- Zero configuration: just let the [ServiceProvider](https://github.com/Skullabs/tRip/wiki/tRip:-detailed-Getting-Started-guide) run the software for you
- Factory-based creation of services: you can take control of how a service is provided creating your own factory
- Manually provided data: you still can provide data manually to your software context.

You can see all these features ( and a few more ) in the [detailed Getting Started guide](https://github.com/Skullabs/tRip/wiki/tRip:-detailed-Getting-Started-guide).

## Low footprint
tRip is basically two jars:
- trip-core ( 18kb ): which is needed to run your application
- trip-processor ( 11kb ): which is responsible by the auto discovery of Singleton's and Stateless' services on your modules. This dependency is needed only during compilation phase.

## Maven ready
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
