package jus.poc.rw.deadlock;

import jus.poc.rw.Actor;
import jus.poc.rw.IResource;

public class Detector implements IDetector {

	public Detector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void freeResource(Actor arg0, IResource arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void useResource(Actor arg0, IResource arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitResource(Actor arg0, IResource arg1)
			throws DeadLockException {
		// TODO Auto-generated method stub

	}

}
