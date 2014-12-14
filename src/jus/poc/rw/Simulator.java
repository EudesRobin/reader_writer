package jus.poc.rw;


import jus.poc.rw.control.Observator;



/**
 * Main class for the Readers/Writers application. This class firstly creates a pool of read/write resources  
 * implementing interface IResource. Then it creates readers and writers operating on these resources.
 * @author P.Morat & F.Boyer
 */
public class Simulator{

	protected static final String OPTIONFILENAME = "option.xml";
	/** the version of the protocole to be used */
	protected static String version;
	/** the number of readers involve in the simulation */
	protected static int nbReaders;
	/** the number of writers involve in the simulation */
	protected static int nbWriters;
	/** the number of resources involve in the simulation */
	protected static int nbResources;
	/** the number of resources used by an actor */
	protected static int nbSelection;
	/** the law for the reader using delay */
	protected static int readerAverageUsingTime;
	protected static int readerDeviationUsingTime;
	/** the law for the reader vacation delay */
	protected static int readerAverageVacationTime;
	protected static int readerDeviationVacationTime;
	/** the law for the writer using delay */
	protected static int writerAverageUsingTime;
	protected static int writerDeviationUsingTime;
	/** the law for the writer vacation delay */
	protected static int writerAverageVacationTime;
	protected static int writerDeviationVacationTime;
	/** the law for the writer number of iterations */
	protected static int writerAverageIteration;
	protected static int writerDeviationIteration;
	/** the chosen policy for priority */
	protected static String policy;
	/**
	 * make a permutation of the array
	 * @param array the array to be mixed
	 */
	protected static void mixe(Object[] array) {
		int i1, i2;
		Object a;
		for(int k = 0; k < 2 * array.length; k++){
			i1 = Aleatory.selection(1, array.length)[0];
			i2 = Aleatory.selection(1, array.length)[0];
			a = array[i1]; array[i1] = array[i2]; array[i2] = a;
		}
	}
	/**
	 * Retreave the parameters of the application.
	 * @param file the final name of the file containing the options. 
	 */
	protected static void init(String file) {
		// retreave the parameters of the application
		final class Properties extends java.util.Properties {
			private static final long serialVersionUID = 1L;
			public int get(String key){return Integer.parseInt(getProperty(key));}
			public Properties(String file) {
				try{
					loadFromXML(ClassLoader.getSystemResourceAsStream(file));
				}catch(Exception e){e.printStackTrace();}			
			}
		}
		Properties option = new Properties("jus/poc/rw/options/"+file);
		version = option.getProperty("version");
		nbReaders = Math.max(0,new Aleatory(option.get("nbAverageReaders"),option.get("nbDeviationReaders")).next());
		nbWriters = Math.max(0,new Aleatory(option.get("nbAverageWriters"),option.get("nbDeviationWriters")).next());
		nbResources = Math.max(0,new Aleatory(option.get("nbAverageResources"),option.get("nbDeviationResources")).next());
		nbSelection = Math.max(0,Math.min(new Aleatory(option.get("nbAverageSelection"),option.get("nbDeviationSelection")).next(),nbResources));
		readerAverageUsingTime = Math.max(0,option.get("readerAverageUsingTime"));
		readerDeviationUsingTime = Math.max(0,option.get("readerDeviationUsingTime"));
		readerAverageVacationTime = Math.max(0,option.get("readerAverageVacationTime"));
		readerDeviationVacationTime = Math.max(0,option.get("readerDeviationVacationTime"));
		writerAverageUsingTime = Math.max(0,option.get("writerAverageUsingTime"));
		writerDeviationUsingTime = Math.max(0,option.get("writerDeviationUsingTime"));
		writerAverageVacationTime = Math.max(0,option.get("writerAverageVacationTime"));
		writerDeviationVacationTime = Math.max(0,option.get("writerDeviationVacationTime"));
		writerAverageIteration = Math.max(0,option.get("writerAverageIteration"));
		writerDeviationIteration = Math.max(0,option.get("writerDeviationIteration"));
		policy = option.getProperty("policy");
	}
	public static void main(String... args) throws Exception{
		// set the application parameters
		init((args.length==1)?args[0]:OPTIONFILENAME);

		/* Init Observator */
		Observator observator = new Observator(null); // controler = null pour l'instant
		observator.init(nbReaders+nbWriters,nbResources);
		
		
		/* init ressource pool*/
		ResourcePool pool = new ResourcePool(nbResources,null,observator,"jus.poc.rw."+version+".RWrsc");


		Actor[] array_rw = new Actor[nbReaders+nbWriters];

		/* Init Readers */
		int n=0;
		while(n<nbReaders){
			array_rw[n] = new Reader(new Aleatory(readerAverageUsingTime,readerDeviationUsingTime),
					new Aleatory(readerAverageVacationTime,readerDeviationVacationTime),
					new Aleatory(0,0),
					pool.selection(nbSelection),
					observator);
			
			n++;
		}

		while(n<nbReaders+nbWriters){
			array_rw[n] = new Writer(new Aleatory(writerAverageUsingTime,writerDeviationUsingTime),
					new Aleatory(writerAverageVacationTime,writerDeviationVacationTime),
					new Aleatory(writerAverageIteration,writerDeviationIteration),
					pool.selection(nbSelection),
					observator);
			n++;
		}

		/* On mélange les acteurs dans le tableau...*/
		mixe(array_rw);
		

		/* On lance la simu !*/
		for(Actor acteur:array_rw){
			acteur.start();	
		}
		
		/* COMMENTS V1 - OBJECTIF 2 
		 * 
		 * Les ReentrantReadWriteLock utilisés nous garantissent le fonctionnement suivant :
		 *  n readers sur une ressource ou 1 writer en simultané sur une ressource.
		 *  Grace aux affichages fournis durant l'execution, on peut voir que plusieurs lecteurs vont acceder
		 *  à une même ressource, sans que le précédant lecteur l'ai libéré. En revanche, un writer ne pourra
		 *  accéder à la ressource que si personne l'utilise et personne d'autre que lui pourra l'utiliser
		 *  tant qu'il ne l'a pas libérée.
		 *  
		 * rmq : les lecteurs terminent jamais, car la cond limite de la boucle du run jamais atteinte
		 *  le compteur démarre à 1, le nb d'ité est 0 pour les lecteurs ( et le compteur augmente tjrs,
		 *  donc ne vaudra jamais 0 ... 
		 */
		int nbWriters_end=0;
		if(version.equalsIgnoreCase("v1")){
			while(nbWriters_end!=nbWriters){
				nbWriters_end =0;
				for(Actor act:array_rw){
					if(act.getClass().getSimpleName().equalsIgnoreCase("Writer") && !act.isAlive()){
						nbWriters_end++;
					}
				}
			}
			/* Tout les writers ont effectués leurs ecritures, on termine les lecteurs... */
		}
	}

}