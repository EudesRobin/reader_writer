/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.poc.rw;

import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;

/**
 * Define the gobal behavior of an actor in Reader/writer protocole.
 * @author morat 
 */
public abstract class Actor extends Thread{
	private static int identGenerator=0;
	/** the identificator of the actor */
	protected int ident;
	/** le pool of resources to be used */
	protected IResource[] resources;
	/** the ramdomly service for use delay*/
	protected Aleatory useLaw;
	/** the ramdomly service for vacation delay*/
	protected Aleatory vacationLaw;
	/** the observator */
	protected IObservator observator;
	/** the number of iteration to do, 0 means infinity */
	protected int nbIteration;
	/** the rank of the last access done or under execution */
	protected int accessRank;

	/* MODIF */
	/* Pour arrêter les thread proprepement, sans être unsafe (à l'inverse de la méthode stop de thread */
	protected boolean runable;
	/**
	 * Constructor
	 * @param useLaw the gaussian law for using delay
	 * @param vacationLaw the gaussian law for the vacation delay
	 * @param iterationLaw the gaussian law for the number of iteration do do
	 * @param selection the resources to used
	 * @param observator th observator of the comportment
	 */
	public Actor(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw, IResource[] selection, IObservator observator){
		this.ident = identGenerator++;
		resources = selection;
		this.useLaw = useLaw;
		this.vacationLaw = vacationLaw;
		nbIteration=iterationLaw.next();
		setName(getClass().getSimpleName()+"-"+ident());
		this.observator=observator;
		this.runable=true;
	}
	/**
	 * the behavior of an actor accessing to a resource.
	 */
	public void run(){
		this.observator.startActor(this); // Event Start actor
		for(accessRank=1; accessRank!=nbIteration && runable; accessRank++) {
			temporizationVacation(vacationLaw.next());
			acquire();
			temporizationUse(useLaw.next());
			release();
		}
		this.observator.stopActor(this); // Event Stop actor
		System.out.println("L'acteur n°"+this.ident+"a effectué tout ses accès [TERMINAISON]");
	}
	/**
	 * the temporization for using the ressources.
	 */
	private void temporizationUse(int delai) {
		try{Thread.sleep(delai);}catch(InterruptedException e1){e1.printStackTrace();}		
	}
	/**
	 * the temporization between access to the resources.
	 */
	private void temporizationVacation(int delai) {
		try{Thread.sleep(delai);}catch(InterruptedException e1){e1.printStackTrace();}		
	}
	/**
	 * the acquisition stage of the resources.
	 */
	private void acquire(){
		for(IResource rsc:resources){
			try {
				this.acquire(rsc);
				System.out.println("acquire rsc n°"+rsc.ident());
				if(Simulator.version.equalsIgnoreCase("v4")){ /* Afin de faciliter l'apparition d'IB*/
					sleep(5000);
				}
			} catch (InterruptedException | DeadLockException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("Toutes les ressources requises par l'acteur sont acquises");
	}
	/**
	 * the release stage of the resources prevously acquired
	 */
	private void release(){
		for(IResource rsc:resources){
			try {
				this.release(rsc);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Restart the actor at the start of his execution, having returned all the resources acquired.
	 * @param resource the resource at the origin of the deadlock.
	 */
	protected void restart(IResource resource) {
		this.observator.restartActor(this,resource); // Event restart Actor
		this.release();
		this.run();
	}
	/**
	 * acquisition proceeding specific to the type of actor.
	 * @param resource the required resource
	 * @throws InterruptedException
	 * @throws DeadLockException
	 */
	protected abstract void acquire(IResource resource) throws InterruptedException, DeadLockException;
	/**
	 * restitution proceeding specific to the type of actor.
	 * @param resource
	 * @throws InterruptedException
	 */
	protected abstract void release(IResource resource) throws InterruptedException;
	/**
	 * return the identification of the actor
	 * return the identification of the actor
	 */
	public final int ident(){return ident;}
	/**
	 * the rank of the last access done or under execution.
	 * @return the rank of the last access done or under execution
	 */
	public final int accessRank(){return accessRank;}

	public void clean_stop(){
		runable=false;
	}
}