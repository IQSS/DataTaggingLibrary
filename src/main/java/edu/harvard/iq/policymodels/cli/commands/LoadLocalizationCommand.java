package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.externaltexts.Localization;
import edu.harvard.iq.policymodels.externaltexts.LocalizationException;
import edu.harvard.iq.policymodels.externaltexts.LocalizationLoader;
import java.io.IOException;
import java.util.List;

/**
 * Loads a localization of the current model.
 * @author michael
 */
public class LoadLocalizationCommand extends AbstractCliCommand {

    public LoadLocalizationCommand() {
        super("loc-load", "Loads a localization.");
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        String localizationName;
        if ( args.size()==1 ) {
            localizationName = rnr.readLine("Localization name:");
        } else {
            localizationName = args.get(1);
        }
        
        LocalizationLoader ldr = new LocalizationLoader();
        
        try {
            Localization loc = ldr.load(rnr.getModel(), localizationName);

            rnr.printTitle(loc.getLanguage() + " Localization");
            rnr.println("Title: %s", loc.getLocalizedModelData().getTitle());
            rnr.println("Sub title: %s", loc.getLocalizedModelData().getSubTitle());
            rnr.println("Keywords: %s", loc.getLocalizedModelData().getKeywords());
            rnr.println("");
            rnr.println("Answers:");
            loc.getLocalizedAnswers().stream().sorted().forEach( ans -> rnr.println(" - %s: %s", ans, loc.localizeAnswer(ans)));
            rnr.println("");
            rnr.println("Localized node ids:");
            loc.getLocalizedNodeIds().stream().sorted().forEach( id -> rnr.println(" - %s", id));
            rnr.println("Localized Sections:");
            loc.getLocalizedSectionIds().stream().sorted().forEach( id -> rnr.println(" - %s", id));
            
        } catch ( IOException | LocalizationException exp ) {
            rnr.printWarning( exp.getMessage() );
        }
        
    }
    
}
