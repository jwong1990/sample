/*
Diego Velasquez
CAP 6135
Fuzzer.c
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char *argv[])
{
	char imageTxt[10000];
	char buffer[1000];
	int status, ret,retCode;
	int fSize, address, value;
	int i, fuzzNum=100;
	int bug_number=2;
	time_t t;

	srand((unsigned) time(&t));  /* randomize the initial seed */
	FILE  *fin, *fout;
	fin = fopen("./sample.jpg", "rb"); 
	fseek(fin, 0, SEEK_END);  
	fSize=ftell(fin);
	fseek(fin, 0, SEEK_SET);
	fread(imageTxt, 1, fSize, fin);
	fclose(fin);

	for(i=0; i<fuzzNum; i++)
	{			
		fout = fopen("./tempTest.jpg", "wb"); 
		//Select random addresses and values
		if(i%2 == 0)
			address = rand()%158 + 3; 
		else if(i%3==0)
			address = rand()%609 + 158; 
		else
			address = rand()%5153+609;

		//Depending in the bug some default values have to be executed
		if(bug_number==1 && i==10)//Bug #1
		{
			//Address = 10 and the value was = 105
			address = 10;
			value = 105;
		}

		if(bug_number==2 && i==10)//Bug #2
		{
			//Address = 13 and the value was = 87
			address = 13;
			value = 87;
		}

		if(bug_number==3 && i==10)//Bug #3
		{
			//Address = 14 and the value was = 164
			address = 14;
			value = 164;
		}

		if(bug_number==4 && i==10)//Bug #4
		{
			//Address = 19 and the value was = 517
			address = 19;
			value = 517;
		}

		if(bug_number==5 && i==10)//Bug #5
		{
			//Address = 21 and the value was = 110
			address = 21;
			value = 110;
		}

		if(bug_number==6 && i==10)//Bug #6
		{
			//Address = 24 and the value was = 176
			address = 24;
			value = 176;
		}

		if(bug_number==7 && i==10)//Bug #7
		{
			// Address = 613 and the value was = 211
			address = 613;
			value = 211;
		}

		if(bug_number==8 && i==10)//Bug #8
		{
			//Address = 165 and the value was = 48
			address = 165;
			value = 48;
		}

		value = rand()%255;
		//Sometimes I did use higher values
		if(i==50)
			value = rand()%300 + 255;

		imageTxt[address] = value;
		fwrite (imageTxt, 1, fSize, fout );
		fclose (fout);		
		sprintf(buffer, "./jpegconv -ppm -outfile foo11.ppm ./tempTest.jpg\n");
		ret = system(buffer);	
		wait(&status); /* wait for the fuzzTest-target program to finish */		
		retCode = WEXITSTATUS(ret);

		//Delete the text file used
		remove("./tempTest.jpg");
		if ( retCode > 128)
		{			
			//Print the image with the bug number and break.
			fout = fopen("./bug#.jpg", "wb"); 
			fwrite (imageTxt, 1, fSize, fout ); 
			fclose (fout);
			printf("Address = %d and the value was = %d\n",address,value);
			break;

		}		
	}
	return 0;
}
