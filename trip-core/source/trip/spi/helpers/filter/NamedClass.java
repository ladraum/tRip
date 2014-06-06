package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import trip.spi.Name;

@RequiredArgsConstructor
public class NamedClass<T> implements Condition<Class<T>> {

	final String name;

	@Override
	public boolean check(Class<T> clazz) {
		Name nameAnnotation = clazz.getAnnotation( Name.class );
		if ( nameAnnotation == null )
			return false;
		return name.equals( nameAnnotation.value() );
	}
}
