package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Singleton;

public class SingletonImplementation {

	final String interfaceClass;
	final String implementationClass;

	public SingletonImplementation(
			String interfaceClass, String implementationClass ) {
		this.interfaceClass = stripGenericsFrom( interfaceClass );
		this.implementationClass = stripGenericsFrom( implementationClass );
	}

	public static SingletonImplementation from( Element element ) {
		TypeElement type = (TypeElement)element;
		List<? extends TypeMirror> interfaces = type.getInterfaces();
		String interfaceClass = extractValidProvider( type, interfaces );
		return new SingletonImplementation( interfaceClass, type.asType().toString() );
	}

	private static String extractValidProvider( TypeElement type, List<? extends TypeMirror> interfaces ) {
		TypeMirror providedClass = getProvidedClass( type );
		if ( isAnnotationBlank( providedClass ) && interfaces.size() > 1 )
			throw new IllegalStateException( "You should specify a provider interface for " + type.asType().toString() );
		if ( !isAnnotationBlank( providedClass ) )
			return providedClass.toString();
		if ( interfaces.isEmpty() )
			return type.asType().toString();
		return interfaces.get( 0 ).toString();
	}

	private static boolean isAnnotationBlank( TypeMirror providedClass ) {
		return providedClass.toString().equals( Singleton.class.getCanonicalName() );
	}

	private static TypeMirror getProvidedClass( TypeElement type ) {
		try {
			Singleton provides = type.getAnnotation( Singleton.class );
			provides.value();
			return null;
		} catch ( MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	public String implementationClass() {
		return this.implementationClass;
	}

	public String interfaceClass() {
		return this.interfaceClass;
	}
}
