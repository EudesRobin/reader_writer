package jus.poc.rw.v4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jus.poc.rw.Actor;
import jus.poc.rw.IResource;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Detector implements IDetector {
	
	int[] attente_acteur;
	List<Actor>[] utilisation_rsc;
	
	public Detector(int nb_acteur,int nb_rsc) {
		 attente_acteur=new int[nb_acteur];
		 for(int acteur:attente_acteur){
			 acteur=-1;
		 }
		 utilisation_rsc=new List[nb_rsc];
		 for(List<Actor> l:utilisation_rsc){
			 l=new LinkedList<Actor>();
		 }
	}

	@Override
	public void freeResource(Actor arg0, IResource arg1) {
		utilisation_rsc[arg1.ident()].remove(arg0);
	}

	@Override
	public void useResource(Actor arg0, IResource arg1) {
		utilisation_rsc[arg1.ident()].add(arg0);
		attente_acteur[arg0.ident()]=-1;
	}

	@Override
	public void waitResource(Actor arg0, IResource arg1)
			throws DeadLockException {
		attente_acteur[arg0.ident()]=arg1.ident();
		detectDeadlock(arg0,arg1);
	}
	
	public void detectDeadlock(Actor arg0, IResource arg1)
			throws DeadLockException{
		Iterator itr = utilisation_rsc[arg1.ident()].iterator();
		while(itr.hasNext()) {
			//attente_acteur[(int)itr.next()]
			
		}
	}

}
