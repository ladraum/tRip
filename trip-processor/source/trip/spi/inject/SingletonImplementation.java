package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Singleton;
import trip.spi.Stateless;

public class SingletonImplementation {

	final String interfaceClass;
	final String implementationClass;

	public SingletonImplementation(
			final String interfaceClass, final String implementationClass ) {
		this.interfaceClass = stripGenericsFrom( interfaceClass );
		this.implementationClass = stripGenericsFrom( implementationClass );
	}

	public static SingletonImplementation from( final Element element ) {
		final TypeElement type = (TypeElement)element;
		final String interfaceClass = getProvidedServiceClassAsString( type );
		return new SingletonImplementation( interfaceClass, type.asType().toString() );
	}

	public static String getProvidedServiceClassAsString( final TypeElement type ) {
		TypeMirror typeMirror = getProvidedServiceClass( type );
		if ( typeMirror == null )
			return null;
		return typeMirror.toString();
	}

	public static TypeMirror getProvidedServiceClass( final TypeElement type ) {
		if ( isAnnotatedForStateless( type ) )
			return getProvidedServiceClassForStateless( type );
		if ( isAnnotatedForSingleton( type ) )
			return getProvidedServiceClassForSingleton( type );
		return null;
	}

	public static String getProvidedServiceName( final TypeElement type ) {
		if ( isAnnotatedForStateless( type ) )
			return type.getAnnotation( Stateless.class ).name();
		if ( isAnnotatedForSingleton( type ) )
			return type.getAnnotation( Singleton.class ).name();
		return null;
	}

	private static boolean isAnnotatedForStateless( final TypeElement type ) {
		return type.getAnnotation( Stateless.class ) != null;
	}

	private static boolean isAnnotatedForSingleton( final TypeElement type ) {
		return type.getAnnotation( Singleton.class ) != null;
	}

	private static TypeMirror getProvidedServiceClassForStateless( final TypeElement type ) {
		final TypeMirror statelessService = getProvidedStatelessAsTypeMirror( type );
		if ( isStatelessAnnotationClassBlank( statelessService ) )
			return type.asType();
		return statelessService;
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

	private static TypeMirror getProvidedServiceClassForSingleton( final TypeElement type ) {
		final TypeMirror providedClass = getProvidedSingletonAsTypeMirror( type );
		if ( isSingletonAnnotationBlank( providedClass ) )
			return type.asType();
		return providedClass;
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
