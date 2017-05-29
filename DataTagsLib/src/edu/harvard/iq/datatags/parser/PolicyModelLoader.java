package edu.harvard.iq.datatags.parser;

import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.PolicyModelData;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParseResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.DecisionGraphValidator;
import edu.harvard.iq.datatags.tools.DuplicateNodeAnswerValidator;
import edu.harvard.iq.datatags.tools.RepeatIdValidator;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.ValidCallNodeValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.tools.processors.YesNoAnswersSorter;
import java.io.IOException;
import java.util.logging.Logger;
import static edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.tools.processors.DecisionGraphProcessor;
import edu.harvard.iq.datatags.tools.processors.EndNodeOptimizer;
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
        res.add( new ValidCallNodeValidator() );
        
        return res;
    }
    
    /**
     * @return A loader with optimizations and little validations.
     */
    public static PolicyModelLoader productionLoader() {
        PolicyModelLoader res = new PolicyModelLoader();
        
        res.add( new DuplicateNodeAnswerValidator() );
        res.add( new RepeatIdValidator() );
        
        res.add( new ValidCallNodeValidator() );
        
        res.add( new EndNodeOptimizer() );
        
        return res;
    }
    
    public PolicyModelLoadResult load( PolicyModelData data ) throws DataTagsParseException {
        
        // Setup result
        PolicyModelLoadResult res = new PolicyModelLoadResult();
        PolicyModel model = new PolicyModel();
        model.setMetadata(data);
        res.setModel(model);
        
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
        DecisionGraph dg;
        
        try { 
            DecisionGraphParseResult decisionGraphParseRes = new DecisionGraphParser().parse(data.getDecisionGraphPath());
            res.setDecisionGraphAst(decisionGraphParseRes.getNodes());
            
            dgAstValidators.stream().flatMap( v -> v.validate(decisionGraphParseRes.getNodes()).stream())
                                    .forEach(res::addMessage);
            
            dg = decisionGraphParseRes.compile(spaceRoot);
            switch ( data.getAnswerTransformationMode() ) {
                case Verbatim: break;
                case YesFirst:
                    dg = new YesNoAnswersSorter(true).process(dg);
                    break;
                case YesLast:
                    dg = new YesNoAnswersSorter(false).process(dg);
                    break;
            }
            final DecisionGraph fdg = dg; // lets the lambdas below compile 
            dgValidators.stream().flatMap( v->v.validate(fdg).stream() ).forEach(res::addMessage);
            
            for ( DecisionGraphProcessor dgp : postProcessors ) {
                dg = dgp.process(dg);
            }
            
            model.setDecisionGraph(dg);
            
        } catch (IOException ex) {
            Logger.getLogger(PolicyModelLoader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
