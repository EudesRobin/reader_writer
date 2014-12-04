package jus.poc.rw;

import jus.poc.rw.control.IObservator;
import jus.poc.rw.control.Observator;
import jus.poc.rw.deadlock.Detector;
import jus.poc.rw.deadlock.IDetector;


/**
 * Main class for the Readers/Writers application. This class firstly creates a pool of read/write resources  
 * implementing interface IResource. Then it creates readers and writers operating on these resources.
 * @author P.Morat & F.Boyer
 */
public class Simulator{
	
	static Reader[] reader_tab;
	static Writer[] writer_tab;
	
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
		//nbResources = option.get("nbAverageResources");
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
		
		//init detector
		
		//init observator
		
		//init ressource pool
		jus.poc.rw.ResourcePool p = new ResourcePool(Simulator.nbResources,null,null,"jus.poc.rw.v1.RWrsc");
		
		Simulator.reader_tab = new Reader[Simulator.nbReaders];
		for(int i = 0;i<Simulator.nbReaders;i++){
			Simulator.reader_tab[i] = new Reader(new Aleatory(Simulator.readerAverageUsingTime,Simulator.readerDeviationUsingTime),
					new Aleatory(Simulator.readerAverageVacationTime,Simulator.readerDeviationVacationTime),
					new Aleatory(0,0),
					p.selection(Simulator.nbResources),
					null);
		}
		
		
		Simulator.writer_tab = new Writer[Simulator.nbWriters];
		for(int i=0;i<Simulator.nbWriters;i++){
			Simulator.writer_tab[i] = new Writer(new Aleatory(Simulator.writerAverageUsingTime,Simulator.writerDeviationUsingTime),
					new Aleatory(Simulator.writerAverageVacationTime,Simulator.writerDeviationVacationTime),
					new Aleatory(Simulator.writerAverageIteration,Simulator.writerDeviationIteration),
					p.selection(Simulator.nbResources),
					null);
		}
		
		
		
	}
}
