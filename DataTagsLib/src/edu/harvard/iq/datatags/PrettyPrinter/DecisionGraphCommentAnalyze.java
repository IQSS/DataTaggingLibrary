package edu.harvard.iq.datatags.prettyPrinter;

import java.util.ArrayList;

public class DecisionGraphCommentAnalyze {

	/*public static void main(String[] args) {
		String dg = "[ron test] <--test1 test2 test3\n {jahsjdkh}";
		String dg1 = "[ron test] \n{jahsjdkh}";
		ArrayList<CommentClass> res = findCommentBeforeParser(dg);
		String dg2 = insertCommentToDG(dg1,res);
		System.out.println(dg2);
	}*/
        private ArrayList <CommentClass> _commentArr=null ;
	
        public DecisionGraphCommentAnalyze(){};
        
public  void findCommentBeforeParser(String s) {
    ArrayList<CommentClass> commentArr = new ArrayList <CommentClass>();
    String brk="";
    StringBuilder sb = new StringBuilder();
    boolean isBlockComment = false;
    boolean isLineComment = false;
    int indexOfLastBracket=-1;
    String matchWordForBlockComment="";
    for(int i=0; i<s.length();i++)
    {                                
        char c = s.charAt(i);
        switch(c)
        {
            case '{':
            {
                if(!(isBlockComment) && !(isLineComment)) //not comments
                {
                    brk = brk.concat("{");
                    indexOfLastBracket=i;
                }

                else if(isBlockComment || isLineComment)
                {
                    sb.append(c);
                }
                break;
            }
            case '}':
            {
                if(!(isBlockComment) && !(isLineComment)) //not comments
                {
                    brk = brk.concat("}");
                    indexOfLastBracket=i;
                    }
                    else if(isBlockComment || isLineComment)
                    {
                        sb.append(c);
                    }
                    break;
            }
            case '[':
            {
                if(!(isBlockComment) && !(isLineComment)) //not comments
                {
                    brk = brk.concat("[");
                   indexOfLastBracket=i;
                }
                else if(isBlockComment || isLineComment)
                {
                    sb.append(c);
                }
            break;
        }
        case ']':
        {
            if(!(isBlockComment) && !(isLineComment)) //not comments
            {
                brk = brk.concat("]");
                indexOfLastBracket=i;
            }
            else if(isBlockComment || isLineComment)
            {
                sb.append(c);
            }
            break;
        }
        case '<':
        {
            if(i+1<s.length() && i+2<s.length() && s.charAt(i+1)=='-'&& s.charAt(i+2)=='-') //this is the case of lineComment
            {
                sb.append("<");
                isLineComment = true;
            }
            else if(i+1<s.length()&& s.charAt(i+1) =='*') //case of block commnet
            {
                sb.append("<");
                isBlockComment = true;
                if(indexOfLastBracket==-1)
                {
                    matchWordForBlockComment = s.substring(0, i);
                }
                else
                matchWordForBlockComment = s.substring(indexOfLastBracket+1, i);                                                                                                     
            }
            else if(isBlockComment || isLineComment)
            {
                sb.append(c);
            }
            break;
        }
        case '\n':
        {
         if(isLineComment)
         {
             String comment = sb.toString();
             commentArr.add(new CommentClass(brk,comment,0));
             isLineComment=false;
             sb = new StringBuilder();
            }
            else if(isBlockComment)
            {
             sb.append(c);
            }   
            break;
        }
        case '*':
        {
            if(i+1<s.length() && s.charAt(i+1)=='>' && isBlockComment) //this is the end of block Comment
            {
             String apiLog = "*>";
             String comment = sb.toString() + apiLog;
             commentArr.add(new CommentClass(brk,comment,1,matchWordForBlockComment));
             sb = new StringBuilder();
             isBlockComment=false;
             matchWordForBlockComment="";
            }
            else if(isBlockComment || isLineComment)
            {
             sb.append(c);
             }
            break;
        }
        default:
        {
             if(isBlockComment || isLineComment)
            {
             sb.append(c);
            }    
         break;
        }				
       }
    }
_commentArr=commentArr;
}


	public  String insertCommentToDG(String dg)
	{
		
		for(int i=0; i< _commentArr.size(); i++)
		{
			
                    StringBuilder sb = new StringBuilder();
                    boolean isFound = false;
			
                    String brk = _commentArr.get(i).getBracket(); //this is the bracket pattern
                    if(_commentArr.get(i).getKindOfComment()==0)
                    {
                        if(brk.equals(""))
                        {
                            if(i-1>=0)
                            {
                                if((_commentArr.get(i-1).getBracket().equals(_commentArr.get(i).getBracket()))) 
                                    {
                                        dg = handlLineComment(0, _commentArr.get(i).getComment(),dg,true);
                                    }
                              }
                            else{
                                dg = handlLineComment(0, _commentArr.get(i).getComment(),dg,false);
                            }
                        }
                        else{
                    	for(int j=0;j<dg.length() && !isFound;j++)
                            {
                                if(dg.charAt(j)=='[' || dg.charAt(j)==']' || dg.charAt(j)=='{' || dg.charAt(j)=='}')
                                {
                                    sb.append(dg.charAt(j));
                                    if(brk.equals(sb.toString()))
                                    {	
                                        isFound=true;
                                        if(i-1>=0)
                                        {
                                            if((_commentArr.get(i-1).getBracket().equals(_commentArr.get(i).getBracket())) &&
                                            		_commentArr.get(i-1).getKindOfComment()==0)
                                            	
                                                {
                                                    dg = handlLineComment(j, _commentArr.get(i).getComment(),dg,true);
                                                }
                                                else{
                                                        dg = handlLineComment(j, _commentArr.get(i).getComment(),dg,false);
                                                    }
                                          }
                                            else
                                            dg = handlLineComment(j, _commentArr.get(i).getComment(),dg,false);
                                    }
									
                                    }
                            }
                        }
			}
			else if(_commentArr.get(i).getKindOfComment()==1)
			{
				if(_commentArr.get(i).getBracket().equals(""))
				{
                                    System.out.print("r"+_commentArr.get(i).getMatchWordForBlockComment() + _commentArr.get(i).getMatchWordForBlockComment().equals(""));
                                    if(_commentArr.get(i).getMatchWordForBlockComment().equals(""))
                                    {
                                            sb.append(_commentArr.get(i).getComment());
                                            sb.append(dg);
                                            dg = sb.toString();
                                    }
                                    else
                                    {dg = handlBlockComment(-1,_commentArr.get(i).getMatchWordForBlockComment() ,_commentArr.get(i).getComment(),dg,true);}
				}
				else{
				for(int j=0;j<dg.length() && !isFound;j++)
                                {
                                  if(dg.charAt(j)=='[' || dg.charAt(j)==']' || dg.charAt(j)=='{' || dg.charAt(j)=='}')
                                    {
                                        sb.append(dg.charAt(j));
                                    if(brk.equals(sb.toString()))
                                    {	
            				isFound=true;
            				dg = handlBlockComment(j,_commentArr.get(i).getMatchWordForBlockComment() ,_commentArr.get(i).getComment(),dg,false);                                       
                                     }									
                                }
                                }
                            }
                            }
		}
	
		return(dg);
	}
	public  String handlLineComment(int indexOfLastBrk,String comment, String dg,boolean isSame)
	{
		StringBuilder sb = new StringBuilder();
		boolean isFoundNl = false;
		int i=0;
		for(i=indexOfLastBrk; i< dg.length() && !isFoundNl;i=i+1)
		{
			if(dg.charAt(i)=='\n')
			{
				
				isFoundNl=true;
			}
		}
		i=i-1;
		if(!isSame){
			String firstDG = dg.substring(0,i);
			sb.append(firstDG);
			sb.append(comment);
			sb.append("\n");
			sb.append(dg.substring(i+1));
		}
		else if(isSame)
		{
			String firstDG = dg.substring(0,i+1);
			sb.append(firstDG);
			sb.append(comment);
			sb.append("\n");
			sb.append(dg.substring(i+1));
		}
		
		return(sb.toString());
	}

        public  String handlBlockComment (int indexOfLastBrk, String matchWord ,String comment,String dg,boolean isFirstInText)
        {
            
            int i=0;
            boolean isFound=false;
            int counter = 0;
            StringBuilder sb = new StringBuilder();
            
            for(i = indexOfLastBrk+1; i< dg.length() && !isFound; i++)
            {
                if(dg.charAt(i)==matchWord.charAt(counter))
                   {
                        counter++;
                   }
                
                if(counter==matchWord.length())
                {
                    isFound=true;
                }
            }
            //now i is the index of where put the block Comment
                if(isFound==true){
                	if(!isFirstInText)
                	{
                            String firstDG = dg.substring(0,i-2);
                            sb.append(firstDG);
                            sb.append(comment);
                            sb.append(dg.substring(i-2));
                	}
                	else{
                        String firstDG = dg.substring(0,i);
                    	sb.append(firstDG);
                    	sb.append(comment);
                    	sb.append(dg.substring(i));
                	}
                    return(sb.toString());
                }
            	
            
            return(dg);
        }
}
