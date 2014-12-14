package jus.poc.rw;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;

public class Reader extends Actor {

	public Reader(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator);
	}

	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		super.observator.requireResource(this, resource);; // Event demande rsc
		resource.beginR(this); // demande rsc
	}

	@Override
	protected void release(IResource resource) throws InterruptedException {
		resource.endR(this); // free
		super.observator.releaseResource(this,resource); // Event rsc libre
	}

}
