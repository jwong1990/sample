import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class JpegConv implements TestSoftware {
			
	String imageName;
	String ppmName;
	String sampleName;
	int[] bytesInTheFile;
	int[] testBytes;
	int sizeOfFile;
	int bugNumber;
	boolean isInit;
	boolean anyTest;
	
	public JpegConv(String imageName, String ppmName,String sampleName, int[] bytesInTheFile, int sizeOfFile) {		
		this.imageName = imageName;
		this.ppmName = ppmName;
		this.sampleName = sampleName;
		this.bytesInTheFile = bytesInTheFile;
		this.sizeOfFile = sizeOfFile;
		testBytes = new int[sizeOfFile];
		isInit = false;
		anyTest = false;
	}
	
	@Override
	public boolean run() {
		if(!this.anyTest)
			return false;
		int ans = executeProgram();		
		if(ans == -2)
			return false;
		this.bugNumber = ans;
		return true;
	}

	@Override
	public int getBug() {
		return this.bugNumber;		
	}

	@Override
	public boolean initializeTest() {
		this.isInit = true;
		this.anyTest = false;
		copyArray(this.bytesInTheFile, this.testBytes);
		if(this.imageName == null || this.sampleName == null || this.ppmName == null || this.sizeOfFile == 0)
			return false;
		return true;
	}
	
	@Override
	public boolean createTestCase(int address, int value){
		if(!this.isInit)
			return false;
		this.testBytes[address] = value;
		if (!createNewTest()){
			return false;
		}
		this.anyTest = true;
		return true;
	}
	
	@Override
	public boolean deleteTest()
	{	
		if(!this.anyTest)
			return false;
		copyArray(this.bytesInTheFile, this.testBytes);
		this.anyTest = false;
		File fileTemp = new File(this.sampleName);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}

		fileTemp = new File(this.ppmName);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}		
		this.anyTest = false;
		return true;
	}
	
	/**
	 * creates a new jpg picture base in the bytes pass
	 * 
	 * @param bytes
	 * @param picName
	 * @return false if the case can not be created
	 */
	private boolean createNewTest() {
		FileOutputStream fout;		
		try {
			fout = new FileOutputStream(this.sampleName);
			for (int k = 0; k < this.sizeOfFile; k++) {
				fout.write(this.testBytes[k]);
			}
			fout.close();
		}
		// Catches any error conditions
		catch (IOException e) {
			System.err.println("Unable to write image");
			return false;
		}
		return true;
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
	private int executeProgram() {

		int bugNumber = 0;

		// running a command in java
		String command = "./jpegconv -ppm -outfile " + this.ppmName + " "
				+ this.sampleName;
		Process child = null;
		try {
			child = Runtime.getRuntime().exec(command);
			child.waitFor();
		} catch (IOException e) {
			System.err.println("Unable to execute the command: " + command);
			return -2;
		} catch (InterruptedException e) {
			System.err.println("Unable to wait for the command: " + command);
			return -2;
		}

		// Extract the output of the program
		InputStream inputstream = child.getErrorStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		// Read the output of the program
		String line;
		boolean error = false;
		try {
			while ((line = bufferedreader.readLine()) != null) {
				error = true;
				// Reg expression to check if any bug was triggered
				if (line.matches("BUG \\d+ TRIGGERED")) {
					bugNumber = Integer.parseInt(line.replaceAll("[^\\d]", ""));
					break;
				}
			}
			inputstream.close();
			inputstreamreader.close();
			bufferedreader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Return -1 if another bug was triggered
		if (error && bugNumber == 0)
			bugNumber = -1;
		child.destroy();
		return bugNumber;
	}
	
	/**
	 * Copy an array to another array
	 * 
	 * @param src
	 * @param tar
	 */
	private void copyArray(int[] src, int[] tar) {
		for (int i = 0; i < src.length; i++)
			tar[i] = src[i];
	}

}
