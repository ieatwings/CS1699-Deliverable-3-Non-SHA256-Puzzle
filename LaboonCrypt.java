// CS 1699 - Project #3
// Brandon La - bnl22
// 11/15/2018

import java.lang.Math;
import java.util.*;
import java.io.*;

public class LaboonCrypt {

	// Declare static variables
	public static String INIT_VECTOR = "1AB0";
	public static final int BLOCK_SIZE = 8;

	public static void main(String[] args) {
		// Initialize flags for verbosity levels
		boolean verbose = false;
		boolean veryverbose = false;
		boolean ultraverbose = false;
		boolean chrisIsDeadToMe = true;

		if(args.length == 2) {
			if(args[1].equals("-verbose")) {
				verbose = true;
			}
			else if(args[1].equals("-veryverbose")) {
				verbose = true;
				veryverbose = true;
			}
			else if(args[1].equals("-ultraverbose")) {
				verbose = true;
				veryverbose = true;
				ultraverbose = true;
			}
			else {
				System.out.println("Java LaboonCrypt *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose -veryverbose -ultraverbose");
				System.exit(1);
			}
		}
		else if(args.length == 1) {
			verbose = false;
		}
		else {
			System.out.println("Java LaboonCrypt *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose -veryverbose -ultraverbose");
			System.exit(1);
		}
		String input = args[0];
		LaboonCrypt(input, verbose, veryverbose, ultraverbose);
	}

	// Method to split string into blocks of size 8
	public static String[] splitBlocks(String s) {
		return s.split("(?<=\\G.{" + BLOCK_SIZE + "})");
	}

	// Pulled from LaboonHash.java to Merkel-Damgard Strengthen the last block
	public static String strengthenIfNecessary(String stringBlocks, int origLength) {
		int needed = BLOCK_SIZE - stringBlocks.length();
		int pad = (int) Math.pow(16, needed);
		String strengthen = Integer.toHexString(origLength % pad);

		if(strengthen.length() > 1) {
			int uneeded = strengthen.length() - 1;
			needed = needed - uneeded;
		}

		for(int i = 0; i < needed - 1; i++) {
			stringBlocks = stringBlocks + "0";
		}
		stringBlocks = stringBlocks + needed;
		return stringBlocks;
	}

	// Function to hash lhs of 4 chars and rhs of 8 chars into result[]
	public static String LaboonHash(String lhs, String rhs) {
		char[] result = new char[4];

		// Phase #1
		result[0] = (char)(lhs.charAt(0) + rhs.charAt(3));
		result[1] = (char)(lhs.charAt(1) + rhs.charAt(2));
		result[2] = (char)(lhs.charAt(2) + rhs.charAt(1));
		result[3] = (char)(lhs.charAt(3) + rhs.charAt(0));

		// Phase #2
		result[0] = (char)(result[0] ^ rhs.charAt(7));
		result[1] = (char)(result[1] ^ rhs.charAt(6));
		result[2] = (char)(result[2] ^ rhs.charAt(5));
		result[3] = (char)(result[3] ^ rhs.charAt(4));

		// Phase #3
		result[0] = (char)(result[0] ^ result[3]);
		result[1] = (char)(result[1] ^ result[2]);
		result[2] = (char)(result[2] ^ result[1]);
		result[3] = (char)(result[3] ^ result[0]);

		// Output
		String output = Integer.toHexString(result[0] % 16);
		output += Integer.toHexString(result[1] % 16);
		output += Integer.toHexString(result[2] % 16);
		output += Integer.toHexString(result[3] % 16);
		output = output.toUpperCase();
		return output;
	}

	// Helper function to print information when -ultraverbose
	public static void printInit(String padded, String INIT_VECTOR, String finalHash) {
		System.out.println("Padded string: " + padded);
		System.out.println("Blocks: \n" + padded);
		System.out.println("Iterating with " + INIT_VECTOR + " / " + padded + " = " + finalHash);
		System.out.println("Final result: " + finalHash);
	}

	public static void LaboonCrypt(String input, boolean verbose, boolean veryverbose, boolean ultraverbose) {

		String oldVector = INIT_VECTOR;
		String finalHash;
		String[] blockHash;

		// Make sure the hashed blocks are compatible with size 8 blocks
		if(input.length() % 8 == 0) {
			blockHash = new String[(input.length() / 8)];
		} 
		else {
			blockHash = new String[(input.length() / 8) + 1];
		}
		// TEST //
		//System.out.println(stringToHash);
		String stringToHash = input;
		int stringToHashLength = stringToHash.length();

		// Split the string into blocks of size 8
		String[] splitBlocks = splitBlocks(stringToHash);
		splitBlocks[splitBlocks.length - 1] = strengthenIfNecessary(splitBlocks[splitBlocks.length - 1], splitBlocks.length);

		// If ultraverbose, display padded string and blocks
		if(ultraverbose) {
			System.out.print("Padded string: ");

			for(int i = 0; i < splitBlocks.length; i++) {
				System.out.print(splitBlocks[i]);
			}

			System.out.println();
			System.out.println("Blocks:");

			for(int i = 0; i < splitBlocks.length; i++) {
				System.out.println(splitBlocks[i]);
			}
		}

		// Hash each block of string
		for(int i = 0; i < splitBlocks.length; i++) {
			blockHash[i] = LaboonHash(oldVector, splitBlocks[i]);
			oldVector = blockHash[i];
		}
		finalHash = oldVector;

		if(ultraverbose) {
			System.out.println("Iterating with " + INIT_VECTOR + " / " + splitBlocks[splitBlocks.length - 1] + " = " + finalHash);
		}

		// Initialize 2-D matrix for storage of hashes
		String[][] thiccArray = new String[12][12];
		thiccArray[0][0] = finalHash;

		// Initialize padded string
		String padded = "";

		// Beging hashing blocks and building matrix
		for(int i = 0; i < 12; i++) {
			for(int j = 0; j < 12; j++) {
				if(i == 0 && j == 0) {
					continue;
				}
				finalHash = strengthenIfNecessary(finalHash, finalHash.length());
				padded = finalHash;
				finalHash = LaboonHash(INIT_VECTOR, finalHash);

				if(ultraverbose) {
					printInit(padded, INIT_VECTOR, finalHash);
				}
				thiccArray[i][j] = finalHash;
			}
		}
		// Strengthen finalHash if needed
		finalHash = strengthenIfNecessary(finalHash, finalHash.length());
		padded = finalHash;
		// Hash the final string with padding
		finalHash = LaboonHash(INIT_VECTOR, finalHash);

		if(ultraverbose) {
			printInit(padded, INIT_VECTOR, finalHash);
		}

		// Print matrix of hashes
		if(verbose) {
			//verbose(splitBlocks, blockHash, oldVector);
			System.out.println("Initial array: ");

			for(int i = 0; i < 12; i++) {
				if(i != 0) {
					System.out.println();
				}
				for(int j = 0; j < 12; j++) {
					System.out.print(thiccArray[i][j] + " ");
				}
			}
		}

		// Spacer
		System.out.println();

		// Initialize cursor
		int cursorX = 0;
		int cursorY = 0;

		// Display the movement of the 'cursor' in the matrix
		for(int i = 0; i < stringToHashLength; i++) {
			if(veryverbose) {
				System.out.print("Moving Down " + (stringToHash.charAt(i) * 11) + " down ");
			}

			cursorX = cursorX + (stringToHash.charAt(i) * 11);

			if(veryverbose) {
				System.out.print("and " + ((stringToHash.charAt(i) + 3) * 7) + " right");
			}

			cursorY = cursorY + ((stringToHash.charAt(i) + 3) * 7);
			cursorX = cursorX % 12;
			cursorY = cursorY % 12;

			if(veryverbose) {
				System.out.print(" - modifying [" + cursorX + ", " + cursorY + "] from " + thiccArray[cursorX][cursorY] + " to ");
			}

			if(ultraverbose) {
				finalHash = strengthenIfNecessary(thiccArray[cursorX][cursorY], thiccArray[cursorX][cursorY].length());
				padded = finalHash;
				finalHash = LaboonHash(INIT_VECTOR, finalHash);
				printInit(padded, INIT_VECTOR, finalHash);
			}

			// Strengthen a temp string if needed
			String tempString = strengthenIfNecessary(thiccArray[cursorX][cursorY], thiccArray[cursorX][cursorY].length());

			// Hash the temp string
			tempString = LaboonHash(INIT_VECTOR, tempString);

			if(veryverbose) {
				System.out.println(tempString);
			}

			// Tempstring now becomes the hashed  matrix
			thiccArray[cursorX][cursorY] = tempString;
		}

		if(verbose) {
			System.out.println("Final array:");

			for(int i = 0; i < 12; i++) {
				if(i != 0) {
					System.out.println();
				}
				for(int j = 0; j < 12; j++) {
					System.out.print(thiccArray[i][j] + " ");
				}
			}
		}

		// Initialize concatenation string
		String allTogether = new String();

		for(int i = 0; i < 12; i++) {
			for(int j = 0; j < 12; j++) {
				allTogether += thiccArray[i][j];
			}
		}

		// If ultraverbose, continue to print statements
		if(ultraverbose) {
			System.out.println();
			System.out.println("Padded string: " + allTogether);
			System.out.println("Blocks:");

			String[] tempString = splitBlocks(allTogether);

			for(int i = 0; i < tempString.length; i++) {
				System.out.println(tempString[i]);
			}

			oldVector = INIT_VECTOR;

			for(int i = 0; i < tempString.length; i++) {
				finalHash = strengthenIfNecessary(tempString[i], stringToHashLength);
				padded = finalHash;
				finalHash = LaboonHash(oldVector, finalHash);
				System.out.println("Iterating with " + oldVector + " / " + padded + " = " + finalHash);
				oldVector = finalHash;
			}
			System.out.println("Final result: " + finalHash);
		}

		// Initialize the final string to hash
		String[] finalStringToHash;

		if(allTogether.length() % 8 == 0) {
			finalStringToHash = new String[(allTogether.length() / 8)];
		}
		else {
			finalStringToHash = new String[(allTogether.length() / 8) + 1];
		}

		String [] finalSplitBlocks = splitBlocks(allTogether);

		//finalSplitBlocks[finalSplitBlocks.length - 1] = strengthenIfNecessary(finalSplitBlocks[finalSplitBlocks.length - 1], allTogether.length());

		// Reset oldVector variable for final block hashing
		oldVector = INIT_VECTOR;

		for(int i = 0; i < finalSplitBlocks.length; i++) {
			finalSplitBlocks[i] = LaboonHash(oldVector, finalSplitBlocks[i]);
			oldVector = finalSplitBlocks[i];
		}

		// Spacer
		if(verbose) {
			System.out.println();
		}

		// Print final result
		System.out.println("LaboonCrypt hash: " + oldVector);
	}
}