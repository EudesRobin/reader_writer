package jus.poc.rw.v3;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWrsc extends Resource {
	// Notre verrou
	ReentrantLock Lock = new ReentrantLock();
	Condition high = Lock.newCondition();
	Condition low = Lock.newCondition();
	/*
	 * Nombre de rédacteurs en attente
	 */
	int nb_writters=0;
	int nb_readers=0;

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		nb_readers=0;
		nb_writters=0;
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		nb_readers++;
		
		Lock.lock();
		while(nb_writters>0 && Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			high.await();
		}
		
		nb_readers--;
		
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		nb_writters++;
		
		Lock.lock();
		while(nb_readers>0 && Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE")){
			low.await();
		}
		
		nb_writters--;
		
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			high.signal();
		}
		
		Lock.unlock();
		
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// notif event deja effectue par la classe Reader
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		if((Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE"))){
			low.signal();
		}
		Lock.unlock();
		
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// Notif event deja effectue par la classe Writer
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("(V3) methode init non suportee..\n");
	}
}
