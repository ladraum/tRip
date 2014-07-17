package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import trip.spi.Name;
import trip.spi.ProviderContext;

public class ProducerImplementation {

	final String packageName;
	final String provider;
	final String providerName;
	final String providerMethod;
	final String type;
	final String typeName;
	final String name;
	final boolean expectsContext;
	final String serviceFor;
	final boolean stateless;

	public ProducerImplementation(
			final String packageName, final String provider,
			final String providedMethod, final String type,
			final String typeName, final String name,
			final boolean expectsContext,
			final String serviceFor, final boolean stateless ) {
		this.packageName = stripGenericsFrom( packageName );
		this.provider = stripGenericsFrom( provider );
		this.providerMethod = stripGenericsFrom( providedMethod );
		this.type = stripGenericsFrom( type );
		this.typeName = stripGenericsFrom( typeName );
		this.name = name;
		this.expectsContext = expectsContext;
		this.serviceFor = serviceFor;
		this.providerName = String.valueOf( createIdentifier() );
		this.stateless = stateless;
	}

	private long createIdentifier() {
		int hashCode =
				String.format( "%s%s%s%s%s%s%s%s",
						packageName, provider, providerMethod,
						type, typeName, name, expectsContext, stateless )
						.hashCode();

		return hashCode & 0xffffffffl;
	}

	public static ProducerImplementation from( TypeElement type ) {
		String providerName = type.getSimpleName().toString();
		String provider = type.asType().toString();
		return new ProducerImplementation(
				provider.replace( "." + providerName, "" ),
				provider, "", ServiceImplementation.getProvidedServiceClass( type ), "",
				extractNameFrom( type ), false, "", true );
	}

	public static ProducerImplementation from( ExecutableElement element ) {
		ExecutableElement method = assertElementIsMethod( element );
		TypeElement type = (TypeElement)method.getEnclosingElement();
		String providerName = type.getSimpleName().toString();
		String provider = type.asType().toString();
		DeclaredType returnType = (DeclaredType)method.getReturnType();
		String typeAsString = returnType.toString();
		String typeName = returnType.asElement().getSimpleName().toString();
		return new ProducerImplementation(
				provider.replace( "." + providerName, "" ),
				provider,
				method.getSimpleName().toString(),
				typeAsString, typeName,
				extractNameFrom( method ),
				measureIfExpectsContextAsParameter( method ),
				ServiceImplementation.getProvidedServiceClass( type ), false );
	}

	static boolean measureIfExpectsContextAsParameter( ExecutableElement method ) {
		List<? extends VariableElement> parameters = method.getParameters();
		if ( parameters.size() == 0 )
			return false;
		VariableElement variableElement = parameters.get( 0 );
		if ( !variableElement.asType().toString().equals( ProviderContext.class.getCanonicalName() ) )
			throw new IllegalStateException(
					"@Provider annotated methods should have no parameters, or the parameter should be of type ProviderContext." );
		return true;
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

	public String name() {
		return this.name;
	}
}
