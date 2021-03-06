package trip.spi.inject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.*;

import trip.spi.*;

@SupportedAnnotationTypes( "trip.spi.*" )
public class ProvidedClassesProcessor extends AbstractProcessor {

	static final String EOL = "\n";
	static final String SERVICES = "META-INF/services/";
	static final String PROVIDER_FILE = SERVICES + ProviderFactory.class.getCanonicalName();

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate = this.mustacheFactory.compile( "META-INF/provided-class.mustache" );
	final List<String> factoryProviders = new ArrayList<>();
	final Map<String, List<String>> providers = new HashMap<>();

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		try {
			System.out.println( "tRip is processing " + roundEnv );
			process( roundEnv );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return true;
	}

	void process( RoundEnvironment roundEnv ) throws IOException {
		processServices( roundEnv, Service.class );
		processProducers( roundEnv, Producer.class );
		if ( !this.factoryProviders.isEmpty() )
			createServiceProviderForClassProviders();
		if ( !this.providers.isEmpty() )
			createServiceLocators();
		flush();
	}

	void processServices( RoundEnvironment roundEnv, Class<? extends Annotation> annotation ) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( annotation );
		for ( Element element : annotatedElements )
			if ( element.getKind() == ElementKind.CLASS )
				memorizeAProviderImplementation( ProviderImplementation.from( element ) );
	}

	void memorizeAProviderImplementation( ProviderImplementation from ) {
		List<String> list = this.providers.get( from.interfaceClass() );// ,
		if ( list == null ) {
			list = new ArrayList<>();
			this.providers.put( from.interfaceClass(), list );
		}
		list.add( from.implementationClass() );
	}

	void processProducers( RoundEnvironment roundEnv, Class<? extends Annotation> annotation ) throws IOException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( annotation );
		for ( Element element : annotatedElements )
			if ( element.getKind() == ElementKind.METHOD )
				createAProviderFactoryClassFrom( FactoryProvidedClass.from( element ) );
	}

	void createAProviderFactoryClassFrom( FactoryProvidedClass clazz ) throws IOException {
		String name = createClassCanonicalName( clazz );
		System.out.println( "Generating " + name );
		JavaFileObject sourceFile = filer().createSourceFile( name );
		Writer writer = sourceFile.openWriter();
		this.providedClazzTemplate.execute( writer, clazz );
		writer.close();
		memorizeProvider( name );
	}

	String createClassCanonicalName( FactoryProvidedClass clazz ) {
		return String.format( "%s.%sAutoGeneratedProvider%s%s",
				clazz.packageName(),
				clazz.typeName(),
				clazz.providerName(),
				clazz.providerMethod() );
	}

	void memorizeProvider( String name ) {
		this.factoryProviders.add( name );
	}

	void createServiceProviderForClassProviders() throws IOException {
		Writer writer = createServiceForProviderInterface();
		for ( String provider : this.factoryProviders )
			writer.write( provider + EOL );
		writer.close();
	}

	Writer createServiceForProviderInterface() throws IOException {
		return createResource( PROVIDER_FILE );
	}

	void createServiceLocators() throws IOException {
		for ( String interfaceClass : this.providers.keySet() ) {
			System.out.println( "Registering service providers for " + interfaceClass );
			Writer resource = createResource( SERVICES + interfaceClass );
			for ( String implementation : this.providers.get( interfaceClass ) )
				resource.write( implementation + EOL );
			resource.close();
		}
	}

	Writer createResource( String resourcePath ) throws IOException {
		FileObject resource = filer().getResource( StandardLocation.SOURCE_OUTPUT, "", resourcePath );
		URI uri = resource.toUri();
		createNeededDirectoriesTo( uri );
		File file = createFile( uri );
		return new FileWriter( file );
	}

	void createNeededDirectoriesTo( URI uri ) {
		File dir = null;
		if ( uri.isAbsolute() )
			dir = new File( uri ).getParentFile();
		else
			dir = new File( uri.toString() ).getParentFile();
		dir.mkdirs();
	}

	File createFile( URI uri ) throws IOException {
		File file = new File( uri );
		if ( !file.exists() )
			file.createNewFile();
		return file;
	}

	Filer filer() {
		return this.processingEnv.getFiler();
	}

	void flush() {
		this.factoryProviders.clear();
		this.providers.clear();
	}

	/**
	 * We just return the latest version of whatever JDK we run on. Stupid?
	 * Yeah, but it's either that or warnings on all versions but 1. Blame Joe.
	 * 
	 * PS: this method was copied from Project Lombok. ;)
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.values()[SourceVersion.values().length - 1];
	}
}
