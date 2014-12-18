package jus.poc.rw.v2;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class RWrsc extends Resource {
	/* nombre de lecteurs, redacteurs*/
	int readers,writers;
	/*Nombre de lecture restantes avant la prochaine écriture possible (ou la fin de la simu) */
	int N_R;

	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		readers =0;
		writers =0;
		N_R =0;
	}

	@Override
	public synchronized void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		
		while(writers>0){
			wait();
		}
		readers++;
		
		System.out.println("(LECTEUR) L'acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public synchronized void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		
		/*Si redacteur ou lecture ou nb minimal de lecture non atteint */
		while(writers>0 || readers>0 || N_R>0){
			wait();
		}
		writers++;
		
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public synchronized void endR(Actor arg0) throws InterruptedException {
		
		readers--;
		if(N_R>0){
			N_R--;
		}
		
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		notifyAll();

	}

	@Override
	public synchronized void endW(Actor arg0) throws InterruptedException {
		writers--;
		/* On set le nb de lectures à effectuer avant la prochaine ecriture */
		N_R=Simulator.NB_LECTURE;
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
		notifyAll();
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("(V2) Methode init non suportee...\n");
	}
	
	/**
	 * Retourne le nombre de lectures à effectuer
	 * @return nombre de lecture restantes
	 */
	public int get_NR(){
		return N_R;
	}

}
