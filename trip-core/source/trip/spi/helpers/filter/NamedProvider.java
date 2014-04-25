package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.Name;

@RequiredArgsConstructor
public class NamedProvider<T> implements Condition<T> {

	final String name;

	@Override
	public boolean check(Object object) {
		val clazz = object.getClass();
		Name nameAnnotation = clazz.getAnnotation( Name.class );
		if ( nameAnnotation == null )
			return false;
		return name.equals( nameAnnotation.value() );
	}
}
