import java.io.FileWriter;
import java.util.Map;


public class FuzzerGA extends FitnessFunction {
	
	/*******************************************************************************
	*                            INSTANCE VARIABLES                                *
	*******************************************************************************/


	/*******************************************************************************
	*                            STATIC VARIABLES                                  *
	*******************************************************************************/


	/*******************************************************************************
	*                              CONSTRUCTORS                                    *
	*******************************************************************************/
	
	public FuzzerGA () {
		name = "Fuzzer with Genetic Algorithm";
	}
	/*******************************************************************************
	*                                MEMBER METHODS                                *
	*******************************************************************************/



		/* (non-Javadoc)
		 * @see FitnessFunction#doRawFitness(Chromo)
		 */
		@Override
		public void doRawFitness(Chromo X){			
			
			X.rawFitness = 0;
			//number of combinations to run to the jpegconv
			int chromoSize = Parameters.geneSize;
			
			//Variables use to run different vars
			//int [] tempBytes = new int[FuzzerHelper.sizeOfFile];
			int value = 0;
			int address = 0;		
			int bugNumber=0;
			int i = 0;			
			boolean[] foundBugs = new boolean[Parameters.numBugs+1]; //Stores found bug numbers
			//TestSoftware tarPro = new JpegConv(FuzzerHelper.imageName,FuzzerHelper.ppmName,FuzzerHelper.sampleName, FuzzerHelper.bytesInTheFile, FuzzerHelper.sizeOfFile);
			TestSoftware tarPro = new MockJpegConv();
			tarPro.initializeTest();
			boolean isIntrons = false;
			int n=0;
			while(i<chromoSize)
			{					

				if(Parameters.introns && (i-n)%18 == 0 && isIntrons)
				{
					i+=Parameters.numIntrons;
					n+=Parameters.numIntrons;
					isIntrons = false;
					continue;
				}
				if(FuzzerHelper.allBugsCounter != 8)
					FuzzerHelper.evaluationsCounter++;
				
				isIntrons = true;
				//Use this line to triggered only 8 of the 10 bugs
				//tarPro.initializeTest();
				address = getNumber(X.chromo, i,i+10);
				value = getNumber(X.chromo, i+10,i+18);
				
				Pair temp = new Pair(address,value);
				FuzzerHelper.valList.add(value);
				FuzzerHelper.addList.add(address);
				
				//System.out.println(temp.toString());
				
				if(address>700){
					FuzzerHelper.res.put(temp, false);
					i += 18;
					continue;
				}				
				tarPro.createTestCase(address, value);								
				if(!tarPro.run())
				{
					FuzzerHelper.res.put(temp, false);
					i+=18;
					tarPro.initializeTest();
					continue;
				}
				
				bugNumber = tarPro.getBug();				
				if (bugNumber > 0) {										
					//count only unique bugs
					//If the bug hasn't been found before, then add one
					//Otherwise, continue
					if(!foundBugs[bugNumber])
					{
						X.rawFitness++;
						FuzzerHelper.countBugs[bugNumber]++;
					}
					addArchive(bugNumber);
					foundBugs[bugNumber] = true;
					tarPro.initializeTest();					
					FuzzerHelper.res.put(temp, true);
					// This part is used to check when all the bugs are triggere
					// for any individual
					if(!FuzzerHelper.bugAll[bugNumber])
					{			
						FuzzerHelper.bugAll[bugNumber] = true;
						FuzzerHelper.allBugsCounter++;		
						FuzzerHelper.table.put(FuzzerHelper.allBugsCounter, FuzzerHelper.evaluationsCounter);
						
						if(FuzzerHelper.allBugsCounter == 10)
						{
							for (Map.Entry<Integer, Integer> entry : FuzzerHelper.table.entrySet())
								System.out.println("Bugs: " + entry.getKey() + " Evaluation: " +entry.getValue());
							System.out.println("probability of success: " + FuzzerHelper.monteCarloSimulation(FuzzerHelper.valList,FuzzerHelper.addList,FuzzerHelper.res));
						}
								
					}
				}	
				else
					FuzzerHelper.res.put(temp, false);
				if(bugNumber < 0)
					tarPro.initializeTest();
				//tarPro.deleteTest();				
				i += 18; //Next solution in the chromosome string
			}
			
		}


		/* (non-Javadoc)
		 * @see FitnessFunction#doPrintGenes(Chromo, java.io.FileWriter)
		 */
		@Override
		public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
			int i=0,j=1;
			if(Parameters.introns)
				return;
			System.out.printf("\n");
			//Select what implementation you want to use
			//TestSoftware tarPro = new JpegConv(FuzzerHelper.imageName,FuzzerHelper.ppmName,FuzzerHelper.sampleName, FuzzerHelper.bytesInTheFile, FuzzerHelper.sizeOfFile);
			TestSoftware tarPro = new MockJpegConv();
			while(i<Parameters.geneSize)
			{				
				int address = getNumber(X.chromo, i,i+10);
				int value = getNumber(X.chromo, i+10,i+18);
				int bugNumber = 0;
				System.out.printf("Solution %d : address %d, and value %d ",j++,address,value);
				tarPro.initializeTest();								
				//Get value and address
				address = getNumber(X.chromo, i,i+10);
				value = getNumber(X.chromo, i+10,i+18);
				
				if(address>700){
					System.out.printf("Doesn't trigger any bug\n");
					i += 18;
					continue;
				}
				
				tarPro.createTestCase(address, value);				
				
				if(!tarPro.run())
				{
					i+=18;
					continue;
				}
				
				bugNumber = tarPro.getBug();			
				if(bugNumber <= 0)
					System.out.printf("Doesn't trigger any bug\n");
				else
					System.out.printf("Trigger bug #%d\n",bugNumber);
				
				tarPro.deleteTest();
				
				i+=18;
			}
		}
		

		/**
		 * Will return the value in decimal that was represented in the chromosome in binary
		 * @param chromosome
		 * @param start
		 * @param end
		 * @return
		 */
		public int getNumber(String chromosome, int start,int end) {		
			//Get substring (Either value or address)
			String binNumber = chromosome.substring(start,end);
			return Integer.parseInt(binNumber, 2);
		}
		public void addArchive(int bugNum)
		{
			FuzzerHelper.archive[bugNum] = 1;
		}
		public static int totalBugs()
		{
			int count=0;
			for(int i=0;i<FuzzerHelper.archive.length;i++)
			{
				if(FuzzerHelper.archive[i] == 1)
				{
					count++;
				}
			}
			return count;
		}
		public static void printArchive()
		{
			for(int i=1;i<FuzzerHelper.archive.length;i++)
			{
				System.out.print(FuzzerHelper.archive[i] + " ");
			}
			System.out.println();
		}
		/*******************************************************************************
		*                             STATIC METHODS                                   *
		*******************************************************************************/
}
