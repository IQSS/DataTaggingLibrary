package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.externaltexts.LocalizationLoader;
import static edu.harvard.iq.datatags.io.FileUtils.ciResolve;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.DecisionGraphValidator;
import edu.harvard.iq.datatags.tools.DuplicateNodeAnswerValidator;
import edu.harvard.iq.datatags.tools.RepeatIdValidator;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.tools.processors.YesNoAnswersSorter;
import java.io.IOException;
import static edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.tools.processors.DecisionGraphProcessor;
import edu.harvard.iq.datatags.tools.processors.EndNodeOptimizer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads policy models from {@link PolicyModelData}. Each loader has a list of
 * validators and post-processors that is runs in the appropriate places.
 * 
 * Use the class' statis methods to obtain instances that fit certain contexts 
 * (e.g. dev, production).
 * 
 * @author michael
 */
public class PolicyModelLoader {
    
    private final List<DecisionGraphAstValidator> dgAstValidators = new ArrayList<>();
    private final List<DecisionGraphValidator> dgValidators = new ArrayList<>();
    private final List<DecisionGraphProcessor> postProcessors = new ArrayList<>();
    
    /**
     * @return A loader with all validations and no post-processing.
     */
    public static PolicyModelLoader verboseLoader() {
        PolicyModelLoader res = new PolicyModelLoader();
        
        res.add( new DuplicateNodeAnswerValidator() );
        res.add( new RepeatIdValidator() );
        
        res.add( new UnreachableNodeValidator() );
        
        return res;
    }
    
    /**
     * @return A loader with optimizations and little validations.
     */
    public static PolicyModelLoader productionLoader() {
        PolicyModelLoader res = new PolicyModelLoader();
        
        res.add( new DuplicateNodeAnswerValidator() );
        res.add( new RepeatIdValidator() );
        
        
        res.add( new EndNodeOptimizer() );
        
        return res;
    }
    
    public PolicyModelLoadResult load( PolicyModelData data ) {
        // Setup result
        PolicyModelLoadResult res = new PolicyModelLoadResult();
        PolicyModel model = new PolicyModel();
        model.setMetadata(data);
        res.setModel(model);
        
        try {
            // Load space root.
            CompoundSlot spaceRoot = null;
            try {
                TagSpaceParseResult spaceParseRes = new TagSpaceParser().parse(data.getPolicySpacePath());
                spaceRoot = spaceParseRes.buildType(data.getRootTypeName()).orElse(null);
                if ( spaceRoot == null ) {
                    res.addMessage( new ValidationMessage(Level.ERROR, "Type '" + data.getRootTypeName() + "', used as policy space root, is not defined. ") );
                    return res;
                }
                model.setSpaceRoot(spaceRoot);
                
            } catch (IOException ex) {
                res.addMessage( new ValidationMessage(Level.ERROR, "Cannot load policy space: " + ex.getMessage()));
                
            } catch (SyntaxErrorException ex) {
                res.addMessage( new ValidationMessage(Level.ERROR, "Syntax error in policy space: " + ex.getMessage()));
                
            } catch (SemanticsErrorException ex) {
                res.addMessage( new ValidationMessage(Level.ERROR, "Semantic error in policy space: " + ex.getMessage()));
            }
            if ( spaceRoot == null ) return res;
            
            // load decision graph
            DecisionGraphCompiler decisionGraphCompiler = new DecisionGraphCompiler();
            DecisionGraph dg = decisionGraphCompiler.compile(spaceRoot, data, dgAstValidators);
            decisionGraphCompiler.getMessages().forEach(res::addMessage);
            
            if ( dg != null ) {
                switch ( data.getAnswerTransformationMode() ) {
                    case Verbatim: break;
                    case YesFirst:
                        dg = new YesNoAnswersSorter(true).process(dg);
                        break;
                    case YesLast:
                        dg = new YesNoAnswersSorter(false).process(dg);
                        break;
                }
                final DecisionGraph fdg = dg; // let the lambdas below compile
                dgValidators.stream().flatMap( v->v.validate(fdg).stream() ).forEach(res::addMessage);
                for ( DecisionGraphProcessor dgp : postProcessors ) {
                    dg = dgp.process(dg);
                }
                model.setDecisionGraph(dg);
                
            } else {
                res.addMessage( new ValidationMessage(Level.ERROR, "Failed to create decision graph; see previous errors.") );
            }
            
            // Load localizations
            Path localizations;
            try {
                localizations = ciResolve(data.getMetadataFile().getParent(), LocalizationLoader.LOCALIZATION_DIRECTORY_NAME);
                if ( localizations != null ) {
                    Files.list(localizations).filter(Files::isDirectory)
                                             .map(p->p.getFileName().toString())
                                             .forEach(res.getModel()::addLocalization);
                }
            } catch (IOException ex) {
                res.addMessage( new ValidationMessage(Level.WARNING, "IO Error reading localizations: " + ex.getMessage()));
            }
            
        } catch (NoSuchFileException ex) {
            res.addMessage( new ValidationMessage(Level.ERROR, "File " + ex.getMessage() + " cannot be found."));
        } catch (IOException ex) {
            res.addMessage( new ValidationMessage(Level.ERROR, "IO error while reading graph: " + ex.getMessage()));
        }
        
        return res;
    }
    
    public void add( DecisionGraphAstValidator vld ) {
        dgAstValidators.add(vld);
    }
    
    public void add( DecisionGraphValidator vld ) {
        dgValidators.add(vld);
    }
    
    public void add( DecisionGraphProcessor prc ) {
        postProcessors.add(prc);
    }
}
