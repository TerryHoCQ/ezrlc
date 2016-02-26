/**
 * 
 */
package pro2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author noah
 *
 */
public class RFData {

	/**
	 * 
	 */
	private String fname = "";

	private long dataEntrys = 0;
	private long commentEntrys = 0;
	private long instructionEntrys = 0;
	private int freqMultiplier = 1;
	
	
	public RFData(String fname) {
		// TODO Auto-generated constructor stub
		this.fname = fname;
		System.out.println("Filename " +this.fname);
		
	}
	
	public void parse () throws IOException {
		FileReader file;
		String line;
		int lineno = 0;
		
		// Try opening the input file
		try {
			file = new FileReader(this.fname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Input File not found!");
			return;
		}
		
		// Add file to a buffer to read line by line
		BufferedReader br = new BufferedReader(file);
		lineno = 1;
	    while ((line = br.readLine()) != null) {
	    	if(line.length() != 0) {
		    	// check if first char is '!', which means a comment
		    	if(line.charAt(0) == '!') {
		    		this.commentEntrys++;
		    	}
		    	// check if first char is a '#' which means instruction
		    	else if (line.charAt(0) == '#') {
		    		this.instructionEntrys++;
		    		// split line into array
		    		// delimiter is space or tab
		    		String[] data = line.split("\\t| ");
		    		// parse each entry
		    		for(int i=0; i<data.length; i++) {
		    			// first entry is unit of freq
		    			if(++i==1){
		    				if(data[i].contains("HZ"))
		    					this.freqMultiplier = 1;
		    				if(data[i].contains("KHZ"))
		    					this.freqMultiplier = 1000;
		    			}
		    		}
		    		
		    		
		    	}
		    	// check if first char is a number which means data
		    	else if (Character.isDigit(line.charAt(0))) {
		    		this.dataEntrys++;
		    		
		    	}
		    	
		    	lineno++;
	    	}
	    }
		
	    System.out.println("lines=" +lineno +" comments=" +this.commentEntrys +" instructions=" +this.instructionEntrys +" data=" +this.dataEntrys);
		System.out.println("Freq multiplier="+this.freqMultiplier);
	}

}