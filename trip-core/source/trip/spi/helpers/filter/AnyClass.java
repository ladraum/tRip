package trip.spi.helpers.filter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnyClass<T> implements Condition<Class<T>> {

	@Override
	public boolean check( Class<T> object ) {
		return true;
	}
}
