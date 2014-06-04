package trip.spi;

import java.lang.annotation.Annotation;

public interface ProviderContext {

	/**
	 * The list of annotations present on the target.
	 * 
	 * @return
	 */
	<A extends Annotation> A getAnnotation( Class<A> anntationClass );

	/**
	 * The type is expected to generate an object.
	 * 
	 * @return
	 */
	Class<?> targetType();

}
