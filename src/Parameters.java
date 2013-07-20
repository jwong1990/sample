/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Parameters
{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public static String expID;
	public static String problemType;

	public static String dataInputFileName;

	public static int numRuns;
	public static int generations;
	public static int popSize;

	public static int genCap;
	public static int fitCap;

	public static String minORmax;
	public static int selectType;
	public static int scaleType;

	public static int xoverType;
	public static double xoverRate;
	public static int mutationType;
	public static double mutationRate;
	public static double hypermutationRate;
	public static int hypermutation;

	public static long seed;
	public static int numGenes;
	public static int geneSize;


	public static double tournParam;
	
	public static int numSolutions;
	public static int numBugs;
	public static String imageName;
	public static String ppmName;
	public static String sampleName;
	public static int elitism;
	
	public static boolean introns;
	public static int numIntrons;
	public static int islands;
	public static int islandCount;
	public static int islandSize;
	public static int migrationSize;
	public static int migrationInterval;
/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Parameters(String parmfilename) throws java.io.IOException{

		BufferedReader parmInput = new BufferedReader(new FileReader (parmfilename));

		expID = parmInput.readLine().substring(30);
		problemType = parmInput.readLine().substring(30);

		dataInputFileName = parmInput.readLine().substring(30);

		numRuns = Integer.parseInt(parmInput.readLine().substring(30).trim());
		generations = Integer.parseInt(parmInput.readLine().substring(30).trim());
		popSize = Integer.parseInt(parmInput.readLine().substring(30).trim());

		selectType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		scaleType = Integer.parseInt(parmInput.readLine().substring(30).trim());

		xoverType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		xoverRate = Double.parseDouble(parmInput.readLine().substring(30).trim());
		mutationType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		mutationRate = Double.parseDouble(parmInput.readLine().substring(30).trim());

		seed = Long.parseLong(parmInput.readLine().substring(30).trim());
		numGenes = Integer.parseInt(parmInput.readLine().substring(30).trim());
		
		tournParam = Double.parseDouble(parmInput.readLine().substring(30).trim());
		numSolutions = Integer.parseInt(parmInput.readLine().substring(30).trim());
		geneSize = 18 * numSolutions;
		
		numBugs = Integer.parseInt(parmInput.readLine().substring(30).trim());
		imageName = parmInput.readLine().substring(30);		
		ppmName = parmInput.readLine().substring(30);
		sampleName = parmInput.readLine().substring(30);
		hypermutation = Integer.parseInt(parmInput.readLine().substring(30).trim());
		hypermutationRate = Double.parseDouble(parmInput.readLine().substring(30));
		elitism = Integer.parseInt(parmInput.readLine().substring(30).trim());
		
		islands = Integer.parseInt(parmInput.readLine().substring(30).trim());
		islandCount = Integer.parseInt(parmInput.readLine().substring(30).trim());
		islandSize = popSize/islandCount;
		migrationSize = Integer.parseInt(parmInput.readLine().substring(30).trim());
		migrationInterval = Integer.parseInt(parmInput.readLine().substring(30).trim());		
		numIntrons = Integer.parseInt(parmInput.readLine().substring(30).trim());		
		if(numIntrons>0)
		{
			introns = true;
			geneSize = geneSize +(numIntrons*(numSolutions-1));
		}
		
		parmInput.close();

		if (scaleType==0 || scaleType==2) minORmax = "max";
		else minORmax = "min";

	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/
	
	
	/*
	 * Some methods to set value on class 
	 */
	public static int getGeneSize()
	{
		return geneSize;
	}
	public static void setGeneSize(int geneSize)
	{
		Parameters.geneSize = geneSize;
	}
	
	public static int getGeneNum(int geneNum)
	{
		return Parameters.numGenes;
	}
	
	public static void setGeneNum(int geneNum)
	{
		Parameters.geneSize = geneNum;
	}
	
	
	public static void outputParameters(FileWriter output) throws java.io.IOException{


		output.write("Experiment ID                :  " + expID + "\n");
		output.write("Problem Type                 :  " + problemType + "\n");

		output.write("Data Input File Name         :  " + dataInputFileName + "\n");

		output.write("Number of Runs               :  " + numRuns + "\n");
		output.write("Generations per Run          :  " + generations + "\n");
		output.write("Population Size              :  " + popSize + "\n");

		output.write("Selection Method             :  " + selectType + "\n");
		output.write("Fitness Scaling Type         :  " + scaleType + "\n");
		output.write("Min or Max Fitness           :  " + minORmax + "\n");

		output.write("Crossover Type               :  " + xoverType + "\n");
		output.write("Crossover Rate               :  " + xoverRate + "\n");
		output.write("Mutation Type                :  " + mutationType + "\n");
		output.write("Mutation Rate                :  " + mutationRate + "\n");

		output.write("Random Number Seed           :  " + seed + "\n");
		output.write("Number of Genes/Points       :  " + numGenes + "\n");
		output.write("Size of Genes                :  " + geneSize + "\n");		
		
		output.write("Initial Image Name           :  " + imageName + "\n");
		output.write("PPM output Name              :  " + ppmName + "\n");
		output.write("Sample Image Name            :  " + sampleName + "\n");
		output.write("\n\n");

	}
}   // End of Parameters.java **************************************************