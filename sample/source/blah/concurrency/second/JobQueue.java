package blah.concurrency.second;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import lombok.val;

public class JobQueue {

	final Locker emptySlotsLocker = new Locker();
	final Locker fullSlotsLocker = new Locker();

	final AtomicReference<Job>[] jobs;
	final Cursor readCursor;
	final Cursor writeCursor;
	final int bufferSize;

	volatile int wroteData = 0;

	@SuppressWarnings("unchecked")
	public JobQueue( int bufferSize ) {
		this.jobs = new AtomicReference[ bufferSize ];
		this.readCursor = new Cursor(bufferSize);
		this.writeCursor = new Cursor(bufferSize);
		this.bufferSize = bufferSize;
		preAllocateBuffer();
	}

	void preAllocateBuffer() {
		for ( int i=0; i<bufferSize; i++ )
			this.jobs[i] = new AtomicReference<>();
	}

	public void put( Job job ) throws InterruptedException {
		val writablePosition = writeCursor.next();
		AtomicReference<Job> reference = this.jobs[ writablePosition ];
		while( !reference.compareAndSet(null, job))
			LockSupport.parkNanos(reference, 1l);
		wroteData++;
	}

	int waitUntilHaveWritableSlots() {
		return writeCursor.next();
	}

	boolean haveWritableSlots() {
		return this.wroteData <= bufferSize;
	}

	public Job get() throws InterruptedException {
		val readableSlot = waitUntilHaveReadableSlots();
		Job job = readableSlot.getAndSet(null);
		this.wroteData--;
		return job;
	}

	AtomicReference<Job> waitUntilHaveReadableSlots() {
		AtomicReference<Job> reference = this.jobs[ readCursor.next() ];
		while( reference.get() == null )
			LockSupport.parkNanos(reference, 1l);
		return reference;
	}

	boolean haveReadbleData() {
		return this.wroteData > 0;
	}
}
