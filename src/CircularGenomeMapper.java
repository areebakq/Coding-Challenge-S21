import ca.ualberta.stothard.cgview.*;

import java.awt.Color;
import java.io.*;
import java.util.Scanner;

public class CircularGenomeMapper implements CgviewConstants{
	
	public static int geneLength;
	public static String geneName;
	public static Cgview cgview;

	public static void main(String args[]) throws IOException, Exception {

        readData();
        
        // map characteristics
        cgview.setWidth(600);
        cgview.setHeight(600);
        cgview.setTitle(geneName);
        cgview.setShowWarning(true);
        cgview.setLabelLineLength(10.0d);
        cgview.setLabelLineThickness(0.7f);
        
        try {
            //create a PNG file
            CgviewIO.writeToPNGFile(cgview, "CircularGenomeMap.png");
            //create an SVG file
            CgviewIO.writeToSVGFile(cgview, "CircularGenomeMap.svg", false);
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
	}
	
	private static void readData() throws FileNotFoundException {
		File file = new File("../Coding-Challenge-S21/Genome.gb");
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine()) {
			String next = scan.next();
			if (next.equals("ORGANISM")) {
				geneName = scan.nextLine().trim();
			}
			if (next.equals("LOCUS")) {
				geneLength = returnGeneLength(scan);
				cgview = new Cgview(geneLength);
			}
			if (next.equals("FEATURES")) {
				createFeatures(scan);
			}
		}
		scan.close();
	}
	
	private static int returnGeneLength(Scanner scan) {
		String line = scan.nextLine();
		String[] arr = line.split("\\s+");
		int length = Integer.parseInt(arr[2]);
		return length;
	}
	
	private static void createFeatures(Scanner scan) {
		// creates 2 feature slots to alternate for better visualization
    	FeatureSlot featureSlot1 = new FeatureSlot(cgview, DIRECT_STRAND);
    	FeatureSlot featureSlot2 = new FeatureSlot(cgview, DIRECT_STRAND);
    	boolean alternate = true;
    	
    	// reads all features of genome
        while (scan.hasNext() && !scan.next().equals("ORIGIN")) {
        	String geneLabel = "";
			String next = scan.next();
			
			// finds gene label and stores it in geneLabel
			while (scan.hasNext() && !next.contains("/gene=")) {
				next = scan.next();
			}
			geneLabel = next.substring(7, next.length()-1);
			
			//finds gene range
			while (scan.hasNext() && !next.equals("gene")) {
				next = scan.next();
			}
			if (scan.hasNext()) {
				next = scan.next();
				if (next.contains("complement"))
					next = next.substring(11, next.length()-1);
				String[] range = next.split("\\.+");
				
	        	int startRange = Integer.parseInt(range[0]);
	        	int endRange = Integer.parseInt(range[1]);
	        	
	            // creates feature to add to FeatureSlot
	            Feature feature;         
	        	if (alternate) {
	        		feature = new Feature(featureSlot1, geneLabel);
	        		feature.setColor(Color.CYAN);
	        		alternate = false;
	        	}
	        	else {
		            feature = new Feature(featureSlot2, geneLabel);
		            feature.setColor(Color.BLUE);
	        		alternate = true;
	        	}
	        		
	            // creates feature range
	            FeatureRange featureRange = new FeatureRange (feature, startRange, endRange);
	            featureRange.setDecoration(DECORATION_CLOCKWISE_ARROW);				
			}
        }
	}
}