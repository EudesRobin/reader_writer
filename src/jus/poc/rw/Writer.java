package jus.poc.rw;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;

public class Writer extends Actor {

	public Writer(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void release(IResource resource) throws InterruptedException {
		// TODO Auto-generated method stub

	}

}
