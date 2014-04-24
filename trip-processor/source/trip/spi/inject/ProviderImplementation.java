package trip.spi.inject;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Service;

public class ProviderImplementation {

	final String interfaceClass;
	final String implementationClass;

	public ProviderImplementation(
			String interfaceClass, String implementationClass ) {
		this.interfaceClass = interfaceClass;
		this.implementationClass = implementationClass.replaceAll("<[^>]*>", "");
	}

	public static ProviderImplementation from( Element element ) {
		TypeElement type = (TypeElement)element;
		List<? extends TypeMirror> interfaces = type.getInterfaces();
		String interfaceClass = extractValidProvider( type, interfaces );
		return new ProviderImplementation( interfaceClass, type.asType().toString() );
	}

	private static String extractValidProvider( TypeElement type, List<? extends TypeMirror> interfaces ) {
		TypeMirror providedClass = getProvidedClass( type );
		if ( isAnnotationBlank( providedClass ) && interfaces.size() > 1 )
			throw new IllegalStateException( "You should specify a provider interface for " + type.asType().toString() );
		if ( !isAnnotationBlank( providedClass ) )
			return providedClass.toString();
		return interfaces.get( 0 ).toString();
	}

	private static boolean isAnnotationBlank( TypeMirror providedClass ) {
		return providedClass.toString().equals( Service.class.getCanonicalName() );
	}

	private static TypeMirror getProvidedClass( TypeElement type ) {
		try {
			Service provides = type.getAnnotation( Service.class );
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
