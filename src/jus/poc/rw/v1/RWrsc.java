package jus.poc.rw.v1;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class RWrsc extends Resource {

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
	}
}
