package trip.spi.helpers.filter;

import java.util.ArrayList;

import lombok.val;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Filter {

	public static Iterable<?> filter( Iterable<?> self, Condition condition ) {
		val list = new ArrayList<Object>();
		for ( Object object : self )
			if ( condition.check(object) )
				list.add(object);
		return list;
	}
	
	public static Object first( Iterable<?> self, Condition condition ) {
		for ( Object object : self )
			if ( condition.check(object) )
				return object;
		return null;
	}
}
