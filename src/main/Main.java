/**
 * MIT License
 *
 * Copyright (c) 2018 Alexander Miller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * I do not claim any rights or anything else to DiffChecker.com. This is only meant to be
 * a public tool to assist with large product development.
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Alexander Miller
 * @version 10/20/2018
 */
public class Main {
	
	/** sourceDirectoryPath - String containing the system-specific path to source directory */
	private static String sourceDirectoryPath;
	
	/** targetDirectoryPath - String containing the system-specific path to target directory */
	private static String targetDirectoryPath;
	
	/** ignoreMetadataFiles - boolean to describe whether the metadata files should be diff'ed as well. Default is it will ignore */
	private static Boolean ignoreMetadataFiles;
	
	/** expiresSetting - String which describes how long the diff will last on DiffChecker's servers. Default is a day */
	private static String expiresSetting;
	
	/** configFileLocation - Configuration file by default is the same directory as the jar for simplicity */
	private static String configFileLocation = ".\\DiffCheckMe.properties";
	
	/**
	 * Main
	 * 
	 * @param args - String[]
	 */
	public static void main(String[] args) 
	{		
		// 1. init
		init();
		
		// 2. Read config file and set parameters accordingly
		readConfigurationFile();
		
		// 3. Look at configuration resource for source folder
		ArrayList<File> sourceFiles = getFilesInDirectory(sourceDirectoryPath);
		
		// 4. Look at configuration resource for target folder
		ArrayList<File> targetFiles = getFilesInDirectory(targetDirectoryPath);
	
		// 5. Marry files of the same name together
		Map<String, String> mapOfMarriedFiles = getMapOfFileMarraiges(sourceFiles, targetFiles);
		
		// 6. Call DiffChecker CLI on all files
		try {
			callDiffCheckerCLI(mapOfMarriedFiles, sourceDirectoryPath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * init
	 * 
	 * function to run every time the program starts up
	 */
	public static void init()
	{
		sourceDirectoryPath = new String();
		targetDirectoryPath = new String();
		ignoreMetadataFiles = true;
		expiresSetting = new String();
	}
	
	/**
	 * readConfigurationFile
	 * 
	 * Function which sets all of the global parameters 
	 * based on what the config files says
	 */
	public static void readConfigurationFile()
	{
		Properties prop = new Properties();
		
		InputStream input = null;
				
		try 
		{
			input = new FileInputStream(configFileLocation);

			// load a properties file
			prop.load(input);
		
			sourceDirectoryPath = prop.getProperty("sourceDirectory");		
			
			targetDirectoryPath = prop.getProperty("targetDirectory");
			
			String ignoreMetadataFilesString = prop.getProperty("ignoreMetadataFiles");
			
			if(ignoreMetadataFilesString != null && ignoreMetadataFilesString.equalsIgnoreCase("true"))
			{
				ignoreMetadataFiles = true;
			}
			else
			{
				ignoreMetadataFiles = false;
			}
			
			if(sourceDirectoryPath == null)
			{
				throwError("sourceDirectoryPath is null. Therefore, there is nothing to compare. Please define a Source Directory to continue.");
			}
			
			if(targetDirectoryPath == null)
			{
				throwError("targetDirectoryPath is null. Therefore, there is nothing to compare. Please define a Target Directory to continue.");
			}
			
			expiresSetting = prop.getProperty("expires");
			
			if(expiresSetting == null)
			{
				expiresSetting = "day";
			}
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		} 
		finally 
		{
			if (input != null) 
			{
				try 
				{
					input.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}		
	}
	
	/**
	 * throwError
	 * 
	 * Simple function which throws and error message
	 * to the user when something happens that isn't 
	 * allowed. 
	 * 
	 * @param errorMessage
	 */
	public static void throwError(String errorMessage)
	{
		throw new java.lang.RuntimeException(errorMessage);
	}
	
	/**
	 * callDiffCheckerCLI
	 * 
	 * Function which calls Diffchecker's CLI commands
	 * 
	 * This function assumes you have everything installed correctly: https://www.diffchecker.com/cli
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void callDiffCheckerCLI(Map<String, String> mapOfFiles, String sourceDirectory) throws IOException, InterruptedException
	{
		File folder = new File(sourceDirectory);
		
		String cmdPrompt="cmd";
        String path="/c";
        
        for(String fileName : mapOfFiles.keySet())
		{
        	List<String> updateCommand=new ArrayList<String>();
        	updateCommand.add(cmdPrompt);
        	updateCommand.add(path);
        
			// The absolute paths must contain a space or else its just a single file. DiffChecker API doesn't compare single files
			if(mapOfFiles.get(fileName).contains(" "))
			{
				updateCommand.add("diffchecker --expires " + expiresSetting + " " + mapOfFiles.get(fileName));
			}
			
			runExecution(updateCommand,folder);
		}
	}
	
	/**
	 * runExecution
	 * 
	 * Function taken from https://stackoverflow.com/questions/40503074/how-to-run-npm-command-in-java-using-process-builder
	 * 
	 * Takes the command built in another function and runs NPM commands to interface with DiffChecker
	 * 
	 * @param command
	 * @param navigatePath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void runExecution(List<String> command, File navigatePath) throws IOException, InterruptedException{

        System.out.println(command);

        ProcessBuilder executeProcess=new ProcessBuilder(command);
        executeProcess.directory(navigatePath);
        Process resultExecution=executeProcess.start();

        BufferedReader br=new BufferedReader(new InputStreamReader(resultExecution.getInputStream()));
        StringBuffer sb=new StringBuffer();

        String line;
        while((line=br.readLine())!=null){
            sb.append(line+System.getProperty("line.separator"));
        }
        br.close();
        int resultStatust=resultExecution.waitFor();
        System.out.println("Result of Execution"+(resultStatust==0?"\tSuccess":"\tFailure"));
    }
	
	/**
	 * getMapOfFileMarraiges
	 * 
	 * Function which looks at all files in the directories and if they have the same name,
	 * it will "marry". For example:
	 * 
	 * main folder/ 
	 *       
	 *            source folder / 
	 *                  
	 *                           Test1.js
	 *                           Test2.js
	 *             
	 *            target folder /
	 *            
	 *                          Test1.js
	 *                          
	 *  It will find that Test1.js is a commonality between all subdirectories and consider them the
	 *  "same" file. It will marry it by Map<FileName, source_absolute_path + " " + target_absolute_path>
	 * 
	 * @param source - list of files found in the source folder
	 * @param target - list of files found in the target folder
	 * @return Map of File name to both file paths
	 */
	public static Map<String, String> getMapOfFileMarraiges(ArrayList<File> source, ArrayList<File> target)
	{		
		Map<String, String> mapOfMarriedFiles = new HashMap<String, String>();
		
		for(File sourceFile : source)
		{
			if(mapOfMarriedFiles.get(sourceFile.getName()) == null)
			{
				mapOfMarriedFiles.put(sourceFile.getName(), sourceFile.getAbsolutePath());
			}
		}
		
		for(File targetFile : target)
		{
			if(mapOfMarriedFiles.get(targetFile.getName()) == null)
			{
				mapOfMarriedFiles.put(targetFile.getName(), targetFile.getAbsolutePath());
			}
			else
			{
				String originalValue = mapOfMarriedFiles.get(targetFile.getName());
				mapOfMarriedFiles.put(targetFile.getName(), targetFile.getAbsolutePath() + " " + originalValue );
			}
		}
		
		return mapOfMarriedFiles;
	}
	
	/**
	 * getFilesInDirectory
	 * 
	 * Given a directory via String parameter, 
	 * return all of the file names found
	 * 
	 * @param directory - String of the system directory where the files should be pulled from
	 * 
	 * @return ArrayList<File> - ArrayList of all files found in given directory
	 */
	public static ArrayList<File> getFilesInDirectory(String directory)
	{	
		/** folder - initialize all of the folder's contents */
		File folder = new File(directory);
				
		/** listOfFilesToEvaluate - array of all files, both files and directories in the given location */
		File[] listOfFilesToEvaluate = folder.listFiles();
				
		/** listOfFilesToReturn - ArrayList containing all of the files, not directories, chosen in the filtering */
		ArrayList<File> listOfFilesToReturn = new ArrayList<>();
		
		for (int i = 0; i < listOfFilesToEvaluate.length; i++) 
		{	
			Boolean isMetadata = listOfFilesToEvaluate[i].getAbsolutePath().endsWith(".xml");
			
			if (listOfFilesToEvaluate[i].isFile() && 
					( 
							(ignoreMetadataFiles == true && !isMetadata ) || 
					        (ignoreMetadataFiles == false)
			        )
				)
			{
				System.out.println(listOfFilesToEvaluate[i].getAbsolutePath());
				listOfFilesToReturn.add(listOfFilesToEvaluate[i]);
			}
			else if(listOfFilesToEvaluate[i].isDirectory())
			{
				// recursively get all sub directories
				listOfFilesToReturn.addAll(getFilesInDirectory(listOfFilesToEvaluate[i].getAbsolutePath()));
			}
		}
		
		return listOfFilesToReturn;
	}
}