import java.util.HashMap;
import java.util.Map;


public class MockJpegConv implements TestSoftware {	

	private Map<Pair, Integer> JPEGCONV;	
	private int bugNumber;
	private Pair testCase;
	boolean isInit;
	private boolean anyTest;
	private int bug3;
	private int bug4;

	
	
	public MockJpegConv()
	{
		createTable();
		bug3 = 0;
		bug4 = 0;		
	}
	
	@Override
	public boolean run() {
		if(!this.anyTest)
			return false;
		int ans = executeProgram();		
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
		bug3 = 0;
		bug4 = 0;
		return true;
	}
	
	@Override
	public boolean createTestCase(int address, int value) {
		if(!this.isInit)
			return false;
		testCase = new Pair(address,value);
		this.anyTest = true;
		return true;
	}
	@Override
	public boolean deleteTest() {		
		if(!this.anyTest)
			return false;
		bug3 = 0;
		bug4 = 0;
		this.anyTest = false;
		return true;
	}
	private int executeProgram(){
		int bugNumber = 0;
		if(JPEGCONV.containsKey(testCase))
		{
			bugNumber = JPEGCONV.get(testCase);
			if(bugNumber == 3 || bugNumber == 4)
			{
				if(bugNumber == 3)
				{
					if(bug3 == 0)
					{
						bug3 = testCase.getAddress();
						bugNumber = 0;
					}
					else if(bug3 != testCase.getAddress())
						bugNumber = 3;//Found								
						
				}
				
				if(bugNumber == 4)
				{
					if(bug4 == 0)
					{
						bug4 = testCase.getAddress();
						bugNumber = 0;
					}
					else if(bug4 != testCase.getAddress())
						bugNumber = 4;//Found									
				}
			}
		}
		else//not bug triggered
			bugNumber = -1;
		return bugNumber;
	}
	private void createTable() {
		
		JPEGCONV = new HashMap<Pair, Integer>();
		
		//Bug #1 10>0<255
		for (int i = 1; i <= 255; i++)
		{
			Pair temp = new Pair(10,i);
			JPEGCONV.put(temp, 1);
		}
		
		//Bug #2 13>3<255 by 4's 3,7,11,15
		for (int i = 3; i <= 255; i+=4)
		{
			Pair temp = new Pair(13,i);
			JPEGCONV.put(temp, 2);
		}
		
		//Bug #3 address 14>=239 and address 16>=137
		for (int i = 239; i <= 255; i++)
		{
			Pair temp = new Pair(14,i);		
			JPEGCONV.put(temp, 3);
		}
		for (int i = 137; i <= 255; i++)
		{
			Pair temp = new Pair(16,i);		
			JPEGCONV.put(temp, 3);
		}
						
		//Bug #4 address 18>128 and address 19<128
		for (int i = 0; i < 128; i++)
		{
			Pair temp = new Pair(19,i);		
			JPEGCONV.put(temp, 4);
		}
		for (int i = 129; i <= 255; i++)
		{
			Pair temp = new Pair(18,i);
			JPEGCONV.put(temp, 4);
		}
		
		//Bug #5 any value with 3,21,90,159,178,211,394,427,610
		for (int i = 0; i < 255; i++)
		{
			Pair temp = new Pair(3,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(21,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(90,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(159,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(178,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(211,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(394,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(427,i);
			JPEGCONV.put(temp, 5);
			temp = new Pair(610,i);
			JPEGCONV.put(temp, 5);
		}
		
		for (int i = 623; i <= 1000; i++)
		{
			Pair temp = new Pair(i,255);
			JPEGCONV.put(temp, 5);
		}
		
		//Bug #6 32,33,34,35,48,49,50,51,64,65
		int k=0;
		for (int i = 32; i < 255; i++)
		{
			Pair temp = new Pair(93,i);
			JPEGCONV.put(temp, 6);
			k++;
			if(k==4)
			{
				i+=13;
				k=0;
				i--;
			}
		}
		
		//Bug #7 613 v>128<255
		for (int i = 128; i <= 255; i++)
		{
			Pair temp = new Pair(613,i);
			JPEGCONV.put(temp, 7);
		}
		
		//Bug #8 165 with v>0<255
		for (int i = 0; i <= 255; i++)
		{
			Pair temp = new Pair(165,i);
			JPEGCONV.put(temp, 8);
		}
		
		//Bug #9 169,172,175,with 1-16,32,48,64
		for (int i = 0; i < 255; i++)
		{
			Pair temp = new Pair(169,i);
			JPEGCONV.put(temp, 9);
			temp = new Pair(172,i);
			JPEGCONV.put(temp, 7);
			temp = new Pair(175,i);
			JPEGCONV.put(temp, 7);
			if(i>=16)
			{
				i+=16;
				i--;
			}
		}
		
		//Bug #10 23,92,180,213,396,429,612 with v==1	
		Pair temp = new Pair(23,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(92,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(180,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(213,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(396,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(429,1);
		JPEGCONV.put(temp, 10);
		temp = new Pair(612,1);
		JPEGCONV.put(temp, 10);

	}

}
