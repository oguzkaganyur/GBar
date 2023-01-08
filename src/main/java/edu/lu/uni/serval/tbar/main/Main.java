package edu.lu.uni.serval.tbar.main;

import java.io.File;

import edu.lu.uni.serval.tbar.AbstractFixer;
import edu.lu.uni.serval.tbar.TBarFixer;
import edu.lu.uni.serval.config.Configuration;

/**
 * Fix bugs with Fault Localization results.
 * 
 * @author kui.liu
 *
 */
public class Main {
	
	public static void main(String[] args) {
/*		if (args.length != 3) {
			System.err.println("Arguments: \n"
					+ "\t<Bug_Data_Path>: the directory of checking out Defects4J bugs. \n"
					+ "\t<Bug_ID>: bug id of each Defects4J bug, such as Chart_1. \n"
//					+ "\t<Suspicious_Code_Positions_File_Path>: \n"
//					 +"\t<Failed_Test_Cases_File_Path>: \n"
					+ "\t<defects4j_Home>: the directory of defects4j git repository.\n");
			System.exit(0);
		}
 */
		for (int i = 1; i <= 1; i++) {
			String bugDataPath = "/Users/turgutt/GBar/D4J/projects/";
			String bugId = "VUL4J_" + i; // "Chart_1"
			String defects4jHome = "/Users/turgutt/vul4j/"; // "../defects4j/"
			System.out.println(bugId);
			fixBug(bugDataPath, defects4jHome, bugId);
		}
	}

	public static void fixBug(String bugDataPath, String defects4jHome, String bugIdStr) {
		Configuration.outputPath += "NormalFL/";
		String suspiciousFileStr = Configuration.suspPositionsFilePath;
		
		String[] elements = bugIdStr.split("_");
		String projectName = elements[0];
		int bugId;
		try {
			bugId = Integer.valueOf(elements[1]);
		} catch (NumberFormatException e) {
			System.err.println("Please input correct buggy project ID, such as \"Chart_1\".");
			return;
		}
		
		AbstractFixer fixer = new TBarFixer(bugDataPath, projectName, bugId, defects4jHome);
		fixer.dataType = "TBar";
		fixer.metric = Configuration.faultLocalizationMetric;
		fixer.suspCodePosFile = new File(suspiciousFileStr);
		if (Integer.MAX_VALUE == fixer.minErrorTest) {
			System.out.println("Failed to defects4j compile bug " + bugIdStr);
			return;
		}
		
		fixer.fixProcess();
		
		int fixedStatus = fixer.fixedStatus;
		switch (fixedStatus) {
		case 0:
			System.out.println("Failed to fix bug " + bugIdStr);
			break;
		case 1:
			System.out.println("Succeeded to fix bug " + bugIdStr);
			break;
		case 2:
			System.out.println("Partial succeeded to fix bug " + bugIdStr);
			break;
		}
	}

}
