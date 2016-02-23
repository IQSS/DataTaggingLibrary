/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.prettyPrinter;

import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParseResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode.AggregateAssignment;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode.Assignment;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode.AtomicAssignment;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author amazor
 */
public class PrettyPrinterForDG {
    String _dgPrettyPrinted;
    public PrettyPrinterForDG(Path decisionGraphPath) throws DataTagsParseException, IOException
    {
        String strGraph = new String(Files.readAllBytes(decisionGraphPath), StandardCharsets.UTF_8); 
        DecisionGraphCommentAnalyze conmentAnalyzer = new DecisionGraphCommentAnalyze();
        conmentAnalyzer.findCommentBeforeParser(strGraph);

        DecisionGraphParser fcsParser = new DecisionGraphParser();
        DecisionGraphParseResult DgParser =  fcsParser.parse(decisionGraphPath);
        List <? extends AstNode> nodes1 = DgParser.getNodes();
        String prettyPrinter = prettyPrinterToGraph(nodes1, 0);
        String prettyPrinterWithLineComment= conmentAnalyzer.insertCommentToDG(prettyPrinter);
        _dgPrettyPrinted=prettyPrinterWithLineComment;
        //System.out.println(prettyPrinterWithLineComment);
    }
    
    private static String prettyPrinterToGraph (List<? extends AstNode> lstNodes, int nSpaces){
        String res = "";
        for (Object object : lstNodes) {
            String name = object.getClass().getName().split("\\.")[7];
            String id = "";
            //object.
            if (((AstNode)object).getId() != null) 
                id = ">" + ((AstNode)object).getId() + "< ";
            
            if((nSpaces == 0) && (name.contains("AstAskNode"))){
                res += "\n";
            }
            
            res += "\n" + addSpaces(nSpaces) + "[" + id;
            
            switch (name){
                case "AstCallNode":
                    res += "call: " + ((AstCallNode)(object)).getCalleeId() + "]"; 
                    break;
                case "AstEndNode":
                    res += "end]"; 
                    break;
                case "AstSetNode":
                    String strAssignments = createAssignmentString(((AstSetNode)(object)).getAssignments(), nSpaces + 2);
                    res += "set: " + strAssignments + "]";
                    break;
                case "AstRejectNode":
                    res += "reject: " + ((AstRejectNode)(object)).getReason() + "]";
                    break;    
                case "AstTodoNode":
                    res += "todo: " + handleTextSpaces(((AstTodoNode)(object)).getTodoText(), nSpaces + 7) + "]"; 
                    break;
                case "AstAskNode":
                    String strAnswers = createAnswersString(((AstAskNode)(object)).getAnswers(), nSpaces + 2);
                    String strTerms = createTermsString(((AstAskNode)(object)).getTerms(), nSpaces + 2);
                    
                    res += "ask:\n" + addSpaces(nSpaces + 2) + 
                            "{text: " + handleTextSpaces(((AstAskNode)(object)).getTextNode().getText(), nSpaces + 7) + 
                            "}" + strTerms + strAnswers + "]";
                break;
            }
        }
        return res;
    }
    
    private static String handleTextSpaces(String oldText, int nSpaces){
        StringBuilder text = new StringBuilder(oldText);
        for(int i = 0; i < text.length(); i++)
        {
            String spaces;
            char c = text.charAt(i);
            if (c == '\n'){
                int k = i + 1;
                char nextChar = text.charAt(k);
                while (Character.isWhitespace(nextChar)){
                    text.deleteCharAt(k);
                    nextChar = text.charAt(k);
                }
                spaces = addSpaces(nSpaces);
                text.insert(i+1, spaces);
            }
        }
        return text.toString();
    }
    
    private static String createAssignmentString(List<Assignment> lst, int nSpaces){
        String res = "";
        Assignment ass = lst.get(0);
        
        if(ass instanceof AtomicAssignment)
            res += ass.getSlot().get(0) + "=" + ((AtomicAssignment)ass).getValue();
        else
            res += ass.getSlot().get(0) + "+=" + ((AggregateAssignment)ass).getValue().get(0);
        
        for(int i = 1; i < lst.size(); i++)
        {
            ass = lst.get(i);
            if(ass instanceof AtomicAssignment)
                res += "; " + ass.getSlot().get(0) + "=" + ((AtomicAssignment)ass).getValue();
            else
                res += "; " + ass.getSlot().get(0) + "+=" + ((AggregateAssignment)ass).getValue().get(0);
        }
            
        return res;
    }
    
    private static String createTermsString(List<AstTermSubNode> lst, int nSpaces){
        String res = "";
        if(lst != null && lst.size() > 0){
            res += "\n" + addSpaces(nSpaces);
            
            res += "{terms:"; 
            
            for(int j = 0; j < lst.size(); j++)
            {
                res += "\n" + addSpaces(nSpaces + 2);
                res += "{" + lst.get(j).getTerm() + ":\n";
                res += addSpaces(nSpaces + 3);
                res += handleTextSpaces(lst.get(j).getExplanation(), nSpaces + 3) + "}";
            }
            res += "}";
        }
        return res;
    }
        
    private static String addSpaces(int nSpaces){
        String res = "";
        for(int i = 0; i < nSpaces; i++)
            res += " ";
        return res; 
    }
    
    private static String createAnswersString(List<AstAnswerSubNode> lst, int nSpaces){
        String res = "";
        res += "\n" + addSpaces(nSpaces);
        res += "{answers:";
        
        for(int i = 0; i < lst.size(); i++)
            res += "\n" + addSpaces(nSpaces+ 2) + "{" + handleTextSpaces(lst.get(i).getAnswerText(), nSpaces+ 3) + ":" + 
                    prettyPrinterToGraph(lst.get(i).getSubGraph(), nSpaces+4) + "}";
        
        res += "}";
            
        return res;
    }
    
    public void dgToPrint()
    {
        System.out.println(_dgPrettyPrinted);
    }
}
