package trip.spi.inject.stateless;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import trip.spi.inject.GenerableClass;
import trip.spi.inject.SingletonImplementation;

public class StatelessClass implements GenerableClass {

	/**
	 * This attribute will be part of the class name.
	 */
	final long identifaction;

	/**
	 * An easy name to find the class when two or more services are provided to
	 * a same type.
	 */
	final String serviceIdentificationName;

	/**
	 * The package where the new class should be placed.
	 */
	final String packageName;

	/**
	 * The exposed type as Canonical Name notation.
	 */
	final String typeCanonicalName;

	/**
	 * The exposed type simple name.
	 */
	final String typeName;

	/**
	 * The implementation type as Canonical Name notation.
	 */
	final String implementationCanonicalName;

	/**
	 * Identify if the exposed type is a class or interface.
	 */
	final boolean exposedByClass;

	/**
	 * A list of methods that will be wrapped up.
	 */
	final List<ExposedMethod> exposedMethods;

	public StatelessClass( String serviceIdentificationName, String typeCanonicalName,
			String implementationCanonicalName, boolean exposedByClass, List<ExposedMethod> exposedMethods ) {
		this.serviceIdentificationName = serviceIdentificationName;
		this.packageName = extractPackageNameFrom( implementationCanonicalName );
		this.typeCanonicalName = typeCanonicalName;
		this.typeName = extractClassNameFrom( typeCanonicalName );
		this.implementationCanonicalName = implementationCanonicalName;
		this.exposedByClass = exposedByClass;
		this.exposedMethods = exposedMethods;
		this.identifaction = createIdentifier();
	}

	private long createIdentifier() {
		final int hashCode =
				String.format( "%s%s%s%s%s%s%s",
						packageName, serviceIdentificationName, typeCanonicalName,
						typeName, implementationCanonicalName, exposedByClass, exposedMethodsAsString() )
						.hashCode();

		return hashCode & 0xffffffffl;
	}

	private String exposedMethodsAsString() {
		StringBuilder buffer = new StringBuilder();
		for ( ExposedMethod method : exposedMethods )
			buffer
					.append( method.name )
					.append( method.returnType )
					.append( method.getParametersWithTypesAsString() );
		return buffer.toString();
	}

	// smaple.generated.Class
	String extractPackageNameFrom( String canonicalName ) {
		return canonicalName.replaceFirst( "(.*)\\.[^\\.]+", "$1" );
	}

	String extractClassNameFrom( String canonicalName ) {
		return canonicalName.replaceFirst( ".*\\.([^\\.]+)", "$1" );
	}

	public Long getIdentifaction() {
		return identifaction;
	}

	public String getServiceIdentificationName() {
		return serviceIdentificationName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTypeCanonicalName() {
		return typeCanonicalName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getImplementationCanonicalName() {
		return implementationCanonicalName;
	}

	public boolean isExposedByClass() {
		return exposedByClass;
	}

	public List<ExposedMethod> getExposedMethods() {
		return exposedMethods;
	}

	@Override
	public String getGeneratedClassCanonicalName() {
		return String.format( "%s.%sStateless%s",
				packageName,
				typeName,
				identifaction );
	}

	public static StatelessClass from( TypeElement type ) {
		String serviceIdentificationName = SingletonImplementation.getProvidedServiceName( type );
		String typeCanonicalName = SingletonImplementation.getProvidedServiceClassAsString( type );
		String implementationCanonicalName = type.asType().toString();
		boolean exposedByClass = isImplementingClass( typeCanonicalName, type );
		List<ExposedMethod> exposedMethods = retrieveExposedMethods( type );
		return new StatelessClass( serviceIdentificationName, typeCanonicalName, implementationCanonicalName, exposedByClass,
				exposedMethods );
	}

	public static boolean isImplementingClass( String typeCanonicalName, TypeElement type ) {
		while ( !Object.class.getCanonicalName().equals( type.asType().toString() ) ) {
			for ( TypeMirror interfaceType : type.getInterfaces() )
				if ( typeCanonicalName.equals( interfaceType.toString() ) )
					return false;
			type = ( (TypeElement)( (DeclaredType)type.getSuperclass() ).asElement() );
		}
		return true;
	}

	static List<ExposedMethod> retrieveExposedMethods( TypeElement type ) {
		List<ExposedMethod> list = new ArrayList<ExposedMethod>();
		for ( Element method : type.getEnclosedElements() ) {
			if ( method.getKind().equals( ElementKind.METHOD ) )
				list.add( ExposedMethod.from( (ExecutableElement)method ) );
		}
		return list;
	}
}
