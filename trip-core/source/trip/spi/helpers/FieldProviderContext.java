package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import trip.spi.ProviderContext;

@Getter
@Accessors
@RequiredArgsConstructor
public class FieldProviderContext implements ProviderContext {

	final Field field;

	@Override
	public <A extends Annotation> A getAnnotation( Class<A> annotationClass ) {
		return field.getAnnotation( annotationClass );
	}

	@Override
	public Class<?> targetType() {
		return field.getType();
	}
}
