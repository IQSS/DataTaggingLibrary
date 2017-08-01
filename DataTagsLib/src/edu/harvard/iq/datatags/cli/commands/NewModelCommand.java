package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.metadata.AuthorData;
import edu.harvard.iq.datatags.model.metadata.GroupAuthorData;
import edu.harvard.iq.datatags.model.metadata.PersonAuthorData;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static edu.harvard.iq.datatags.util.StringHelper.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import static java.util.stream.Collectors.toList;

/**
 * Creates a new model, based on a possible interview.
 * @author michael
 */
public class NewModelCommand extends AbstractCliCommand {
    
    private PolicyModelData data;
    
    private final Pattern fieldDetector = Pattern.compile("\\$\\{[_A-Z]*\\}");
    
    public NewModelCommand() {
        super(  "new", 
                "Create a new policy model. Usage: new [-q] [path-to-model-folder]\n"
              + "-q: quick creation, no questions asked. Model created in current folder.");
    }
    
    @Override
    public boolean requiresModel() {
        return false;
    }
    
    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        
        rnr.printTitle("Creating New Model");
        
        // interview the user or generate a basic model.
        data = new PolicyModelData();
        data.setReleaseDate(LocalDate.now());
        
        List<String> noFlags = noFlags(args);
        
        if ( noFlags.size()>1 ) {
            Path pathToFolder = Paths.get(C.last(noFlags));
            data.setMetadataFile(pathToFolder.resolve("policy-model.xml"));
            data.setTitle( C.last(noFlags) );
        }
        
        if ( hasFlag("q", args) ) {
            autoFillModel();
        } else {
            interactiveFillModel(rnr);
        }

        rnr.print("Creating model at " + data.getMetadataFile().getParent().toAbsolutePath() + "...");
        createModel();
        rnr.println("...Done\n");
        new LoadPolicyModelCommand().execute(rnr, Arrays.asList("dummy", data.getMetadataFile().toString()));
        
    }
    
    private void createModel() throws IOException {
        // read the template
        try ( InputStream rIn=getClass().getResourceAsStream("policy-model-template.xml");
              BufferedReader rdr = new BufferedReader(new InputStreamReader(rIn)) 
        ) {
            List<String> xmlTemplate = new ArrayList<>(50);  
            String line;
            while ( (line=rdr.readLine()) != null ) {
                xmlTemplate.add(line);
            }
            
            // process the template
            Stream<String> processedLines = xmlTemplate.stream().flatMap(s->processSingleLine(s).stream());

            // create files.
            Files.createDirectory(data.getMetadataFile().getParent());
            Files.write(data.getMetadataFile(), processedLines.collect(toList()), StandardCharsets.UTF_8);
            
            Files.write(data.getDecisionGraphPath(), Arrays.asList("[todo: create decision graph]"), StandardCharsets.UTF_8);
            
            Files.write(data.getPolicySpacePath(), 
                        Arrays.asList( data.getRootTypeName() + ": consists of A, B.",
                                       "A: TODO.",
                                       "B: TODO."), StandardCharsets.UTF_8);
            
        }  
        
    }
    
    List<String> processSingleLine(String in) {
        String out = in;
        boolean go = true;
        
        while ( go ) {
            Matcher matcher = fieldDetector.matcher(out);
            if ( matcher.find() ) {
                String group = matcher.group();
                String fieldName = group.substring(2,group.length()-1);
                
                switch ( fieldName ) {
                    case "TITLE": 
                        out = out.replace(group, data.getTitle() );
                        break;
                    case "DATE":
                        DateTimeFormatter fmt = new DateTimeFormatterBuilder().appendPattern("YYYY-MM-dd").toFormatter();
                        out = out.replace(group, data.getReleaseDate().format(fmt));
                        break;
                    case "DECISION_GRAPH":
                        out = out.replace(group, data.getDecisionGraphPath().getFileName().toString());
                        break;
                    case "TAG_SPACE":
                        out = out.replace(group, data.getPolicySpacePath().getFileName().toString());
                        break;
                    case "BASE_SLOT":
                        out = out.replace(group, data.getRootTypeName());
                        break;
                    case "AUTHORS":
                        AuthorToXml a2x = new AuthorToXml(matcher.start());
                        List<String> outList = new ArrayList<>();
                        data.getAuthors().forEach( ad -> 
                            outList.addAll(ad.accept(a2x)) );
                        return outList;
                }
                
            } else {
                go = false;
            }
        }
        return Collections.singletonList(out);
    }

    private void interactiveFillModel(CliRunner rnr) throws IOException {
        data.setTitle(rnr.readLineWithDefault("Model title: ", "Policy Model"));
        
        Path modelFolder = Paths.get("PolicyModel");
        int i=1;
        while ( Files.exists(modelFolder) ) {
            modelFolder = Paths.get("PolicyModel-" + i);
            i++;
        }
        data.setMetadataFile( modelFolder.resolve("policy-model.xml") );
        String ans = rnr.readLineWithDefault("Decision graph filename: ", "decision-graph.dg");
        data.setDecisionGraphPath( data.getMetadataFile().resolveSibling(ans) );
        
        ans = rnr.readLineWithDefault("Policy space filename: ", "policy-space.ps");
        data.setPolicySpacePath( data.getMetadataFile().resolveSibling(ans) );
        
        ans = rnr.readLineWithDefault("Root slot name (Only letters, no spaces):", "DataTags");
        data.setRootTypeName(ans);
        
        boolean go=true;
        while (go) {
            ans = rnr.readLineWithDefault("Add author? (y/n): ", "y");
            if ( ans.equals("n") ) {
                go = false;
            } else if ( ans.equals("y") ) {
                boolean go2 = true;
                while (go2) {
                    ans = rnr.readLineWithDefault("Person or Group? (p/g): ", "p");
                    if ( ans.equals("p") ) {
                        PersonAuthorData pad = new PersonAuthorData();
                        pad.setName( rnr.readLine("Name: ") );
                        pad.setAffiliation( rnr.readLine("Affiliation: ") );
                        pad.setEmail(rnr.readLine("email: ") );
                        data.add(pad);
                        go2=false;
                        
                    } else if ( ans.equals("g") ) {
                        GroupAuthorData gad = new GroupAuthorData();
                        gad.setName( rnr.readLine("Name: ") );
                        gad.setContact( rnr.readLine("Contact: ") );
                        data.add(gad);
                        go2=false;
                        
                    }
                }
            }
        }
        
    }

    private void autoFillModel() {
        PersonAuthorData pad = new PersonAuthorData();
        pad.setName( System.getProperty("user.name") );
        data.add(pad);
        if ( data.getMetadataFile() == null ) {
            Path modelFolder = Paths.get("PolicyModel");
            int i=1;
            while ( Files.exists(modelFolder) ) {
                modelFolder = Paths.get("PolicyModel-" + i);
                i++;
            }
            data.setMetadataFile(modelFolder.resolve("policy-model.xml") );
            data.setTitle("Policy Model");
        } else {
            data.setTitle( data.getMetadataFile().getParent().getFileName().toString() );
        }
        data.setDecisionGraphPath( data.getMetadataFile().resolveSibling("decision-graph.dg") );
        data.setPolicySpacePath( data.getMetadataFile().resolveSibling("policy-space.ps") );
        data.setRootTypeName("DataTags");
    }
}

class AuthorToXml implements AuthorData.Visitor<List<String>> {
    
    private final String prefix;
    
    AuthorToXml( int offset ) {
        char[] arr = new char[offset];
        Arrays.fill(arr, ' ');
        prefix = new String(arr);
    }
    
    @Override
    public List<String> visit(PersonAuthorData p) {
        List<String> res = new ArrayList<>();
        if ( nonEmpty(p.getOrcid()) ) {
            res.add( prefix +"<person orcid=\"" + p.getOrcid() + "\">" );
        } else {
            res.add( prefix +"<person>" );
        }
                
        res.add( prefix +"  <name>" + p.getName() + "</name>" );
        if ( nonEmpty(p.getAffiliation()) ) {
            res.add( prefix +"  <affiliation>" + p.getAffiliation() + "</affiliation>" );
        }
        if ( nonEmpty(p.getEmail()) ) {
            res.add( prefix +"  <email>" + p.getEmail()+ "</email>" );
        }
        res.add( prefix +"</person>" );
                
        return res;
    }

    @Override
    public List<String> visit(GroupAuthorData g) {
        List<String> res = new ArrayList<>();
        res.add( prefix + "<group>");
        res.add( prefix +"  <name>" + g.getName() + "</name>" );
        res.add( prefix +"  <contact>" + g.getContact() + "</contact>" );
        
        res.add( prefix + "</group>");
        
        return res;
    }

    
    
}