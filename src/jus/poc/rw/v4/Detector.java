package jus.poc.rw.v4;

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
		for(int i=0;i<attente_acteur.length;i++){
			attente_acteur[i]=(-1);
		}

		// Warning, mais on mettra que des Actor dans la liste ( pb pour faire un tableau de liste de type <T>
		utilisation_rsc=new List[nb_rsc];
		for(int l=0;l<utilisation_rsc.length;l++){
			utilisation_rsc[l]= new LinkedList<Actor>();
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

	/*
	 * BUG ACTUEL : detecte seulement les cycles faisant intervenir qu'un seul autre acteur
	 * (tentative infrctueuse de prolonger la recherche de cycles en profondeur en commentaires).
	 */
	public void detectDeadlock(Actor arg0, IResource arg1)
			throws DeadLockException{

		//LinkedList<Actor> waiting = new LinkedList<Actor>();
		//Iterator<Actor> itr_waiting = waiting.iterator();

		Iterator<Actor> itr = utilisation_rsc[arg1.ident()].iterator();
		while(itr.hasNext()) { // je parcour les Acteurs utilisant la ressource demandée
			Object current = itr.next();
			
			if(attente_acteur[((Actor)current).ident()]!=-1){ // Si un des acteurs est en attente
				Iterator<Actor> itrb = utilisation_rsc[attente_acteur[((Actor)current).ident()]].iterator(); // it sur la rsc
				
				while(itrb.hasNext()){
					Object currentb = itrb.next();
					
					if(((Actor)currentb).ident()==arg0.ident()){ // On a un cycle !
						System.out.println("DEADLOCK");
						throw new DeadLockException(arg0,arg1);
					}
				}
			}
		}

		/*if(attente_acteur[((Actor)current).ident()]!=-1){ // un des act est en attente
		if(!waiting.contains((Actor)current)){ // déjà non add
			waiting.add((Actor)current);
		}


		do{
			Object sec = itr_waiting.next();

			Iterator<Actor> itrb = utilisation_rsc[attente_acteur[((Actor)sec).ident()]].iterator(); // it sur la rsc
			while(itrb.hasNext()){

				Object currentb = itrb.next();

				if(attente_acteur[((Actor)currentb).ident()]!=-1){
					if(!waiting.contains((Actor)currentb)){ // déjà non add
						waiting.add((Actor)currentb);
					}
				}

				if(((Actor)currentb).ident()==arg0.ident()){
					System.out.println("DEADLOCK");
					throw new DeadLockException(arg0,arg1);
				}
			}
		}while(itr_waiting.hasNext());*/
	}
}
