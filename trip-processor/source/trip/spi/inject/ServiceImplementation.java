package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import java.util.List;

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
			String interfaceClass, String implementationClass ) {
		this.interfaceClass = stripGenericsFrom( interfaceClass );
		this.implementationClass = stripGenericsFrom( implementationClass );
	}

	public static ServiceImplementation from( Element element ) {
		TypeElement type = (TypeElement)element;
		String interfaceClass = getProvidedServiceClass( type );
		return new ServiceImplementation( interfaceClass, type.asType().toString() );
	}

	public static String getProvidedServiceClass( TypeElement type ) {
		if ( isAnnotatedForStateless( type ) )
			return getProvidedServiceClassForStateless( type );
		if ( isAnnotatedForSingleton( type ) )
			return getProvidedServiceClassForSingleton( type );
		return null;
	}

	private static boolean isAnnotatedForStateless( TypeElement type ) {
		return type.getAnnotation( Stateless.class ) != null;
	}

	private static boolean isAnnotatedForSingleton( TypeElement type ) {
		return type.getAnnotation( Singleton.class ) != null;
	}

	private static String getProvidedServiceClassForStateless( TypeElement type ) {
		TypeMirror statelessService = getProvidedStatelessAsTypeMirror( type );
		List<? extends TypeMirror> interfaces = type.getInterfaces();
		if ( isStatelessAnnotationClassBlank( statelessService ) ) {
			if ( interfaces.isEmpty() )
				return type.asType().toString();
			return interfaces.get( 0 ).toString();
		}
		return statelessService.toString();
	}

	private static TypeMirror getProvidedStatelessAsTypeMirror( TypeElement type ) {
		try {
			Stateless singleton = type.getAnnotation( Stateless.class );
			if ( singleton != null )
				singleton.value();
			return null;
		} catch ( MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isStatelessAnnotationClassBlank( TypeMirror providedClass ) {
		return providedClass.toString().equals( Stateless.class.getCanonicalName() );
	}

	private static String getProvidedServiceClassForSingleton( TypeElement type ) {
		TypeMirror providedClass = getProvidedSingletonAsTypeMirror( type );
		List<? extends TypeMirror> interfaces = type.getInterfaces();
		if ( isSingletonAnnotationBlank( providedClass ) ) {
			if ( interfaces.isEmpty() )
				return type.asType().toString();
			return interfaces.get( 0 ).toString();
		}
		return providedClass.toString();
	}

	private static TypeMirror getProvidedSingletonAsTypeMirror( TypeElement type ) {
		try {
			Singleton singleton = type.getAnnotation( Singleton.class );
			if ( singleton != null )
				singleton.value();
			return null;
		} catch ( MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isSingletonAnnotationBlank( TypeMirror providedClass ) {
		return providedClass.toString().equals( Singleton.class.getCanonicalName() );
	}

	public String implementationClass() {
		return this.implementationClass;
	}

	public String interfaceClass() {
		return this.interfaceClass;
	}
}
