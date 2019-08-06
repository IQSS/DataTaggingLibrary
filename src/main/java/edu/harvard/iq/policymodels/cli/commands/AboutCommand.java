package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.model.PolicyModel;
import edu.harvard.iq.policymodels.model.metadata.AuthorData;
import edu.harvard.iq.policymodels.model.metadata.GroupAuthorData;
import edu.harvard.iq.policymodels.model.metadata.PersonAuthorData;
import edu.harvard.iq.policymodels.model.metadata.PolicyModelData;
import static edu.harvard.iq.policymodels.util.StringHelper.nonEmpty;
import java.util.List;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author michael
 */
public class AboutCommand implements CliCommand {

    @Override
    public String command() {
        return "about";
    }

    @Override
    public String description() {
        return "What's this application all about.";
    }
    
    @Override
    public boolean requiresModel() {
        return false;
    }
    
    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.println( CliRunner.LOGO );
        rnr.println("");
        rnr.printTitle("About PolicyModels CliRunner");
        rnr.println("This application allows working with policy models from the a command line.");
        rnr.println("");
        rnr.println("For more info: http://datatags.org");
        rnr.println("Engine status: %s", rnr.getEngine().getStatus());
        rnr.println("");
        
        PolicyModel model = rnr.getEngine().getModel();
        if ( model != null ) {
            rnr.printTitle("Current Model");
            PolicyModelData metadata = model.getMetadata();
            rnr.println("title:\t%s", metadata.getTitle() );
            if ( nonEmpty(metadata.getSubTitle()) ) {
                rnr.println("      \t %s", metadata.getSubTitle());
            }
            rnr.println("keywords:\t%s", metadata.getKeywords().stream().collect(joining("\", \"", "\"", "\"")));
            rnr.println("version:\t%s", metadata.getVersion() );
            rnr.println("authors:");
            
            rnr.println("Space root:\t%s", rnr.getModel().getSpaceRoot().getName());
            
            AuthorVisitor prt = new AuthorVisitor(rnr);
            metadata.getAuthors().forEach(e->e.accept(prt));
            
            if ( ! model.getLocalizations().isEmpty() ) {
                rnr.println("Localizations:");
                model.getLocalizations().forEach(lName -> rnr.println("* " + lName));
            }
            
            rnr.println( metadata.getBestReadmeFormat()
                                 .map( fmt -> "readme: " + fmt)
                                 .orElse("No readme file")
            );
            
            rnr.println("location:\n %s", metadata.getMetadataFile().getParent().toAbsolutePath() );
        }
        
    }
    
}

class AuthorVisitor implements AuthorData.Visitor {
    private final CliRunner rnr;

    public AuthorVisitor(CliRunner rnr) {
        this.rnr = rnr;
    }
    
    @Override
    public Object visit(PersonAuthorData p) {
        rnr.println( "* " + p.getName() 
                          + (nonEmpty(p.getAffiliation()) ? ", " + p.getAffiliation() + "." : "")
                          + (nonEmpty(p.getOrcid()) ? " ORCiD:" + p.getOrcid() : "") );
        if ( nonEmpty(p.getEmail()) ) {
            rnr.println( "   " + p.getEmail() );
        }
        if ( nonEmpty(p.getUrl()) ) {
            rnr.println( "   " + p.getUrl() );
        }
        return null;
    }

    @Override
    public Object visit(GroupAuthorData g) {
        rnr.println( "* " + g.getName() );
        if ( nonEmpty(g.getContact()) ) {
            rnr.println( "   contact: " + g.getContact() );
        }
        if ( nonEmpty(g.getUrl()) ) {
            rnr.println( "   " + g.getUrl() );
        }
        return null;
    }
    
}