package jus.poc.rw.v4;

import java.util.concurrent.Semaphore;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class RWrsc extends Resource {
	
	int  Nblect;
	Semaphore lectred = new Semaphore(1);
	Semaphore mutex = new Semaphore(1);

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		Nblect=0;
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		mutex.acquire();
		if(Nblect++==0){
			lectred.acquire();
		}
		mutex.release();
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc

	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		lectred.acquire();
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc

	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		mutex.acquire();
		if(--Nblect==0){
			lectred.release();
		}
		mutex.release();
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// notif event deja effectue par la classe Reader
		
		
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		lectred.release();
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// Notif event deja effectue par la classe Writer
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("(V4) methode init non suportee..\n");
	}

}
