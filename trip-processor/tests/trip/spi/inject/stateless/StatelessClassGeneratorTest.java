package trip.spi.inject.stateless;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StatelessClassGeneratorTest {

	@Test
	public void ensureThatGenerateTheExpectedClassFromInterfaceImplementation() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfInterface();
		final StatelessClassGenerator generator = new StatelessClassGenerator();
		final StringWriter writer = new StringWriter();
		generator.write( statelessClass, writer );
		final String expected = readFile( "tests/stateless-class-exposing-interface.txt" );
		assertEquals( expected, writer.toString() );
	}

	@Test
	public void ensureThatGenerateTheExpectedClassFromExposedServiceByItSelf() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfClass();
		final StatelessClassGenerator generator = new StatelessClassGenerator();
		final StringWriter writer = new StringWriter();
		generator.write( statelessClass, writer );
		final String expected = readFile( "tests/stateless-class-exposing-class.txt" );
		assertEquals( expected, writer.toString() );
	}

	StatelessClass createStatelessImplementationOfInterface() {
		return new StatelessClass(
			"", "important.api.Interface", "sample.project.ServiceFromInterface", false,
			list( voidMethod(), returnableMethod() ),
			list( returnableMethod() ),
			list( voidMethod() ) );
	}

	StatelessClass createStatelessImplementationOfClass() {
		return new StatelessClass(
			"my-self", "sample.project.ServiceFromInterface",
			"sample.project.ServiceFromInterface", true,
			list( voidMethod(), returnableMethod() ),
			list( returnableMethod() ),
			list( voidMethod() ) );
	}

	ExposedMethod returnableMethod() {
		return new ExposedMethod( "sum", "Long", list( "Double", "Integer" ) );
	}

	ExposedMethod voidMethod() {
		return new ExposedMethod( "voidMethod", "void", emptyStringList() );
	}

	@SuppressWarnings( "unchecked" )
	<T> List<T> list( T... ts ) {
		List<T> list = new ArrayList<T>();
		for ( T t : ts )
			list.add( t );
		return list;
	}

	List<String> emptyStringList() {
		return new ArrayList<>();
	}

	String readFile( String name ) throws IOException {
		byte[] allReadBytes = Files.readAllBytes( Paths.get( name ) );
		return new String( allReadBytes );
	}
}
