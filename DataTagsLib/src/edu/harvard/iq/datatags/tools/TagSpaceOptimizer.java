package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An optimizer that makes the chart use a single end node, instead
 * of many. Only end nodes that don't have an ID are optimized.
 *
 * @author michael
 */
public class TagSpaceOptimizer implements FlowChartOptimizer {

    @Override
    public String getTitle() {
        return "Preceding nodes.";
    }

    @Override
    public DecisionGraph optimize(final DecisionGraph fcs) {

        int counter = 1;

        System.out.println("My Tag space optimizer");

//        // create the end node
//        final EndNode end = new EndNode("[#" + fcs.getId() + "-end]" );
//        fcs.add( end );

        // now traverse the chart and replace all.
        TagSpaceOptimizer.ConclusionVisitor traversor = new TagSpaceOptimizer.ConclusionVisitor() {

            @Override
            public Conclusion visitImpl(AskNode nd) throws DataTagsRuntimeException {
                List<Conclusion> conclusions = new ArrayList<Conclusion>();

                System.out.println("AskNode: " + nd.getId());

                /* 1. Collect conclusions from children */
                for ( Answer a : nd.getAnswers() ) {
                    Node ansNode = nd.getNodeFor(a);

                    /* TODO: Is it possible? that no conclusion made? (yes..) */
                    Conclusion c = ansNode.accept(this);
                    if (c != null) {
                        c.relatedAnswer = a;
                        conclusions.add(c);
                    }
//                    nd.setNodeFor(a, end);
                }

                /* 2. Values must be added */
                List<CompoundValue> allValuesList = new ArrayList<CompoundValue>();

                /* Collect values to lists */
                for (Conclusion c : conclusions) {
                    CompoundValue union = null;

                    if (c.values != null && c.mustAdd != null) {
                        // TODO: Smart compose is needed

                        union = c.values.composeWith(c.mustAdd);
                    }

                    if (c.values != null) {
                        union = c.values;
                    }

                    if (c.mustAdd != null) {
                        union = c.mustAdd;
                    }

                    allValuesList.add(union);
                }

                /* Evaluate values for different cases */
                CompoundValue sharedValues = intersectValues(allValuesList);

                System.out.println("\n************************************************");
                System.out.println("* {ask} " + nd.getId() + " MustAdd: " + sharedValues);
                System.out.println("* {ask} " + nd.getId() + " Allvalues: " + allValuesList);
                System.out.println("************************************************\n");



                /**
                 * 1. Calculate All Shared values (including must values)
                 * 2. Must values minus <All Shared Values> should be added to the apropriate place
                 */
//                /* Handle 'insert' cases */
//                /* For each Answer (e.g. conclusion) */
//                for (Conclusion c : conclusions) {
//                    if (c.mustAdd == null) {
//                        continue;
//                    }
//
//                    /* Substract populate values */
//                    CompoundValue newValue = c.mustAdd.substractKeys(sharedValues);
//
//                    SetNode setNode = new SetNode("[#" + getNewId() + "-optimizer]", newValue);
//                    setNode.setNextNode(nd.getNodeFor(c.relatedAnswer));
//                    nd.setNodeFor(c.relatedAnswer, setNode);
//                }
//
                /* Remove Key-Values(shared values) from direct children of values that will be populated up as 'must' */
                for (Conclusion c : conclusions) {
                    System.out.println("%%%%% Looking at answer: " + c.relatedAnswer.getAnswerText());
                    Node answerNode = nd.getNodeFor(c.relatedAnswer);

                    /* answer is SetNode */
                    if (answerNode instanceof SetNode) {
                        SetNode answerSetNode = (SetNode) answerNode;

                        CompoundValue newAnswerValues = answerSetNode.getTags().getOwnableInstance();
                        System.out.println("[" + c.relatedAnswer.getAnswerText() + "] TAGS: " + newAnswerValues);
                        newAnswerValues = newAnswerValues.substractKeys(sharedValues);
                        System.out.println("[" + c.relatedAnswer.getAnswerText() + "] BUGS: " + newAnswerValues);

                        /* If new SetNode should be empty */
                        if (null == newAnswerValues) {
                            System.out.println("[" + c.relatedAnswer.getAnswerText() + "] Removing node");
                            /* Just remove the node */
                            nd.setNodeFor(c.relatedAnswer, answerSetNode.getNextNode());
                            fcs.remove(answerSetNode);
                        }

                        else {
                            /* Create new node */
                            SetNode newAnswerSetNode = new SetNode("[#" + getNewId() + "-Zoptimizer]", newAnswerValues);
                            newAnswerSetNode.setNextNode(answerSetNode.getNextNode());

                            /* Replace old node */
                            nd.setNodeFor(c.relatedAnswer, newAnswerSetNode);
                            fcs.remove(answerSetNode);
                        }
                    }

                    else {

                        // See if there are values to be added
                        if (c.mustAdd == null) {
                            continue;
                        }

                        // If so - make sure we are not populating them to the parent
                        CompoundValue valuesToBeAdded = c.mustAdd.substractKeys(sharedValues);
                        if (valuesToBeAdded == null) {
                            continue;
                        }
                        System.out.println("** Values to be added : " + valuesToBeAdded.toString());
                        // Create new node
                        SetNode newAnswerSetNode = new SetNode("[#" + getNewId() + "-Zoptimizer]", valuesToBeAdded);
                        newAnswerSetNode.setNextNode(answerNode);

                        /* Replace old node */
                        nd.setNodeFor(c.relatedAnswer, newAnswerSetNode);
                        fcs.remove(answerNode);

                    }

                }

                Conclusion conclusion = new Conclusion(nd.getId(), null, sharedValues, null);
                return conclusion;
            }

            private CompoundValue intersectValues(List<CompoundValue> values) {
                boolean first = true;
                CompoundValue result = null;

                for (CompoundValue v : values) {
                    if (first) {
                        result = v;
                        first = false;
                    }

                    result = result.intersectWith(v);
                }

                return result;
            }

            private CompoundValue unionValues(List<CompoundValue> values) {
                boolean first = true;
                CompoundValue result = null;

                for (CompoundValue v : values) {
                    if (first) {
                        result = v;
                        first = false;
                    }

                    result = result.composeWith(v);
                }

                return result;
            }

            @Override
            public Conclusion visitImpl(SetNode nd) throws DataTagsRuntimeException {
                CompoundValue retvalMust = null;
//                System.out.println("### SET NODE ### " + nd.getTags().toString());

                // 1. Optmize sub-tree

                // 2. If new SetNode is the sub tree

                // 3. Merge with current node

                // 4. Then return as usual

                Node nextNode = nd.getNextNode();
                Conclusion childConclusion = nextNode.accept(this);
                if (childConclusion != null) {
                    retvalMust = childConclusion.mustAdd;
                }

//                /* If found conclusion from child */
//                if (c != null) {
//                    if (c.values != null) {
//                        System.out.println("!!!!!!! Values: " + c.values);
//                    }
//
//                    if (c.mustAdd != null) {
//                        // newValue is the values of the node +
//                        //    1. All the new keys from MustAdd
//                        //    2. If keys were in both - value in 'mustAdd' is what counts
//                        CompoundValue newValue = nd.getTags().composeWith(c.mustAdd);
////                        SetNode setNode = new SetNode("[#" + getNewId() + "-optimizer-shared]", newValue);
////                        setNode.setNextNode(nd.getNodeFor(c.relatedAnswer));
////                        nd.setNodeFor(c.relatedAnswer, setNode);
//
//
//                        // Reduce current node values from 'mustAdd' values
//                        // If a value must be set - and it is already happening here - no need to return it
//                        CompoundValue shared = c.mustAdd.intersectWith(nd.getTags());
//                        CompoundValue remainedMust = c.mustAdd.substractKeys(shared);
//                        System.out.println("!!!!!!!!!!!! Subtracted keys: " + remainedMust);
//                        retvalMust = remainedMust;
//                    }
//                }

                // TODO: Visit nd.getNextNode()
//                visitThroughNode(nd);
                Conclusion conclusion = new Conclusion(nd.getId(), nd.getTags(), retvalMust, null);
                return conclusion;
            }


            @Override
            public Conclusion visitImpl(CallNode nd) throws DataTagsRuntimeException {
                //visitThroughNode(nd);
                return null;
            }

            @Override
            public Conclusion visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                //visitThroughNode(nd);
                return null;
            }

            @Override
            public Conclusion visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                return null;
            }
            @Override
            public Conclusion visitImpl(EndNode nd) throws DataTagsRuntimeException {
                return null;
            }

            private void visitThroughNode( ThroughNode nd) {
//                if ( shouldReplace(nd.getNextNode()) ) {
//                    fcs.remove(nd.getNextNode());
//                    nd.setNextNode( end );
//                }
            }

            private boolean shouldReplace( Node n ) {
                return ( (n.getId().startsWith("[#"))
                        && ( n instanceof EndNode ) );
            }
        };

        // We need to hold the node list to avoid concurrent modification errors.
        Set<String> nodeIds = new TreeSet<>();

        System.out.println(">> Start node id: " + fcs.getStart().getId());
        System.out.println(">> Start node: " + fcs.getStart().toString());

        Node startNode = fcs.getStart();

        /* Traverse from head */
        Conclusion finalConclusion = startNode.accept(traversor);

        /* Add last conclusion */
        if (finalConclusion.mustAdd != null) {
                SetNode setNode = new SetNode("[#" + fcs.getId() + "-setoptX]", finalConclusion.mustAdd.getOwnableInstance());
            setNode.setNextNode(startNode);
            fcs.setStart(setNode);
        }

        return fcs;
    }

    /**
     * Recursive Information Struct
     */
    private static class Conclusion {
        public String nodeId;
        public CompoundValue values;
        public CompoundValue mustAdd;
        public Answer relatedAnswer;  /* if applicable */

        public Conclusion(String nodeId, CompoundValue values, CompoundValue mustAdd, Answer relatedAnswer) {
            this.nodeId = nodeId;
            this.values = values;
            this.mustAdd = mustAdd;
            this.relatedAnswer = relatedAnswer;
        }
    }

    /**
     * CompoundValue visitor allows us to get 'compound' set values from child
     */

    public static abstract class ConclusionVisitor implements Node.Visitor<Conclusion> {

        private int counter;

        public ConclusionVisitor() {
            System.out.println("$$$$$$$$$$$$$$$$$$$$");
            counter = 0;
        }

        public String getNewId() {
            counter++;
            return Integer.toString(counter);
        }

        @Override
        public Conclusion visit(AskNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        @Override
        public Conclusion visit(SetNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        @Override
        public Conclusion visit(RejectNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        @Override
        public Conclusion visit(CallNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        @Override
        public Conclusion visit(TodoNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        @Override
        public Conclusion visit(EndNode nd) throws DataTagsRuntimeException {
            return visitImpl(nd);
        }

        public abstract Conclusion visitImpl( AskNode nd    ) throws DataTagsRuntimeException;
        public abstract Conclusion visitImpl( SetNode nd    ) throws DataTagsRuntimeException;
        public abstract Conclusion visitImpl( RejectNode nd ) throws DataTagsRuntimeException;
        public abstract Conclusion visitImpl( CallNode nd   ) throws DataTagsRuntimeException;
        public abstract Conclusion visitImpl( TodoNode nd   ) throws DataTagsRuntimeException;
        public abstract Conclusion visitImpl( EndNode nd    ) throws DataTagsRuntimeException;

    }
}
