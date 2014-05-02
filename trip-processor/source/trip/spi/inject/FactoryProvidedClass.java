package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

import trip.spi.Name;

public class FactoryProvidedClass {

	final String packageName;
	final String provider;
	final String providerName;
	final String providerMethod;
	final String type;
	final String typeName;
	final String name;

	public FactoryProvidedClass(
			final String packageName, final String provider,
			final String providerName,
			final String providedMethod, final String type,
			final String typeName, final String name ) {
		this.packageName = stripGenericsFrom( packageName );
		this.provider = stripGenericsFrom( provider );
		this.providerName = stripGenericsFrom( providerName );
		this.providerMethod = stripGenericsFrom( providedMethod );
		this.type = stripGenericsFrom( type );
		this.typeName = stripGenericsFrom( typeName );
		this.name = name;
	}

	public static FactoryProvidedClass from( Element element ) {
		ExecutableElement method = assertElementIsMethod( element );
		String providerName = method.getEnclosingElement().getSimpleName().toString();
		String provider = method.getEnclosingElement().asType().toString();
		DeclaredType returnType = (DeclaredType)method.getReturnType();
		String type = returnType.toString();
		String typeName = returnType.asElement().getSimpleName().toString();
		return new FactoryProvidedClass(
				provider.replace( "." + providerName, "" ),
				provider,
				provider.replace( ".", "Dot" ),
				method.getSimpleName().toString(),
				type, typeName,
				extractNameFrom( element ) );
	}

	static String extractNameFrom( Element element ) {
		Name name = element.getAnnotation( Name.class );
		if ( name != null )
			return name.value();
		return null;
	}

	static ExecutableElement assertElementIsMethod( Element element ) {
		return (ExecutableElement)element;
	}

	public String packageName() {
		return this.packageName;
	}

	public String provider() {
		return this.provider;
	}

	public String providerMethod() {
		return this.providerMethod;
	}

	public String providerName() {
		return this.providerName;
	}

	public String type() {
		return this.type;
	}

	public String typeName() {
		return this.typeName;
	}

}
