package blah.tests;

import static org.junit.Assert.assertEquals;
import lombok.val;
import lombok.experimental.ExtensionMethod;

import org.junit.Test;

import blah.tests.Commons;

@ExtensionMethod( Commons.class )
public class GenericTest {

	@Test
	public void grantThatCouldExtractGenericAsExpected() {
		val hello = new Hello();
		Class<?> type = hello.getClass().extractGenericTypeFromFirstInterface();
		assertEquals( type, World.class );
	}

	class Hello implements Generic<World> {
		@Override
		public World doSomething() {
			return null;
		}
	}

	class World {
	}

	interface Generic<T> {
		T doSomething();
	}
}
