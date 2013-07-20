import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Fuzzer {
	public static Random r = new Random();

	public enum Options {

		SAMPLEIMAGE, TESTNAME, NTRIES, ADVMODE, HELP, SEED, SAVEPIC, PPMNAME
	}

	public static void main(String args[]) throws IOException {
		boolean[] foundBugs = new boolean[11];
		int bugsFound = 0;
		int sizeOfFile = 0;
		// Default Values
		int numberOfTries = 5000;
		int seed = 75982;
		boolean advMode = false;
		boolean helpMode = false;
		boolean savePic  = false;
		String testName = "test.jpg";
		String ppmName = "foo.ppm";
		String sampleImage = "sample.jpg";

		for (int i = 0; i < args.length; i++) {
			Options currentOption = Options.valueOf("HELP");
			String current = args[i];
			try{
		    	currentOption = Options.valueOf(current.replace("-", "").toUpperCase());
			}catch (IllegalArgumentException ex) {
				System.out.println("\nWrong commands, please use any of the commands below");
			}
			
			switch (currentOption) {
			case SAMPLEIMAGE:
				sampleImage = args[++i];
				break;
			case TESTNAME:
				testName = args[++i];
				break;
			case PPMNAME:
				ppmName = args[++i];
				break;
			case NTRIES:
				numberOfTries = Integer.parseInt(args[++i]);
				break;
			case ADVMODE:
				advMode = true;
				break;
			case HELP:
				helpMode = true;
				break;
			case SEED:
				seed = Integer.parseInt(args[++i]);
				break;
			case SAVEPIC:
				savePic = true;
				break;
			}
		}
		if (helpMode) {
			printHelp();
			System.exit(1);
		}
		Calendar dateAndTime = Calendar.getInstance();
		Date startTime = dateAndTime.getTime();
		sizeOfFile = countBytes(sampleImage);
		r.setSeed(seed);
		// test reg expressions
		// String line = "BUG 10 TRIGGERED";
		// if (line.matches("BUG \\d+ TRIGGERED")) {
		// int res = Integer.parseInt(line.replaceAll("[^\\d]", ""));
		// System.out.print(res);
		// }

		// Extract baseFile
		int[] bytesReturn = readBaseFile("sample.jpg", sizeOfFile);
		int[] tempBytes = new int[sizeOfFile];
		copyArray(bytesReturn, tempBytes);
		// Start running the fuzzer
		int max = (advMode)? 100 : 700;
		int min = 1;
		int intervals = numberOfTries/7;
		for (int i = 1; i < numberOfTries; i++) {
			if ((i % intervals) == 0 && advMode) {
				min = max;
				max += 100;
				System.out.printf("New Max: %d, New Min: %d\n", max, min);

			}
			int randomAddress = r.nextInt(max - min + 1) + min;
			int randomValue = r.nextInt(255 - 1 + 1) + 1;

			// Write the random data
			tempBytes[randomAddress] = randomValue;

			// Create new test
			if (!createNewTest(tempBytes, testName)) {
				copyArray(bytesReturn, tempBytes);
				continue;
			}

			// Run the test
			int bugNumber = executeProgram(testName,ppmName);
			if (bugNumber > 0) {
				if (!foundBugs[bugNumber]) {
					// Save picture
					if(savePic)
					{
						String picName = "bug#" + bugNumber + ".jpg";
						createNewTest(tempBytes, picName);
					}
					foundBugs[bugNumber] = true;
					// reset array
					copyArray(bytesReturn, tempBytes);
					System.out.printf(
							"Run:%d found bug#%d Address: %d, Value: %d \n", i,
							bugNumber, randomAddress, randomValue);
					bugsFound++;
					continue;
				}

				// reset array
				copyArray(bytesReturn, tempBytes);
			}

			if (bugNumber < 0) {
				// reset array
				copyArray(bytesReturn, tempBytes);
			}
			// Delete file after each use
			File fileTemp = new File(testName);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}

			// Delete file after each use
			fileTemp = new File(ppmName);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}
			
			if (bugsFound == 10)// We are done						
				break;
			
			if ((i % 1000) == 0)
				System.out.println("Done with try " + i);
		}
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance();
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
		System.out.println("Number of bugs found: " + bugsFound);
	}

	public static void printHelp() {
		System.out.println("\nJava Fuzzer <arguments>");
		System.out.println("");
		System.out.println("Supported options are:");
		System.out.println("   -sample-image <image name>  Specify the name of the image to be tested.");
		System.out.println("   -test-name <test name>      Specify the name of a test that will be use as a temporary image.");
		System.out.println("   -ppm-name <test name>       Specify the name of the ppm output.");
		System.out.println("   -n-tries   <number>         Specify the number of iterations to run.");		
		System.out.println("   -seed      <number>         Specify specific seeder number.");
		System.out.println("   -adv-mode                   Specify if advance fuzzer wants to be use instead or standard fuzzer.");
		System.out.println("   -save-pic                   Specify if picture needs to be save.");
		System.out.println("");
		System.out.println("Default values are:");
		System.out.println("   -sample-image sample.jpg");
		System.out.println("   -test-name    test.jpg");
		System.out.println("   -ppm-name     foo.ppm");
		System.out.println("   -n-tries      5000");		
		System.out.println("   -seed         75982");
		System.out.println("   -adv-mode     false");
		System.out.println("   -save-pic     false\n");
		
	}

	public static int executeProgram(String imageName, String ppmName)
			throws NumberFormatException, IOException {

		int bugNumber = 0;

		// running a command in java
		String command = "./jpegconv -ppm -outfile "+ppmName+" " + imageName;
		Process child = null;
		try {
			child = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.err.println("Unable to execute the command: " + command);
			System.exit(1);
		}
		InputStream inputstream = child.getErrorStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		// Read the output of the program
		String line;
		boolean error = false;
		while ((line = bufferedreader.readLine()) != null) {
			error = true;
			if (line.matches("BUG \\d+ TRIGGERED")) {
				bugNumber = Integer.parseInt(line.replaceAll("[^\\d]", ""));
				break;
			}
		}
		if (error && bugNumber == 0)
			bugNumber = -1;

		return bugNumber;
	}
	
	/**
	 * Use the file and count the chars in the file, so we can use static arrays
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

	public static boolean createNewTest(int[] bytes, String picName) {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(picName);
			for (int k = 0; k < bytes.length; k++) {
				fout.write(bytes[k]);
			}
			fout.close();
		}
		// Catches any error conditions
		catch (IOException e) {
			//System.err.println("Unable to write image");
			return false;
		}
		return true;
	}

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

	public static void copyArray(int[] src, int[] tar) {
		for (int i = 0; i < src.length; i++)
			tar[i] = src[i];
	}
}