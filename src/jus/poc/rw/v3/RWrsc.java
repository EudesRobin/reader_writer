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
	int nb_writters;
	int nb_lect;

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		nb_writters=0;
		nb_lect=0;
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
			Lock.lock();
		while(nb_writters>0 && Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			System.out.println("(LECTEUR) [HIGH_WRITE] AWAIT (Acteur "+arg0.ident()+")");
			high.await();
		}
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		nb_writters++;
		if(Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE") && nb_lect!=0){
			System.out.println("(REDACTEUR) [LOW_WRITE] AWAIT (Acteur "+arg0.ident()+")");
			low.await();
		}
		Lock.lock();
		nb_writters--;
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			System.out.println("(LECTEUR) [HIGH_WRITE] SIGNAL (Acteur "+arg0.ident()+")");
			high.signal();
		}
		if(--nb_lect==0 && Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE")){
			System.out.println("(LECTEUR) [LOW_WRITE] SIGNAL ALL (Acteur "+arg0.ident()+")");
			low.signalAll();
		}
		Lock.unlock();
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// notif event deja effectue par la classe Reader
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		if(Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE")){
			nb_lect=Simulator.NB_LECTURE;
			System.out.println("(REDACTEUR) [LOW_WRITE] Prochaine écriture possible dans "+nb_lect+
					" lectures terminées (Acteur "+arg0.ident()+")");
		}
		Lock.unlock();
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		// Notif event deja effectue par la classe Writer
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("(V1) methode init non suportee..\n");
	}
}
