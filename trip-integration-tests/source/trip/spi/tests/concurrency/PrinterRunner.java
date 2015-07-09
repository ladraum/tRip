package trip.spi.tests.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import lombok.RequiredArgsConstructor;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@RequiredArgsConstructor
public class PrinterRunner implements Runnable {

	final BlockingQueue<Object> events;
	final ServiceProvider provider;
	final CountDownLatch couter;

	@Override
	public void run() {
		try {
			Object last = null;
			while ( last != "END" ) {
				last = nextEvent();
				instantiateService().printNames();
			}
		} catch ( final Throwable cause ) {
			cause.printStackTrace();
		}
	}

	Object nextEvent() {
		try {
			return events.take();
		} catch ( final InterruptedException e ) {
			throw new RuntimeException( e );
		} finally {
			couter.countDown();
		}
	}

	StatelessService instantiateService() {
		try {
			return provider.load( StatelessService.class );
		} catch ( final ServiceProviderException e ) {
			throw new RuntimeException( e );
		}
	}
}
