package trip.spi.inject;

import static trip.spi.inject.NameTransformations.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

public class FactoryProvidedClass {

	final String packageName;
	final String provider;
	final String providerMethod;
	final String type;
	final String typeName;

	public FactoryProvidedClass(
			final String packageName, final String provider,
			final String providedMethod, final String type, final String typeName ) {
		this.packageName = stripGenericsFrom( packageName );
		this.provider = stripGenericsFrom( provider );
		this.providerMethod = stripGenericsFrom( providedMethod );
		this.type = stripGenericsFrom( type );
		this.typeName = stripGenericsFrom( typeName );
	}

	public static FactoryProvidedClass from( Element element ) {
		ExecutableElement method = assertElementIsMethod( element );
		DeclaredType returnType = (DeclaredType)method.getReturnType();
		String type = returnType.toString();
		String typeName = returnType.asElement().getSimpleName().toString();
		return new FactoryProvidedClass(
				type.replace( "." + typeName, "" ),
				method.getEnclosingElement().asType().toString(),
				method.getSimpleName().toString(), type, typeName );
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

	public String type() {
		return this.type;
	}

	public String typeName() {
		return this.typeName;
	}

}
