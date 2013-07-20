/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.FileWriter;

class FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public String name;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public FitnessFunction() {

		System.out.print("Setting up Fitness Function.....");

	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/


	/**
	 * Compute the raw fitness of the chromosome 
	 * @param X
	 */
	public void doRawFitness(Chromo X){
		System.out.println("Executing FF Raw Fitness");
	}


	/**
	 * Print the chromosome to a file
	 * @param X
	 * @param output
	 * @throws java.io.IOException
	 */
	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
		System.out.println("Executing FF Gene Output");
	}


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/


}   // End of OneMax.java ******************************************************

