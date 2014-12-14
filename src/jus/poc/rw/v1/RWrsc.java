package jus.poc.rw.v1;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWrsc extends Resource {
	// Notre verrou
	ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		readWriteLock.readLock().lock();
		System.out.println("Le lecteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		readWriteLock.writeLock().lock();
		System.out.println("Le rédacteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		readWriteLock.readLock().unlock();
		System.out.println("Le lecteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// notif event deja effectue par la classe Reader
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		readWriteLock.writeLock().unlock();
		System.out.println("Le rédacteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// Notif event deja effectue par la classe Writer
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// arg0 la donnee a utiliser pour init notre ressource
		throw new UnsupportedOperationException("methode init non suportee en v1\n");
	}
}
