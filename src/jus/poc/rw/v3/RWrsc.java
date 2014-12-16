package jus.poc.rw.v3;

import java.util.concurrent.Semaphore;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class RWrsc extends Resource {
	
	int nb_readers;
	int nb_writers;

	Semaphore MutexW;
	Semaphore MutexR;
	Semaphore rsc;

	/**
	 * Pour bloquer les lecteurs HIGH_WRITE */
	Semaphore SasR;
	
	
	/**
	 * Nombre de lectures restantes (pour LOW_WRITE)
	 */
	int nr;
	
	public RWrsc(IDetector detector, IObservator observator) {
		super(detector, observator);
		nb_readers=0;
		nb_writers=0;
		MutexW =new Semaphore(1);
		MutexR =new Semaphore(1);
		SasR=new Semaphore(1);
		rsc=new Semaphore(1);
		nr=0;
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			SasR.acquire();
			System.out.println("(LECTEUR) [HIGH_WRITE] L'Acteur n°" + arg0.ident()+"a passé SAS_R");
		}
		
		MutexR.acquire();
		if(nb_readers++==0){
			rsc.acquire(); // Le premier lecteur prend le verrou sur la rsc
		}
		MutexR.release();
		
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			SasR.release();
			System.out.println("(LECTEUR) [HIGH_WRITE] L'Acteur n°" + arg0.ident()+"libère SAS_R");
		}
		
		System.out.println("(LECTEUR) L'acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
		
	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		MutexW.acquire();
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			if(nb_writers++==0){
			SasR.acquire(); // On bloque les futurs lecteurs
			System.out.println("(REDACTEUR) [HIGH_WRITE] L'Acteur n°" + arg0.ident()+"acquire SAS_R");
			}
		}
		MutexW.release();
		rsc.acquire();
		
		System.out.println("(REDACTEUR) L'acteur n°"+arg0.ident()+" accède à la rsc n°"+this.ident);
		this.observator.acquireResource(arg0,this); // Event acquire rsc
	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		MutexR.acquire();
		if(--nb_readers==0){
			rsc.release();
		}
		if(Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE") && nr>0){
			nr--;
			System.out.println("(LECTEUR) [LOW_WRITE]  Lectures restantes minimales "+ nr);
		}
		MutexR.release();
		System.out.println("(LECTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		MutexW.acquire();
		if(Simulator.getpolicy().equalsIgnoreCase("HIGH_WRITE")){
			if(--nb_writers==0){
				SasR.release();
				System.out.println("(REDACTEUR) [HIGH_WRITE]  L'Acteur n°" + arg0.ident()+"libère SAS_R");
			}
		}else if(Simulator.getpolicy().equalsIgnoreCase("LOW_WRITE")){
			nr=Simulator.NB_LECTURE;
			System.out.println("(REDACTEUR) [LOW_WRITE]  On fixe le nb min de lectures a effectuer : "+nr);
		}
		MutexW.release();
		rsc.release();
		System.out.println("(REDACTEUR) L'Acteur n°"+arg0.ident()+" libère la rsc n°"+this.ident);
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("(V3) methode init non suportee..\n");
	}

}
