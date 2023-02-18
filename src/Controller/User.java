package Controller;

import Model.AES;

public class User {

	public static void main(String[] args) {
		AES imp = new AES();
		long start = System.nanoTime();
		imp.ftn_main();
		long end = System.nanoTime();
	    long execution = end - start;
	    System.out.println("Execution time: " + execution + " nanoseconds");
	}

}