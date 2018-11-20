// CS 1699 - Project #3
// Brandon La - bnl22
// 11/15/2018
import java.lang.Math;
import java.util.*;
import java.io.*;

public class LaboonHash {

	public static String INIT_VECTOR = "1AB0";
	public static final int BLOCK_SIZE = 8;

	public static void main(String[] args) {

		boolean verbose = false;
		String oldVector = INIT_VECTOR;

		if(args.length == 2) {
			if(args[1].equals("-verbose")) {
				verbose = true;
			}
			else {
				System.out.println("Java LaboonHash *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose");
				System.exit(1);
			}
		} else if(args.length == 1) {
			verbose = false;
		}
		else {
			System.out.println("Java LaboonHash *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose");
			System.exit(1);
		}

		String stringToHash = args[0];
		String[] blockHash;
		if(args[0].length() % 8 == 0) {
			blockHash = new String[(args[0].length() / 8)];
		} 
		else {
			blockHash = new String[(args[0].length() / 8) + 1];
		}
		int stringToHashLength = stringToHash.length();
		//String verbosityLevel = args[1];

		// TEST //
		//System.out.println(stringToHash);

		String[] splitBlocks = splitBlocks(stringToHash);
		splitBlocks = strengthenIfNecessary(stringToHashLength, splitBlocks);

		if(verbose) {
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

		for(int i = 0; i < splitBlocks.length; i++) {
			blockHash[i] = compress(oldVector, splitBlocks[i], verbose);
			if(verbose) {
				System.out.println("Iterating with " + oldVector + " / " + splitBlocks[i] + " = " + blockHash[i]);
			}
			oldVector = blockHash[i];
		}
		if(verbose) {
			System.out.println("Final result: " + blockHash[blockHash.length - 1]);
		}
		System.out.println("LaboonHash hash = " + blockHash[blockHash.length - 1]);

		// TEST //
		// for(int i = 0; i < splitBlocks.length; i++) {
		// 	System.out.println(splitBlocks[i]);
		// }

		//System.out.println(splitBlocks);

		// for(int i = 0; i < splitBlocks.length; i++) {
		// 	System.out.println(splitBlocks[i]);
		// }


	}

	public static String compress(String lhs, String rhs, boolean verbose) {
		char[] result = new char[4];

		// if(verbose) {
		// System.out.println("Iterating with " + lhs + " / " + rhs);
		// }

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

	public static String[] splitBlocks(String s) {
		return s.split("(?<=\\G.{" + BLOCK_SIZE + "})");
	}

	public static String pad(String s, int len) {
		int sizeToPad = BLOCK_SIZE - s.length();
		int modValue = (int) Math.pow(10, sizeToPad);
		int moddedLen = len % modValue;
		String hex = Integer.toHexString(moddedLen);
		String padded = "";
		for(int i = 0; i < (sizeToPad - hex.length()); i++) {
			padded = padded + "0";
		}
		padded = padded + hex;
		return padded;
	}

	public static String[] strengthenIfNecessary(int origLength, String[] stringBlocks) {
		String finalBlock = stringBlocks[stringBlocks.length - 1];
		int finalBlockLength = finalBlock.length();
		if(finalBlockLength < BLOCK_SIZE) {
			String paddedBlock = finalBlock + pad(finalBlock, origLength);
			stringBlocks[stringBlocks.length - 1] = paddedBlock;
		}
		return stringBlocks;
	}


}