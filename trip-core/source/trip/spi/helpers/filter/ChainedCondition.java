package trip.spi.helpers.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ChainedCondition<T> implements Condition<T> {

	final List<Condition<T>> conditions = new ArrayList<>();

	@Override
	public boolean check( T object ) {
		for ( val condition : conditions )
			if ( !condition.check( object ) )
				return false;
		return true;
	}

	public void add( Condition<T> condition ) {
		conditions.add( condition );
	}
}
