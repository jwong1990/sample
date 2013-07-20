import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FuzzerHelper {

	public static int[] bytesInTheFile;
	public static int sizeOfFile;
	public static String imageName = Parameters.imageName;
	public static String ppmName = Parameters.ppmName;
	public static String sampleName = Parameters.sampleName;
	public static Map<Integer, Integer> bugScore = new HashMap<Integer, Integer>();
	public static int[] countBugs = new int[11];
	public static ArrayList<Integer> valList = new ArrayList<Integer>();
	public static ArrayList<Integer> addList = new ArrayList<Integer>();
	public static HashMap<Pair, Boolean> res = new HashMap<Pair,Boolean>();
	public static boolean[] bugAll = new boolean[11];
	public static int allBugsCounter = 0;
	public static int evaluationsCounter = 0;
	public static HashMap<Integer, Integer> table = new HashMap<Integer,Integer>();
	public static int[] archive = new int[11];
	public static boolean arch = false;
	
	/**
	 * Will do a monte carlo simulation to check the probability base in the results
	 * @param vList
	 * @param aList
	 * @param res
	 * @return
	 */
	public static double monteCarloSimulation(ArrayList<Integer> vList, ArrayList<Integer> aList, HashMap<Pair, Boolean> res)
	{
		int nSuccess = 0;
		int nTest = 0;
		
		for(int i=0; i<vList.size(); i++)
		{
			nTest++;
			Pair temp = new Pair(aList.get(i),vList.get(i));
			
			if (res.get(temp))             
				nSuccess++;						
		}
		System.out.println("probability of success: " + (double)nSuccess/(double)nTest);
		return (double)nSuccess/(double)nTest;
	}

	/**
	 * Executed jpegconv and return the bug number triggered if any
	 * 
	 * @param imageName
	 * @param ppmName
	 * @return integer to indicate if a bug was triggered
	 * @throws NumberFormatException
	 * @throws IOException
	 */
//	public static int executeProgram(String imageName, String ppmName) {
//
//		int bugNumber = 0;
//
//		// running a command in java
//		String command = "./jpegconv -ppm -outfile " + ppmName + " "
//				+ imageName;
//		Process child = null;
//		try {
//			child = Runtime.getRuntime().exec(command);
//			child.waitFor();
//		} catch (IOException e) {
//			System.err.println("Unable to execute the command: " + command);
//			return -2;
//		} catch (InterruptedException e) {
//			System.err.println("Unable to wait for the command: " + command);
//		}
//
//		// Extract the output of the program
//		InputStream inputstream = child.getErrorStream();
//		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
//		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
//
//		// Read the output of the program
//		String line;
//		boolean error = false;
//		try {
//			while ((line = bufferedreader.readLine()) != null) {
//				error = true;
//				// Reg expression to check if any bug was triggered
//				if (line.matches("BUG \\d+ TRIGGERED")) {
//					bugNumber = Integer.parseInt(line.replaceAll("[^\\d]", ""));
//					break;
//				}
//			}
//			inputstream.close();
//			inputstreamreader.close();
//			bufferedreader.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		// Return -1 if another bug was triggered
//		if (error && bugNumber == 0)
//			bugNumber = -1;
//		child.destroy();
//		return bugNumber;
//	}

	/**
	 * creates a new jpg picture base in the bytes pass
	 * 
	 * @param bytes
	 * @param picName
	 * @return false if the case can not be created
	 */
//	public static boolean createNewTest(int[] bytes, String picName) {
//		FileOutputStream fout;
//		try {
//			fout = new FileOutputStream(picName);
//			for (int k = 0; k < bytes.length; k++) {
//				fout.write(bytes[k]);
//			}
//			fout.close();
//		}
//		// Catches any error conditions
//		catch (IOException e) {
//			System.err.println("Unable to write image");
//			return false;
//		}
//		return true;
//	}

	/**
	 * Read the original picture to an array
	 * 
	 * @param initialFile
	 * @param size
	 * @return an array with the address and values
	 */
	public static int[] readBaseFile(String initialFile, int size) {
		int[] imageTxt = new int[size];
		FileInputStream fin;
		try {
			// Open an input stream
			fin = new FileInputStream(initialFile);
			int k = 0;
			while (fin.available() != 0) {
				imageTxt[k++] = fin.read();
			}
			fin.close();
		}
		// Catches any error conditions
		catch (IOException e) {
			System.err.println("Unable to read image");
			System.exit(1);
		}
		return imageTxt;
	}

	/**
	 * Copy an array to another array
	 * 
	 * @param src
	 * @param tar
	 */
//	public static void copyArray(int[] src, int[] tar) {
//		for (int i = 0; i < src.length; i++)
//			tar[i] = src[i];
//	}

	/**
	 * Use the file and count the chars in the file, so we can use static arrays
	 * 
	 * @param fileName
	 * @return integer with the size of the file
	 */
	public static int countBytes(String fileName) {
		FileInputStream fin;
		int charCount = 0;
		try {
			// Open an input stream
			fin = new FileInputStream(fileName);
			while (fin.available() != 0) {
				fin.read();
				charCount++;
			}
			fin.close();
		}
		// Catches any error conditions
		catch (IOException e) {
			System.err.println("Unable to read image");
			System.exit(1);
		}
		return charCount;
	}

	/**
	 * Will read the initial file
	 */
	public static void readFile() {
		sizeOfFile = 0;
		sizeOfFile = countBytes(imageName);
		bytesInTheFile = readBaseFile(imageName, sizeOfFile);
	}

	/**
	 * Will iterate through an array will get min
	 * 
	 * @param arr
	 * @return min from the arr
	 */
	public static int getMin(int[] arr) {
		int min = Integer.MAX_VALUE;
		for (int i = 1; i < arr.length; i++)
			if (arr[i] < min)
				min = arr[i];
		return min;
	}

	/**
	 * will 
	 * @param generation
	 */
	public static void printFrequencies(int generation) {
		int minBugs = getMin(FuzzerHelper.countBugs);
		String[] mins = new String[11];
		int k = 0;
		System.out.printf("\nIn generation %d, below is the frequency\n",generation);
		
		//Will choose the min frequencies
		for (int i = 1; i < 11; i++) {
			System.out.printf("Bug #%d frequency:%d\n", i,
					FuzzerHelper.countBugs[i]);

			if (FuzzerHelper.countBugs[i] <= minBugs) {
				minBugs = FuzzerHelper.countBugs[i];
				mins[k++] = new Integer(i).toString();
			}
			FuzzerHelper.countBugs[i] = 0;
		}
		
		//Choose randomly for one of the mins and increase the fitness
		int ranBug = Search.r.nextInt(k);
		minBugs = Integer.parseInt(mins[ranBug]);
		//int currentValue = bugScore.get(minBugs);
		//currentValue += 2;
		//FuzzerHelper.bugScore.put(minBugs, currentValue);
		//System.out.printf("\n");
		//FuzzerHelper.printScore();
		System.out.printf("\n");
	}

	/**
	 * Initialize to initial values for each bug#
	 */
	public static void resetScore() {
		for (int i = 1; i < 11; i++)
			bugScore.put(i, 1);
	}

	/**
	 * Prints the scores of the hash
	 */
	public static void printScore() {
		System.out.println("Score so far:");
		Iterator<Entry<Integer, Integer>> it = bugScore.entrySet().iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

}
