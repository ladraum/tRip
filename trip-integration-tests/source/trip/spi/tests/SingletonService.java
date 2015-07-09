package trip.spi.tests;

import trip.spi.Singleton;

@Singleton( exposedAs = Runnable.class )
public class SingletonService implements Runnable {

	@Override
	public void run() {
	}
}
