package trip.spi.tests;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.tests.concurrency.PrinterRunner;

public class ConcurrencyTest {

	final static int NUMBER_OF_CONSUMER = 30;
	final ServiceProvider provider = new ServiceProvider();
	final ExecutorService executor = Executors.newCachedThreadPool();
	final CountDownLatch counter = new CountDownLatch( NUMBER_OF_CONSUMER * 2 );

	@SneakyThrows
	@Test( timeout = 4000 )
	public void runConcurrentStressTestInStatelessCreation() {
		for ( int i = 0; i < NUMBER_OF_CONSUMER; i++ ) {
			val inbox = new LinkedBlockingQueue<Object>();
			executor.submit( new PrinterRunner( inbox, provider, counter ) );
			executor.submit( new MessageDispatcher( inbox ) );
		}
		counter.await();
	}

	@After
	public void shutdownConsumers() {
		executor.shutdownNow();
	}
}

@RequiredArgsConstructor
class MessageDispatcher implements Runnable {

	final LinkedBlockingQueue<Object> inbox;

	@Override
	@SneakyThrows
	public void run() {
		Thread.sleep( 100 );
		inbox.put( "NEXT" );
		inbox.put( "END" );
	}
}