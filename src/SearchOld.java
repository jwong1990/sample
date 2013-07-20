/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SearchOld {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double sumBestFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;
	public static double stdevBestFitness;
	public static double bestOfGenSquared;
	public static double averageBestFitness;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;
	//variables used for triggered hypermutation
	public static int currentBest;
	public static int trigger = 0;
	public static double defaultMutationRate;
	private static double fitnessStats[][];  // 0=Avg, 1=Best 2= Standard Deviation

	private static boolean triggered;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void main(String[] args) throws java.io.IOException{

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

	//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);
		parmValues.toString();

	//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		Parameters.outputParameters(summaryOutput);
		
	//	Set up Fitness Statistics matrix
		fitnessStats = new double[7][Parameters.generations]; //adjust fitness stats to keep track of st dev for avg and best fitness
		
		for (int i=0; i<Parameters.generations; i++){
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
			fitnessStats[2][i] = 0;
			fitnessStats[3][i] = 0;
			fitnessStats[4][i] = 0;
			fitnessStats[5][i] = 0;
			fitnessStats[6][i] = 0;
		}

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("FUZ")) {
			problem = new FuzzerGA();
			//read input file
			FuzzerHelper.readFile();
		}
		else 
			System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();
		//array for the top five best scores in each gen.
		int fivePercent = (int)(Parameters.popSize * .02);
		Chromo[] topFivePercent = new Chromo[fivePercent]; 

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		//  Start program for multiple runs
		for (R = 1; R <= Parameters.numRuns; R++){

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				member[i] = new Chromo();
				child[i] = new Chromo();
				if(i < topFivePercent.length)
					topFivePercent[i] = new Chromo();
			}
			

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				stdevRawFitness = 0; 
				stdevBestFitness = 0;
				bestOfGenChromo.rawFitness = defaultBest;
				bestOfGenSquared = 0;
				sumBestFitness = 0;


				//	Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;

					problem.doRawFitness(member[i]);

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
						member[i].rawFitness * member[i].rawFitness;
					

					if (Parameters.minORmax.equals("max")){
						if (member[i].rawFitness > bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					

				}

				// Accumulate fitness statistics
				// 0 is the average raw fitness score for generation
				// 1 is the best fitness score for generation
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;
							
				averageRawFitness = sumRawFitness / Parameters.popSize;

				stdevRawFitness = Math.sqrt(
							Math.abs(sumRawFitness2 - 
							sumRawFitness*sumRawFitness/Parameters.popSize)
							/
							(Parameters.popSize-1)
							);
				
				fitnessStats[2][G] += stdevRawFitness;

				//add this column to the array to calculate the variance of the average best score
				//for each generation
				fitnessStats[4][G] += bestOfGenChromo.rawFitness * bestOfGenChromo.rawFitness;
				
				//add this columnn to the array to calculate the variance of the average average scores
				fitnessStats[5][G] += (sumRawFitness / Parameters.popSize) * (sumRawFitness / Parameters.popSize);
				
				
				
				// Output generation statistics to screen
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput); ////first column
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);	///second column
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput); //// third column
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);		/////fourth column
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);		//// fifth column
				summaryOutput.write("\n");


		// *********************************************************************
		// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
		// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = member[i].rawFitness + .000001;
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = 1/(member[i].rawFitness + .000001);
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=Parameters.popSize-1; i>0; i--){
						for (int j=0; j<i; j++){
							if (memberFitness[j] > memberFitness[j+1]){
								TmemberIndex = memberIndex[j];
								TmemberFitness = memberFitness[j];
								memberIndex[j] = memberIndex[j+1];
								memberFitness[j] = memberFitness[j+1];
								memberIndex[j+1] = TmemberIndex;
								memberFitness[j+1] = TmemberFitness;
							}
						}
					}
					//  Copy ordered array to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=1; i<Parameters.popSize; i++){
						for (int j=(Parameters.popSize - 1); j>=i; j--){
							if (memberFitness[j-i] < memberFitness[j]){
								TmemberIndex = memberIndex[j-1];
								TmemberFitness = memberFitness[j-1];
								memberIndex[j-1] = memberIndex[j];
								memberFitness[j-1] = memberFitness[j];
								memberIndex[j] = TmemberIndex;
								memberFitness[j] = TmemberFitness;
							}
						}
					}
					//  Copy array order to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}


		// *********************************************************************
		// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
		// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member[i].proFitness = member[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}

		// *********************************************************************
		// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
		// *********************************************************************

				int parent1 = -1;
				int parent2 = -1;

				if(Parameters.elitism ==1 ){
					if(Parameters.scaleType == 2){
						//members are already sorted by scaled fitness
						//store best five percent of population
						for(int i = 0; i <fivePercent; i++ ){
							Chromo.copyB2A(topFivePercent[i], member[(member.length - 1) - i]);
						}					
					}
				}
				//  Assumes always two offspring per mating
				for (int i = 0; i<Parameters.popSize; i=i+2){

					//	Select Two Parents
					parent1 = Chromo.selectParent();
					parent2 = parent1;
					while (parent2 == parent1){
						parent2 = Chromo.selectParent();
					}

					//	Crossover Two Parents to Create Two Children
					randnum = r.nextDouble();
					if (randnum < Parameters.xoverRate){
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
					}
					else {
						Chromo.mateParents(parent1, member[parent1], child[i]);
						Chromo.mateParents(parent2, member[parent2], child[i+1]);
					}
				} // End Crossover

				//	Mutate Children
				for (int i=0; i<Parameters.popSize; i++){
					child[i].doMutation();
				}

				//	Swap Children with Last Generation
				for (int i=0; i<Parameters.popSize; i++){
					Chromo.copyB2A(member[i], child[i]);
				}
				
				if(Parameters.elitism == 1){
					//insert best five percent of previous generation into new generation
					for (int i=0; i<topFivePercent.length; i++){
						Chromo.copyB2A(member[i], topFivePercent[i]);
					}
				}
				
				
			/************************
			 * check for hypermutation	
			 ************************/
				
				if(Parameters.hypermutation == 1){
				//initial population, set current fitness score
				if(G == 0)
				currentBest = (int)bestOfRunChromo.rawFitness;
				
				//if current best same as last generation, increment counter
				else if(currentBest >= (int)bestOfRunChromo.rawFitness && !triggered){
					trigger ++;
					//System.out.println("Stagnate generation: " + trigger);
				}
				else if (currentBest < (int)bestOfRunChromo.rawFitness && !triggered){
					//if it is not, then set the new current best 
					// and reset counter
					currentBest = (int)bestOfRunChromo.rawFitness;
					trigger = 0;
				}
				//check if hyper mutation is triggered
				 if(triggered){
					trigger -= 1;
					//System.out.println("hypermutation triggered and trigger at " + trigger);
					if(trigger == 23){
						triggered = false;
						trigger = 0;
						Parameters.mutationRate = defaultMutationRate;
						System.out.println("trigger off, mutation rate at "+ Parameters.mutationRate);
						currentBest = 0;
					}
				}
				
				//if after 10 generations, we have a stagnate optimum
				//trigger hypermutation - mutation is .08 for 5 generations
				if(trigger == 30){
					defaultMutationRate = Parameters.mutationRate;
					Parameters.mutationRate = Parameters.hypermutationRate;
					System.out.println("hyper-mutation is now triggered for 5 generations");
				    triggered = true;
				}
			}
				
			} //  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);
		} //End of a Run
		
		//calculate standard deviation for the best scores, for each generation throughout the 50 runs
		//fitnessStats[3] is the index for the standard deviation values
		//fitnessStats[4] is the sum of the best scores squared
		//fitnessStats[1] is the sum of the best scores
		for(int x = 0; x < Parameters.generations; x ++){
		fitnessStats[3][x] = Math.sqrt(
							Math.abs(fitnessStats[4][x] - 
							(fitnessStats[1][x]*fitnessStats[1][x] /Parameters.numRuns))
				/ (Parameters.numRuns-1)
				);
		
		}
		FuzzerHelper.printFrequencies(1);
		//calculate standard deviation for average average fitness, for each generation throughout the 50 runs
		//fitnessStats[6] is the index for the standard deviation for the average average
		//fitnessStats[5] is the sum of average raw scores squared
		//fitnessStats[0] is the sum of the average raw scores
		for(int x = 0; x < Parameters.generations; x ++){
			fitnessStats[6][x] = Math.sqrt(
							Math.abs(fitnessStats[5][x] - 
							(fitnessStats[0][x]*fitnessStats[0][x] /Parameters.numRuns))
				/ (Parameters.numRuns-1)
				);
		}
		
		Hwrite.left("B", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		//	Output Fitness Statistics matrix
		summaryOutput.write("****Overall Generation Statistics*** \n");
		summaryOutput.write("Gen                 AvgFit              BestFit              StDev		Stdev for Best      Stdev for avg avg\n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[2][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[3][i], 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[6][i], 20, 2,summaryOutput);
			summaryOutput.write("\n");
		}

		summaryOutput.write("\n");
		summaryOutput.close();

		System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);

	} // End of Main Class

}   // End of Search.Java ******************************************************

