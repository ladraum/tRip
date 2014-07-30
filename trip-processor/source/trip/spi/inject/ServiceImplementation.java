package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Singleton;
import trip.spi.Stateless;

public class ServiceImplementation {

	final String interfaceClass;
	final String implementationClass;

	public ServiceImplementation(
			final String interfaceClass, final String implementationClass ) {
		this.interfaceClass = stripGenericsFrom( interfaceClass );
		this.implementationClass = stripGenericsFrom( implementationClass );
	}

	public static ServiceImplementation from( final Element element ) {
		final TypeElement type = (TypeElement)element;
		final String interfaceClass = getProvidedServiceClass( type );
		return new ServiceImplementation( interfaceClass, type.asType().toString() );
	}

	public static String getProvidedServiceClass( final TypeElement type ) {
		if ( isAnnotatedForStateless( type ) )
			return getProvidedServiceClassForStateless( type );
		if ( isAnnotatedForSingleton( type ) )
			return getProvidedServiceClassForSingleton( type );
		return null;
	}

	private static boolean isAnnotatedForStateless( final TypeElement type ) {
		return type.getAnnotation( Stateless.class ) != null;
	}

	private static boolean isAnnotatedForSingleton( final TypeElement type ) {
		return type.getAnnotation( Singleton.class ) != null;
	}

	private static String getProvidedServiceClassForStateless( final TypeElement type ) {
		final TypeMirror statelessService = getProvidedStatelessAsTypeMirror( type );
		if ( isStatelessAnnotationClassBlank( statelessService ) )
			return type.asType().toString();
		return statelessService.toString();
	}

	private static TypeMirror getProvidedStatelessAsTypeMirror( final TypeElement type ) {
		try {
			final Stateless singleton = type.getAnnotation( Stateless.class );
			if ( singleton != null )
				singleton.exposedAs();
			return null;
		} catch ( final MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isStatelessAnnotationClassBlank( final TypeMirror providedClass ) {
		return providedClass.toString().equals( Stateless.class.getCanonicalName() );
	}

	private static String getProvidedServiceClassForSingleton( final TypeElement type ) {
		final TypeMirror providedClass = getProvidedSingletonAsTypeMirror( type );
		if ( isSingletonAnnotationBlank( providedClass ) )
				return type.asType().toString();
		return providedClass.toString();
	}

	private static TypeMirror getProvidedSingletonAsTypeMirror( final TypeElement type ) {
		try {
			final Singleton singleton = type.getAnnotation( Singleton.class );
			if ( singleton != null )
				singleton.exposedAs();
			return null;
		} catch ( final MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isSingletonAnnotationBlank( final TypeMirror providedClass ) {
		return providedClass.toString().equals( Singleton.class.getCanonicalName() );
	}

	public String implementationClass() {
		return this.implementationClass;
	}

	public String interfaceClass() {
		return this.interfaceClass;
	}
}
