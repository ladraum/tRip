package trip.spi.tests;

import trip.spi.Stateless;

@Stateless( exposedAs = Runnable.class )
public class StatelessService implements Runnable {

	@Override
	public void run() {
	}
}
