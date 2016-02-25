package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.LoadQuestionnaireCommand;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Main class for running decision graphs in the command line.
 * @author michael
 * 
 *We decieded to add two 
 */

public class DecisionGraphCliRunner {
    
    public static void main(String[] args) throws Exception {
        CliRunner cliRunner = new CliRunner();
        cliRunner.printSplashScreen();
		
	if ( args.length < 2 ) {
            new LoadQuestionnaireCommand().execute(cliRunner, Collections.emptyList());
        } 
        
        else if ( args.length == 2 ) {
            Path tagSpace = Paths.get(args[args.length-2]);
            if ( ! Files.exists(tagSpace) ) {
                cliRunner.printWarning("File %s not found", tagSpace.toString());
                System.exit(1);
            }
            
            Path decisionGraphPath = Paths.get(args[args.length-1]);
            if ( ! Files.exists(decisionGraphPath) ) {
               cliRunner.printWarning("File %s not found", tagSpace.toString());
               System.exit(2);
            }

            CompoundType definitions = parseDefinitions(tagSpace);

            DecisionGraphParser fcsParser = new DecisionGraphParser();

            System.out.println("Reading decision graph: " + decisionGraphPath );
            System.out.println(" (full:  " + decisionGraphPath.toAbsolutePath() + ")" );

            DecisionGraph dg = fcsParser.parse(decisionGraphPath).compile(definitions);

            cliRunner.setDecisionGraph(dg);
            cliRunner.setTagSpacePath(tagSpace);
            cliRunner.setDecisionGraphPath(decisionGraphPath);
        }
        
        else
        {
	// Start of multipul files support
        //Tag_File_Places = all tag paths into Tags arr
        Path [] Tag_File_Places= new Path[args.length];
	int number_of_Tag_File =0;
	//To avoid double checks and duplicated file name
	int [] Tag_File_Places_was_checked = new int[args.length];
	//initial Tag_File_Places_was_checked to 0
	for(int j=0;j<args.length;j++)
        {
            //Tag_File_Places_was_checked = 0;
            Tag_File_Places_was_checked[j] = 0;
	}
	//One and only graph file
        Path Decision_Graph= null;        
        boolean isThereRootSlotCMDline = false;
        //int cmdLineOfRootSlot = 0;
        String rootSlot = null;
        boolean isThereRootSlot = false;
        Path root_Path =null;
        
        for(int i=0;i<args.length;i++){
            //CMD line contains a definition to root:
            //CliRunner *.dg *.ts RootSlot=Animals,Food_v1_beta-15,CarsVersionOne *.dg *.ts
            if(args[i].startsWith("RootSlot=")){ 
                isThereRootSlotCMDline=true;
                //cmdLineOfRootSlot = i;
                //Animals,Food_v1_beta-15,CarsVersionOne *.dg *.ts
                rootSlot =args[i].substring(9);
            }
            //CMD line contains a file to root:
            //CliRunner *.dg *.ts add_to_file/Root.slot
            else if(args[i].endsWith("Root.slot")){
                isThereRootSlot = true;
                root_Path = Paths.get(args[i]);
            }  
            //args[i] is either a .ts file or the one and only .dg file
            else{
            String file_Name_Lower_Case= args[i].toLowerCase(); //caps in paths *.TS or .DG 
            boolean  tag = file_Name_Lower_Case.endsWith(".ts");
            boolean  graph = file_Name_Lower_Case.endsWith(".dg");
                if (tag==true){
                    Tag_File_Places[number_of_Tag_File]=Paths.get(args[i]);
                    number_of_Tag_File++;
                }
                else if (graph==true){
                Decision_Graph = Paths.get(args[i]);
                }
            }
        }
        
	//if no root file or cmdLine definition was inserted --> Error
        if(isThereRootSlotCMDline==false && isThereRootSlot == false && args.length > 2){
            System.out.println("Can not find root file or definition please restart");
            System.exit(0);
        }         
       	
        String DataTags = null;
        //Path DataTags_Path=null;
        //We have a file named Root.Slot in Path root_Path 
        try{
            
            if(isThereRootSlot == true){
                rootSlot = new String(Files.readAllBytes(root_Path), StandardCharsets.UTF_8);
            }
            int startFrom=0;
            for (int ind=0; ind< rootSlot.length(); ind++){
                //Not last file name
		if (rootSlot.charAt(ind) == ','){
		//change Animals --> Animals.ts
                String File_To_Open= rootSlot.substring(startFrom, ind-1)+".ts";
		//next file name after the comma
                startFrom= ind+1;
		//search all tag file paths for the file that endsWith Animals.ts and support duplicate names for files by (Tag_File_Places_was_checked[z]!= 1)
                    for(int z=0; z<=number_of_Tag_File;z++){
                        //no corolation between file name and file paths 
                        if(z==number_of_Tag_File){
                            cliRunner.printWarning("There is not corilation between files names and given paths", (Tag_File_Places[z].toString()));
                            System.exit(5);		
                        }					
                        if((Tag_File_Places[z].endsWith(File_To_Open)) && (Tag_File_Places_was_checked[z]!= 1)){
                            Tag_File_Places_was_checked[z]=1;
                            //path is pointing to missing file
                            if ( ! Files.exists((Tag_File_Places[z]) ) ){
                                cliRunner.printWarning("File %s not found", (Tag_File_Places[z].toString()));
                                System.exit(1);
                            }
                            //exports is a saved word only one accurence in a file
                            String File_Is_Open=new String(Files.readAllBytes(Tag_File_Places[z]), StandardCharsets.UTF_8);
                            int StartExportIndex = File_Is_Open.indexOf("exports ")+8;
                            int FinishExportIndex = File_Is_Open.indexOf(":",StartExportIndex);//supports: Car: consists of
                            int FinishExportIndex2 = File_Is_Open.indexOf(" ",StartExportIndex);//supports: Car [coment]: consists of
                            if(FinishExportIndex < FinishExportIndex2){
                                DataTags.concat(File_Is_Open.substring(StartExportIndex, FinishExportIndex) + ",");
                            }
                            else{
                                DataTags.concat(File_Is_Open.substring(StartExportIndex, FinishExportIndex2) + ",");
                            }
                        }
                    }    
                }
					
		//last file name
                else if(ind==rootSlot.length()-1){
                    String File_To_Open= rootSlot.substring(startFrom, ind)+".ts";
                    //startFrom= ind+1;
                    for(int z=0; z<=number_of_Tag_File;z++){
                    //no corolation between file name and file paths 
			if(z==number_of_Tag_File){
                            cliRunner.printWarning("There is not corilation between files names and given paths", (Tag_File_Places[z].toString()));
                            System.exit(5);		
			}				
                        if((Tag_File_Places[z].endsWith(File_To_Open)) && (Tag_File_Places_was_checked[z]!= 1)){
                            Tag_File_Places_was_checked[z]=1;								
                            //path is pointing to missing file
                            if ( ! Files.exists((Tag_File_Places[z]) )) {
				cliRunner.printWarning("File %s not found", (Tag_File_Places[z].toString()));
				System.exit(1);
                            }
                            //exports is a saved word only one accurence in a file
                            String File_Is_Open=new String(Files.readAllBytes(Tag_File_Places[z]), StandardCharsets.UTF_8);
                            int StartExportIndex = File_Is_Open.indexOf("exports ")+8;
                            int FinishExportIndex = File_Is_Open.indexOf(":",StartExportIndex);//supports: Car: consists of
                            int FinishExportIndex2 = File_Is_Open.indexOf(" ",StartExportIndex);//supports: Car [coment]: consists of
							
                            if(FinishExportIndex < FinishExportIndex2){
                                DataTags.concat(File_Is_Open.substring(StartExportIndex, FinishExportIndex) + ".");
                            }
                            else{
                                DataTags.concat(File_Is_Open.substring(StartExportIndex, FinishExportIndex2) + ".");
                            }
                            //delete unwanted string "exports "
                            //File_Is_Open = File_Is_Open.replace(File_Is_Open.substring(StartExportIndex-8,StartExportIndex-1));
                        }
                    }
		}
            }
        }
        
	catch (IOException e) {
            System.err.println("Problem writing to the file Root.slot");
            System.exit(4);
        }
          
        if ( ! Files.exists(Decision_Graph) ) {
               cliRunner.printWarning("File %s not found", Decision_Graph.toString());
               System.exit(2);
        }
		
        CompoundType definitions = parseMultipleDefinitions(DataTags,Tag_File_Places); 
        //assuming Tag_file_places hold all the paths (exept root)
        DecisionGraphParser fcsParser = new DecisionGraphParser();
        System.out.println("Reading decision graph: " + Decision_Graph );
        System.out.println(" (full:  " + Decision_Graph.toAbsolutePath() + ")" );
        DecisionGraph dg = fcsParser.parse(Decision_Graph).compile(definitions);
        cliRunner.setDecisionGraph(dg);
        cliRunner.setDecisionGraphPath(Decision_Graph); 

	}// else
    }// main

	
    public static CompoundType parseDefinitions(Path definitionsFile) throws DataTagsParseException, IOException 
    {
        System.out.println("Reading definitions: " + definitionsFile );
        System.out.println(" (full:  " + definitionsFile.toAbsolutePath() + ")" );
        return new TagSpaceParser().parse(readAll(definitionsFile)).buildType("DataTags").get();
    }
    /*
        parseMultipleDefinition allows to parse many files together
    */
       
    public static CompoundType parseMultipleDefinitions(String definitionsFile , Path [] arrayOfFiles) throws DataTagsParseException, IOException 
    {
        // turn all the paths to strings
        String [] arrayOfStrings=turnIntoStrings(arrayOfFiles);
        //parse all the strings including the definitions string.
        CompoundType allCompound=new TagSpaceParser().MultiParse(definitionsFile,arrayOfStrings).buildType("DataTags").get();
        return allCompound;
    }
    
     /*
        turnintoString gets an array of paths and turn every parh into a string , returns array of strings  
    */
    public static String [] turnIntoStrings ( Path [] files) throws IOException
    {
        String [] result = new String [files.length];
        for ( int i =0; i<= result.length;i++)
        {
            result[i]=readAll(files[i]);
            int StartExportIndex = result[i].indexOf("exports");
             if (StartExportIndex!=-1)
            {//deleting the exports and preparing for the parsing
            int EndExportIndex = result[i].indexOf("exports")+8;
            result[i] = "/"+result[i].substring(0,StartExportIndex)+result[i].substring(EndExportIndex);
            }
        }
        return result;
    }

    private static String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    
    private static void printUsage() {
        System.out.println("Please provide paths to the tag space and decision grpah files.");
    }
}
