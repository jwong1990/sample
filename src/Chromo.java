/******************************************************************************
 *  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
 *  Version 2, January 18, 2004
 *******************************************************************************/

import java.util.Random;

public class Chromo {
	/*******************************************************************************
	 * INSTANCE VARIABLES *
	 *******************************************************************************/

	public String chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;
	public static String maxChromo;
	public static double maxProFitness = 0;
	public static double maxSclFitness = 0;
	public static double maxRawFitness = 0;
	
	static Chromo max = new Chromo();

	/*******************************************************************************
	 * INSTANCE VARIABLES *
	 *******************************************************************************/

	private static double randnum;

	/*******************************************************************************
	 * CONSTRUCTORS *
	 *******************************************************************************/

	public Chromo() {

		// Set gene values to a randum sequence of 1's and 0's
		char geneBit;
		chromo = "";			
		if(!Parameters.introns)
		{
			
			for (int i = 0; i < Parameters.numGenes; i++) {
				for (int j = 0; j < Parameters.geneSize; j++) {
					randnum = Search.r.nextDouble();
					if (randnum > 0.5)
						geneBit = '0';
					else
						geneBit = '1';
					this.chromo = chromo + geneBit;
				}
			}
		} else
		{
			boolean addIntrons = false;
			for (int i = 0; i < Parameters.numGenes; i++) {
				for (int j = 0; j < 18 * Parameters.numSolutions; j++) {
					if(j % 18 == 0 && addIntrons)
					{
						for(int k=0; k<Parameters.numIntrons;k++)
							chromo = chromo + "-";
						j--;
						addIntrons = false;
						continue;
					}										
					addIntrons = true;
					randnum = Search.r.nextDouble();
					if (randnum > 0.5)
						geneBit = '0';
					else
						geneBit = '1';
					this.chromo = chromo + geneBit;
				}
			}
		}
		this.rawFitness = -1; // Fitness not yet evaluated
		this.sclFitness = -1; // Fitness not yet scaled
		this.proFitness = -1; // Fitness not yet proportionalized
	}

	/*******************************************************************************
	 * MEMBER METHODS *
	 *******************************************************************************/

	// Get Alpha Represenation of a Gene **************************************

	public String getGeneAlpha(int geneID) {
		int start = geneID * Parameters.geneSize;
		int end = (geneID + 1) * Parameters.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	// Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****

	public int getIntGeneValue(int geneID) {
		String geneAlpha = "";
		int geneValue;
		char geneSign;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i = Parameters.geneSize - 1; i >= 1; i--) {
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1')
				geneValue = geneValue
						+ (int) Math.pow(2.0, Parameters.geneSize - i - 1);
		}
		geneSign = geneAlpha.charAt(0);
		if (geneSign == '1')
			geneValue = geneValue
					- (int) Math.pow(2.0, Parameters.geneSize - 1);
		return (geneValue);
	}

	// Get Integer Value of a Gene (Positive only) ****************************

	public int getPosIntGeneValue(int geneID) {
		String geneAlpha = "";
		int geneValue;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i = Parameters.geneSize - 1; i >= 0; i--) {
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1')
				geneValue = geneValue
						+ (int) Math.pow(2.0, Parameters.geneSize - i - 1);
		}
		return (geneValue);
	}

	// Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation() {

		String mutChromo = "";
		char x;

		switch (Parameters.mutationType) {

		case 1: // Replace with new random number

			for (int j = 0; j < (Parameters.geneSize * Parameters.numGenes); j++) {
				x = this.chromo.charAt(j);
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate) {
					if (x == '1')
						x = '0';
					else
						x = '1';
				}
				mutChromo = mutChromo + x;
			}
			this.chromo = mutChromo;
			break;

		default:
			System.out.println("ERROR - No mutation method selected");
		}
	}

	public String getChromo() {
		return this.chromo;
	}

	/*******************************************************************************
	 * STATIC METHODS *
	 *******************************************************************************/

	// Select a parent for crossover ******************************************

	public static int selectParent() {

		double rWheel = 0;
		int j = 0;
		int k = 0;
		switch (Parameters.selectType) {

		case 1: // Proportional Selection
			randnum = Search.r.nextDouble();
			for (j = 0; j < Parameters.popSize; j++) {
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel){
					if(Search.member[j].sclFitness > max.sclFitness){
						max = Search.member[j];
						/*
						maxChromo = Search.member[j].getChromo();
						maxProFitness = Search.member[j].proFitness;
						maxSclFitness = Search.member[j].sclFitness;
						maxRawFitness = Search.member[j].rawFitness;
						*/
					}
					return (j);
				}
			}
			break;

		case 2: // Tournament Selection
			Random rand = new Random();
			randnum = Search.r.nextDouble(); // Random number between 0.0-1.0
												// (used for comparing w/
												// tournament parameter)
			double param = Parameters.tournParam; // Get tournament parameter

			j = rand.nextInt(Parameters.popSize);
			k = rand.nextInt(Parameters.popSize);

			double jFitness = Search.member[j].rawFitness;
			double kFitness = Search.member[k].rawFitness;

			// Compare random number with tournament parameter
			// If the random number is less than the parameter, return the
			// greater fitness individual
			// Otherwise, do the opposite
			if (randnum < param) {
				if (jFitness > kFitness)
					return j;
				else
					return k;
			} else {
				if (jFitness < kFitness)
					return j;
				else
					return k;
			}
		case 3: // Random Selection
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			return (j);
			
		case 5: //alternative tournament selection
			/*
			 * Alternative Tournament
			 * In this tournament, 4 candidates will be selected
			 * they will compete in a bracket style tournament
			 * the final candidate will be the one with
			 * the highest score. 
			 */
			int[] candidates = new int[4];
			int finalCandidate1 = 0;
			int finalCandidate2 = 0;
			randnum = Search.r.nextDouble();
			 double kparam = .7;
			
			//randomly select the four candidates
			for(int i = 0; i < 4; i ++){
				candidates[i] = (int)(Math.random() * Parameters.popSize);
			}
			
			//candidate with higher score gets selected
			if(randnum < kparam){
				//Select first candidate for final 
				if(Search.member[candidates[0]].rawFitness < Search.member[candidates[1]].rawFitness){
					finalCandidate1 = candidates[1];
				}
				else{
					finalCandidate1 = candidates[0];
				}
			
			
				//Select second candidate for final
				if(Search.member[candidates[2]].rawFitness < Search.member[candidates[3]].rawFitness){
					finalCandidate2 = candidates[3];
				}
				else{
					finalCandidate2 = candidates[2];
				}
			}
			//candidate with lower score gets selected
			else{
				//Select first candidate for final 
				if(Search.member[candidates[0]].rawFitness < Search.member[candidates[1]].rawFitness){
					finalCandidate1 = candidates[0];
				}
				else{
					finalCandidate1 = candidates[1];
				}
			
			
				//Select second candidate for final
				if(Search.member[candidates[2]].rawFitness < Search.member[candidates[3]].rawFitness){
					finalCandidate2 = candidates[2];
				}
				else{
					finalCandidate2 = candidates[3];
				}
				
			}
			
			//return candidate with best score
			if(Search.member[finalCandidate1].rawFitness < Search.member[finalCandidate2].rawFitness){
				return finalCandidate2;
			}
			else{
				return finalCandidate1;
			}
			

		default:
			System.out.println("ERROR - No selection method selected");
		}
		return (-1);
	}

	//Select parent (For island model)
	public static int selectParent(int islandNum) {

		double rWheel = 0;
		int j = 0;
		int k = 0;
		switch (Parameters.selectType) {

		case 1: // Proportional Selection
			randnum = Search.r.nextDouble();
			int start = Parameters.islandSize * islandNum;
			int end = start + Parameters.islandSize - 1;
			
			for (j = start; j < end; j++) {
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel){
					if(Search.member[j].sclFitness > max.sclFitness){
						max = Search.member[j];
						/*
						maxChromo = Search.member[j].getChromo();
						maxProFitness = Search.member[j].proFitness;
						maxSclFitness = Search.member[j].sclFitness;
						maxRawFitness = Search.member[j].rawFitness;
						*/
					}
					return (j);
				}
			}
			break;

		case 2: // Tournament Selection
			Random rand = new Random();
			randnum = Search.r.nextDouble(); // Random number between 0.0-1.0
												// (used for comparing w/
												// tournament parameter)
			double param = Parameters.tournParam; // Get tournament parameter

			j = rand.nextInt(Parameters.islandSize + (islandNum * Parameters.islandSize)) + (Parameters.islandSize*islandNum) - 1;
			k = rand.nextInt(Parameters.islandSize + (islandNum * Parameters.islandSize)) + (Parameters.islandSize*islandNum) - 1;

			double jFitness = Search.member[j].rawFitness;
			double kFitness = Search.member[k].rawFitness;

			// Compare random number with tournament parameter
			// If the random number is less than the parameter, return the
			// greater fitness individual
			// Otherwise, do the opposite
			if (randnum < param) {
				if (jFitness > kFitness)
					return j;
				else
					return k;
			} else {
				if (jFitness < kFitness)
					return j;
				else
					return k;
			}
		case 3: // Random Selection
			randnum = Search.r.nextDouble();
			j = Search.r.nextInt(Parameters.islandSize) + 
								(Parameters.islandSize*islandNum);
			return (j);
			
		case 5: //alternative tournament selection
			/*
			 * Alternative Tournament
			 * In this tournament, 4 candidates will be selected
			 * they will compete in a bracket style tournament
			 * the final candidate will be the one with
			 * the highest score. 
			 */
			int[] candidates = new int[4];
			int finalCandidate1 = 0;
			int finalCandidate2 = 0;
			randnum = Search.r.nextDouble();
			 double kparam = .7;
			Random rIslandInd = new Random();
			int startIndex = Parameters.islandSize*islandNum;
			//randomly select the four candidates
			for(int i = 0; i < 4; i ++){
				candidates[i] = rIslandInd.nextInt(Parameters.islandSize) + 
									 			  startIndex;
			}
			//candidate with higher score gets selected
			if(randnum < kparam){
				//Select first candidate for final 
				if(Search.member[candidates[0]].rawFitness < Search.member[candidates[1]].rawFitness){
					finalCandidate1 = candidates[1];
				}
				else{
					finalCandidate1 = candidates[0];
				}
				
				//Select second candidate for final
				if(Search.member[candidates[2]].rawFitness < Search.member[candidates[3]].rawFitness){
					finalCandidate2 = candidates[3];
				}
				else{
					finalCandidate2 = candidates[2];
				}
			}
			//candidate with lower score gets selected
			else{
				//Select first candidate for final 
				if(Search.member[candidates[0]].rawFitness < Search.member[candidates[1]].rawFitness){
					finalCandidate1 = candidates[0];
				}
				else{
					finalCandidate1 = candidates[1];
				}
			
			
				//Select second candidate for final
				if(Search.member[candidates[2]].rawFitness < Search.member[candidates[3]].rawFitness){
					finalCandidate2 = candidates[2];
				}
				else{
					finalCandidate2 = candidates[3];
				}
				
			}
			
			//return candidate with best score
			if(Search.member[finalCandidate1].rawFitness < Search.member[finalCandidate2].rawFitness){
				return finalCandidate2;
			}
			else{
				return finalCandidate1;
			}
			

		default:
			System.out.println("ERROR - No selection method selected");
		}
		return (-1);
	}
	// Produce a new child from two parents **********************************
	public static void mateParents(int pnum1, int pnum2, Chromo parent1,
			Chromo parent2, Chromo child1, Chromo child2) {

		int xoverPoint1;
		int xoverPoint2;

		switch (Parameters.xoverType) {

		case 1: // Single Point Crossover

			// Select crossover point
			xoverPoint1 = 1 + (int) (Search.r.nextDouble() * (Parameters.numGenes
					* Parameters.geneSize - 1));

			// Create child chromosome from parental material
			child1.chromo = parent1.chromo.substring(0, xoverPoint1)
					+ parent2.chromo.substring(xoverPoint1);
			child2.chromo = parent2.chromo.substring(0, xoverPoint1)
					+ parent1.chromo.substring(xoverPoint1);
			break;

		case 2: // Two Point Crossover

			// select crossover points
			xoverPoint1 = 1 + (int) (Search.r.nextDouble() * (Parameters.numGenes
					* Parameters.geneSize - 1));
			
			do
				xoverPoint2 = 1 + (int) (Search.r.nextDouble() * (Parameters.numGenes
						* Parameters.geneSize - 1));
			while (xoverPoint2 < xoverPoint1);

			// Create child chromosome from parental material
			child1.chromo = parent1.chromo.substring(0, xoverPoint1)
					+ parent2.chromo.substring(xoverPoint1, xoverPoint2)
					+ parent1.chromo.substring(xoverPoint2);
			child2.chromo = parent2.chromo.substring(0, xoverPoint1)
					+ parent1.chromo.substring(xoverPoint1, xoverPoint2)
					+ parent2.chromo.substring(xoverPoint2);
			;
			break;

		case 3: // Uniform Crossover

			// Initialize the children
			child1.chromo = "";
			child2.chromo = "";

			// length of the chromo
			int length = parent1.chromo.length();

			for (int i = 0; i < length; i++) {
				// Create a random number with half of the possibilities to be
				// swap
				randnum = Search.r.nextDouble();

				if (randnum > 0.5) // we do a swap
				{
					child1.chromo += parent2.chromo.charAt(i);
					child2.chromo += parent1.chromo.charAt(i);
				} else // we keep the same
				{
					child1.chromo += parent1.chromo.charAt(i);
					child2.chromo += parent2.chromo.charAt(i);
				}
			}
			break;

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		// Set fitness values back to zero
		child1.rawFitness = -1; // Fitness not yet evaluated
		child1.sclFitness = -1; // Fitness not yet scaled
		child1.proFitness = -1; // Fitness not yet proportionalized
		child2.rawFitness = -1; // Fitness not yet evaluated
		child2.sclFitness = -1; // Fitness not yet scaled
		child2.proFitness = -1; // Fitness not yet proportionalized
	}

	// Produce a new child from a single parent ******************************

	public static void mateParents(int pnum, Chromo parent, Chromo child) {

		// Create child chromosome from parental material
		child.chromo = parent.chromo;

		// Set fitness values back to zero
		child.rawFitness = -1; // Fitness not yet evaluated
		child.sclFitness = -1; // Fitness not yet scaled
		child.proFitness = -1; // Fitness not yet proportionalized
	}

	// Copy one chromosome to another ***************************************

	public static void copyB2A(Chromo targetA, Chromo sourceB) {

		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

} // End of Chromo.java ******************************************************
