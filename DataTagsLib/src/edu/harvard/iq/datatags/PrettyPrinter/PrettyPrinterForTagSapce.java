/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author goldr
 */


package edu.harvard.iq.datatags.PrettyPrinter;
import java.util.ArrayList;
import java.util.Scanner;

//import java.util.stream.Collectors;

public class PrettyPrinterForTagSapce {

	final static int MAXLENGTHOFLINE = 80;
	String _targeText;
	
        public PrettyPrinterForTagSapce()
        {
            
        }
        // this function initialize the tag space pretty printer 
        public String initTagSapcePrettyPrinter(String text)
        {
            this._targeText=text;
            String beforPrettyPrinter ;
            try{
               beforPrettyPrinter = InitTagSpacaPrettyPrinter(this._targeText,MAXLENGTHOFLINE);
               return(beforPrettyPrinter);
            }
            catch(Exception e)
            {
                
                System.out.println("Couldnt pretty print because of:" + e.getMessage());
                return(_targeText);
            }
            
        }
        
	// this function checks what the next thing she need to do : handle 
        // sub-slots or handle line or block comments   
	public static int findWhatTodo(String text)
	{
		int continueWithTagSpace = getIndexOfType(text);
		
		int continueWithLineComment = getIndexOfLineComment(text);
		int continueWithBlockComment = getIndexOfBlockComment(text);
		int minIndex = -1;
		boolean isFirst = true;
		int[] indexArr = new int[3];
		indexArr[0] = continueWithTagSpace;
		indexArr[1] = continueWithLineComment;
		indexArr[2] = continueWithBlockComment;
		
		
		for(int i = 0; i<indexArr.length; i++)
		{
			if(indexArr[i] != (-1))
			{
				if(isFirst)
				{
					minIndex = indexArr[i];
					isFirst = false;
				}
				else
				{
					minIndex = Math.min(minIndex, indexArr[i]);
				}
			}
		}
		for(int i = 0 ; i<indexArr.length;i++)
		{
			if(indexArr[i]==minIndex && minIndex!=(-1))
			{
				return(i);
			}
		}
		return(-1);
	}
	
	
	// this function returns the index of the next line comment
	public static int getIndexOfLineComment(String text)
	{
		return(text.indexOf("<--"));
	}
	//this function returns the index of the next block comment
	public static int getIndexOfBlockComment(String text)
	{
		return(text.indexOf("<*"));
	}
	// this function deals with the case that we have a line comment
	public static String[] handleLineComment(String text)
	{
		
		String[] strArr = new String[2];
		int indexOfLineComment = text.indexOf("\n");
		strArr[0] = text.substring(0, indexOfLineComment+1);
		strArr[1] = text.substring(indexOfLineComment+1, text.length());
		return(strArr);
		
	}
        
        //this function deals with the case that we have a block comment
	public static String[] handleBlockComment(String text)
	{
		String[] strArr = new String[2];
		int indexOfEndBlockComment = text.indexOf("*>");
		strArr[0] = text.substring(0, indexOfEndBlockComment+2);
		strArr[1] = text.substring(indexOfEndBlockComment+2, text.length());
		return(strArr);
	}
	
	

	
	//this method is the Init Method for the TagSpace
	public static String InitTagSpacaPrettyPrinter(String StringToMakePretty, int maxLengthOfLine)
	{
		String[] element = new String[2];
		
		String workOnString = flattening(StringToMakePretty); //this line make flattening all the string witout spare spaces Tabs and newLines
		
		int indexOfNextPars = findWhatTodo(workOnString);
		
		StringBuilder targetString = new StringBuilder();
		if(indexOfNextPars == 0)
		{
			
			Scanner sc = new Scanner(workOnString).useDelimiter(getStringOfType(workOnString)); //split by the first Type
			
			targetString.append(sc.next());
			element  = getSlotType(getIndexOfType(workOnString),workOnString); // element[0] is string the define by the type, element[1] is the rest of the DataTag
			
			String typeOrg = handleType(element[0],getStringOfType(workOnString)); //contact to the StringBuilder the args of type
			
			targetString.append(typeOrg); //contact to the StringBuilder the args of type
			
			sc.close();
		}
		else if (indexOfNextPars == 1)
		{
			element  = handleLineComment(workOnString);
			targetString.append(element[0]);
			
		}
		
		else if (indexOfNextPars == 2)
		{
			element  = handleBlockComment(workOnString);
			
			targetString.append(element[0]);
		}
		
		while(findWhatTodo(element[1])!=-1) //work on the rest of the String pretty much as before, work until nothing to work on
		{	
			
			String[] temp = doRest(element[1]);
			targetString.append(temp[0]);
			element[1]=temp[1];
			
		}
		String target =  fixLineComment(targetString.toString());
		return(breakLongLine(target,maxLengthOfLine));
	}
	
	//this method is return string of the first type that apears in test
	public static String getStringOfType(String tagSpace)
	{
		int consistsIndex=tagSpace.indexOf("consists of");
		int someIndex=tagSpace.indexOf("some of");
		int oneIndex = tagSpace.indexOf("one of");
		int minIndex = -1;
		boolean isFirst = true;
		int[] indexArr = new int[3];
		indexArr[0] = consistsIndex;
		indexArr[1] = someIndex;
		indexArr[2] = oneIndex;
		for(int i = 0; i<indexArr.length; i++)
		{
			if(indexArr[i] != (-1))
			{
				if(isFirst)
				{
					minIndex = indexArr[i];
					isFirst = false;
				}
				else
				{
					minIndex = Math.min(minIndex, indexArr[i]);
				}
				
			}
		}
		for(int i=0; i<indexArr.length;i++)
		{
			if(indexArr[i] == minIndex){
				if(i==0)
				{
					return("consists of");
				}
				if(i==1)
				{
					return("some of");
				}
				if(i==2)
				{
					return("one of");
				}
			}
				
		}
			return(null);
	}
	
	
	//this method return the index of the first definition of the subslots
	public static int getIndexOfType(String tagSpace) 
	{
		int consistsIndex=tagSpace.indexOf("consists of");
		int someIndex=tagSpace.indexOf("some of");
		int oneIndex = tagSpace.indexOf("one of");
		int minIndex = -1;
		boolean isFirst = true;
		int[] indexArr = new int[3];
		indexArr[0] = consistsIndex;
		indexArr[1] = someIndex;
		indexArr[2] = oneIndex;
		for(int i = 0; i<indexArr.length; i++)
		{
			if(indexArr[i] != (-1))
			{
				if(isFirst)
				{
					minIndex = indexArr[i];
					isFirst = false;
				}
				else
				{
					minIndex = Math.min(minIndex, indexArr[i]);
				}
			}
		}
		return(minIndex);
	}
	
	//this method return string of the subslots that define already pretty printed 
	
	public static String handleType(String targetString,String typeBy)
	{
		
	String[] s = new String[2];
	s = targetString.split(typeBy,2);
	
	StringBuilder b = new StringBuilder();
	
	if(s[1].contains(","))
	{
		
		ArrayList<String> splitByCommaArrList = splitByComma(s[1]);
		b.append(typeBy);
		
		if((!(splitByCommaArrList.get(0).startsWith("<--")) &&  (!splitByCommaArrList.get(0).startsWith(" <--"))))
		{
			
			b.append("\n\t");
		}
		
		for(int i =0;i<((splitByCommaArrList.size()));i++)
		{	
			
			
			if ((i+1 < splitByCommaArrList.size()) && (splitByCommaArrList.get(i+1).startsWith("<--") ||  splitByCommaArrList.get(i+1).startsWith(" <--")))
			{
				b.append(splitByCommaArrList.get(i));
				String goingToAppend = splitByCommaArrList.get(i+1);
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j<goingToAppend.length();j++)
				{
					
					if(goingToAppend.charAt(j)=='\n')
					{
					
						sb.append(goingToAppend.substring(0, j+1));
						sb.append("\t");
						sb.append(goingToAppend.substring(j+1));
						sb.append("\n\t");
						
					}
				}
				goingToAppend=sb.toString();
				b.append(goingToAppend);
				
				i=i+1;
			}
			else{
					b.append(splitByCommaArrList.get(i) + "\n\t");
				}
		}	
	}
	
	else if(!(s[1].contains(",")))
	{
		b.append(typeBy);
		b.append(s[1]);
	}
	return(b.toString());
	}
	
	
        // this method split by comma the given text to array
	public static ArrayList<String> splitByComma (String str)
	{
		ArrayList<String> splitByCommaArr = new ArrayList<String>();
		int bracketChecker = 0;
		boolean flagBlockComment = false;
		boolean flagLineComment = false;
		int blockComment = 0; 
		for (int i = 0 ; i< str.length(); i++){
			char c = str.charAt(i);
			switch(c){
				case '[':  {
					        if (!flagBlockComment && !flagLineComment ){
							bracketChecker = bracketChecker + 1;
					        }
				}
				break;
				
				case ']' :  {
							if (!flagBlockComment && !flagLineComment){
							bracketChecker = bracketChecker - 1;
							}
				}
				break;
				case '<' : 
						  {	  
						  if (str.charAt(i+1) == '*'){
							  flagBlockComment = true;
							  blockComment++;
						  	 }
						  if ( str.charAt(i+1) == '-' && str.charAt(i+2) == '-'){  
							  flagLineComment = true;
							  
							}
						  }
				break; 
				
				case '>' :
					{
					if (!flagLineComment){	
						if (str.charAt(i-1) == '*'){
						blockComment--;
							if (blockComment == 0){
								flagBlockComment = false;
							}
						}
					
						}
					}
				break;
				case ',' : 
					{ 
						if (!flagLineComment){
							if (bracketChecker == 0 )
							{
								splitByCommaArr.add(str.substring(0,i+1));
								str = str.substring(i+1);
								i=0;
							}
						} 
					}
				break;	
				case '.' : 
					{ 
					if (!flagLineComment){
						if (bracketChecker == 0 )
							{
							splitByCommaArr.add(str.substring(0,i+1));
							str = str.substring(i+1);
							i=0;
							}
					 	}
				   }
					
				case '\n' :
					{
						if (flagLineComment){
							flagLineComment = false;
						}
					}
			}	
		}
		if(!str.equals("")){
			splitByCommaArr.add(str);
		}
		
		return splitByCommaArr;
	}
	// this method breaks a long line with 80 characters long
	public static String breakLongLine(String text, int param)
	{
		StringBuilder s = new StringBuilder();
		int closeNewLine=0;
		boolean isBreak=false;
		
		for(int i=0; i<text.length();i++)
		{
			if(closeNewLine>=param)
			{
				
				s.append(text.substring(0, i));
				s.append("\n");
				s.append(text.substring(i+1));
				closeNewLine=0;
				isBreak=true;
				
			}
			else if(text.charAt(i)=='\n' && closeNewLine<param)
			{
				closeNewLine=0;
			}
			closeNewLine++;
		}
		
		if(isBreak)
			return(s.toString());
		else
			return(text);
		
	}
	
	
	//this method return arr of string that the first element is the type and the second element is the subslots name. the type of the slot can be some of,one of or consist of
	public static String[] getSlotType(int indexOfSLotType, String text)
	{
		String justType = text.substring(indexOfSLotType);
		String[] s = new String[2];		
		int bracketCount = 0;
		for (int i=0; i<justType.length();i++){
			
			char c = justType.charAt(i);
			switch(c){
			case '[' : 
			{
				
				bracketCount++;

			}
			break;
			case ']' :
			{
				bracketCount--; 
			}
			break;
			case '.' :
				if (bracketCount == 0){
					s[0] = justType.substring(0,i+1);
					if(i+1>=justType.length())
					{
						s[1]="";
					}
					else {
						
						s[1] = justType.substring(i+1);
					}
					i = text.length()+1;
				}
				break;	
			}
		}
		//s = justType.split("\\.",2);
		//String fixWithDot = s[0].concat(".");
		//s[0]=fixWithDot;
		
		return(s);
	}
	
        // this method deals with the rest of the work needed on the slot
	public static String[] doRest(String workOnString)
	{
		
		String[] element = new String[2];
		
		int indexOfNextPars = findWhatTodo(workOnString);
		
		
		indexOfNextPars = findWhatTodo(workOnString);
		StringBuilder targetString = new StringBuilder();
		if(indexOfNextPars == 0)
		{
			Scanner sc = new Scanner(workOnString).useDelimiter(getStringOfType(workOnString));
			StringBuilder s = new StringBuilder();
			s.append(sc.next());
			element  = getSlotType(getIndexOfType(workOnString),workOnString);
			String typeOrg = handleType(element[0],getStringOfType(workOnString));
			
			s.append(typeOrg);
			
			String[] tempString ={s.toString(),element[1]};
			
			return(tempString);
		}
		else if (indexOfNextPars == 1)
		{
			
			//targetString.append();
			element  = handleLineComment(workOnString);
			
			targetString.append(element[0]);
			return(element);
		}
		
		else if (indexOfNextPars == 2)
		{
			element  = handleBlockComment(workOnString);
			targetString.append(element[0]);
			return(element);
		}
		
		return(element);
	}

	//this is method remove all \n and \t and spaces only for one line that separate by only one space 
	public static String flattening(String text)
	{

		text = lineCommentAndBlockCommantElemination(text);
		
		while(text.contains("\t"))
		{
			int indexOfNewLine = text.indexOf("\t");
			String part1 = text.substring(0,indexOfNewLine);
			String part2 = text.substring(indexOfNewLine+1);
			text = part1.concat(part2);
		}
		
		while(text.contains("  "))
		{
			int indexOfNewLine = text.indexOf("  ");
			String part1 = text.substring(0,indexOfNewLine);
			part1.concat(" ");
			String part2 = text.substring(indexOfNewLine+1);
			text = part1.concat(part2);
		}
		return(text);
	}
	// this function deals with the special identation in case 
        //of new line\block comment
	public static String lineCommentAndBlockCommantElemination(String text)
	{
		
		boolean lineCommentFlag = false;
		boolean blockCommentFlag = false;
		int countBlockComment = 0;
		for (int i= 0; i<text.length();i++){
			StringBuilder sb = new StringBuilder();
			if (i+1 == text.length()){
				
			}
			char c = text.charAt(i);
			switch(c){
				case '\n': {
					        if (!lineCommentFlag && !blockCommentFlag){
					        	
					        	sb.append(text.substring(0, i));
					        	sb.append(text.substring(i+1,text.length()));
					        	text = sb.toString();
					        }
					        else   
					        	{
					        		 lineCommentFlag = false;					        		
					        	}
			     break;
				}
				
				case '<': {
						  if (text.charAt(i+1) == '-' && text.charAt(i+2) == '-'){
							  lineCommentFlag = true; 
						  }
						  if (text.charAt(i+1) == '*'){
							  countBlockComment++;
							  blockCommentFlag = true;
						  }
				break;		  
				}
				
				case '>' : {
							
						 	if (text.charAt(i-1) == '*'){
						 		countBlockComment--;
						 		if (countBlockComment == 0){
								  blockCommentFlag = false;
						 		}
						 	}
				break;
				}
			}
		}

		return text;
	}
	
	// this function deals with fixing the text, after conductiong 
        // a new line comment handling
	public static String fixLineComment (String text){
		for (int i = 0 ; i< text.length(); i++){
			StringBuilder sb = new StringBuilder();
			char c = text.charAt(i);
			switch(c){
			case '.' : {
					if (i+1 < text.length() && i+2 < text.length() && i+3 < text.length()
						&& i+4 < text.length() && i+5 < text.length() && i+6 < text.length()  && text.charAt(i+1) == '\n' &&
						text.charAt(i+2) == '\t' && Character.isWhitespace(text.charAt(i+3)) && text.charAt(i+4) == '<'
						&& text.charAt(i+5) == '-' && text.charAt(i+6) == '-')
						 {
						sb.append(text.substring(0, i));
						sb.append(".");
						sb.append(text.substring(i+3,text.length()));
			        	text = sb.toString();
						 }
						break;
				}
			}
		}
		return text;
	}
	
	
		
	
}

		
	
	
	