package blah.concurrency.second;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Delegate;

public class Locker {

	@Delegate
	final ReentrantLock lock = new ReentrantLock();
	final Condition waitCondition = lock.newCondition();
	
	public void parkNanos( long nanos ){
		try {
			waitCondition.awaitNanos(nanos);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
