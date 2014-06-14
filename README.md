tRip
====
Renewed Interface Provider

Java lacks a way to provide a real low-coupling module development. Interfaces are easy to create and fast to maintain, but seek for its implementations is a painful job. tRip intents to provide an easy-to-maintain, flexible and compile time way to provide modularity on your code in a really small footprint implementation.

# Show me the code

Please consider the following StringGenerator interface. It intent to provide a simple way to generate Strings.
```java
public interface StringGenerator {

  String generate();
}
```

Also, consider the following implementations of ```StringGenerator```.
```java
@trip.api.Service
public class HelloWorldGenerator implements StringGenerator {

  public String generate() {
    return "Hello World";
  }
}

```

The following ```Runnable``` will print twice generated data from injected ```StringGenerator```.
```java
@Log
@Service
// use this annotation for disambiguation when you have more than one implementation
@Name( "twice-runnable" )
public class TwicePrinterRunnable implements Runnable {

  // Will be provided soon
  @Provided StringGenerator generator;
  
  @Override
  public void run(){
    log.info( generator.generate() );
    log.info( generator.generate() );
  }
}
```

The bellow unit test is a simple way to see this example running.

```java
public class SampleTest {

  // Creating the Provider object
  final ServiceProvider provider = new ServiceProvider();

  @Test
  public void seeItWorking(){
    Runnable runnable = new TwicePrinterRunnable();
    // manually providing fields on TwicePrinterRunnable class.
    provider.provideOn( runnable );
    runnable.run();
  }

  @Test
  public void seeItWorkingIfTwicePrinterRunnableWasConfiguredAsRunnableProvider(){
    // will load a service provider for Runnable interface and
    // automatically provide its dependencies.
    Runnable runnable = provider.load( Runnable.class );
    // if you have more than one implementation, just tell ServiceProvide which is its name
    // Runnable runnable = provider.load( Runnable.class, "twice-runnable" );
    runnable.run();
  }
}
```

### Multi-Level Service Providing
The tiny implementation the above implicitly shows that is also possible to _"inject"_ objects in a multi-level way. It means that, if the provided ```HelloWorldGenerator``` has a member field that depends ( hypothetically ) on another interface implementation, and there was available a service implementation in the class path, it will be _"provided"_ and injected on ```HelloWorldGenerator``` as well.

### Going a step further
Imagine you are implementing an API library or module. Its module should consume any class implementing an specific class. But, insted of load instances of its implementations you want to retrieve a list of classes, analyse them and instantiate them based on this analysis.

```java
// A hypothetical interface provided by your library at hypothetical-lib-1.0.jar
public interface Worker {}

@Service
// hypothetical annotation provided by your library at hypothetical-lib-1.0.jar
@NumberOfInstances( 10 )
// hypothetical class implementing your library interface at my-software-0.1-SNAPSHOT.jar
public class TwitterConsumerWorker implements Worker {}
```

The bellow code will be able to transparently load any implementation of ```Worker``` interface, and instantiate them based on the number of instances defined by ```NumberOfInstances``` annotation.

```java
public class Framework {

	public void start() {
		ServiceProvider provider = new ServiceProvider();
		List<Class<Worker>> workerClasses = provider.loadClassesImplementing( Worker.class );
		for ( Class<Worker> workerClass : workerClasses )
			instantiateAndRun( workerClass );
	}
	
	void instantiateAndRun( Class<Worker> workerClass ){
		int numberOfInstancess = workerClass.getAnnotation( NumberOfInstances.class ).value();
		for ( int i=0; i<numberOfInstancess; i++ ){
			Worker worker = workerClass.newInstance();
			run( worker );
		}
	}
	
	void run( Worker worker ) {
		System.out.println( "Running worker " + worker );
	}
}
```

### Configuring project for maven
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
		<version>0.10.1</version>
		<artifactId>trip-core</artifactId>
	</dependency>
...
```

### License

rTip is Apache 2.0 licensed.
