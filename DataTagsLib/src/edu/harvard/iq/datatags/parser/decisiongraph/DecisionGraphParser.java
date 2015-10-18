package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.FlowChartSet;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.Answer;
import static edu.harvard.iq.datatags.model.values.Answer.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.parser.definitions.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.error.ParserException;

/**
 * Takes a string, returns a decision graph, or the AST, in case you're writing
 * some code tool.
 * 
 * @author michael
 */
public class DecisionGraphParser {
	
	private final Map<String, AstNode> id2NodeRef = new HashMap<>();
	private final Map<String, List<CallNode>> callNodesToLink = new HashMap<>();
	
    /**
     * The base type of the data tags (typically, "DataTags").
     * While constructing {@link SetNode}s, this type is used to build node's
     * {@link DataTags} object.
     */
    private final CompoundType topLevelType;
    
    private final Map<TagType, TagType> parentType = new HashMap<>();
    
    public DecisionGraphParser(CompoundType baseType) {
        this.topLevelType = baseType;
    }
    
    
	public DecisionGraphParseResult parse( String source ) throws DataTagsParseException { 
		Parser<List<? extends AstNode>> parser = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.graphParser() );
        try {
            List<? extends AstNode> astNodeList = parser.parse(source);
            // TODO add AST-level validators here. Add results to parse result.
            return new DecisionGraphParseResult(astNodeList);
            
        } catch ( ParserException pe ) {
            throw new DataTagsParseException(new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
                    "Error parsing decision graph code: " + pe.getMessage(), pe);
        }
	}
	
	public FlowChartSet parse( List<? extends AstNode> parsedNodes, String unitName ) throws BadSetInstructionException { 
		// TODO implement the namespace, use unit name.
        
        buildParentTypeRelations();
		
		// Map node refs ids to the node refs.
		initIds( parsedNodes );
		
		FlowChartSet chartSet = new FlowChartSet();
        chartSet.setTopLevelType(topLevelType);
        
		DecisionGraph chart = new DecisionGraph( unitName + "-c1" );
		chartSet.addFlowChart(chart);
		chartSet.setDefaultChartId( chart.getId() );
        try {
            for ( List<AstNode> nodes : breakList(parsedNodes) ) {
                Node startNode = buildNodes( nodes, chart, new EndNode("$"+chart.getId()) );
                if ( chart.getStart()== null ) { 
                    chart.setStart(startNode);
                }
            }
        } catch ( RuntimeException rte ) {
            Throwable cause = rte.getCause();
            if ( cause != null && cause instanceof BadSetInstructionException ) {
                throw (BadSetInstructionException)cause;
            } else { 
                throw rte;
            }
        }
		
		return chartSet;
	}
	
	/**
	 * Break a list of instruction nodes to reachable components. 
	 * The list generated by the AST parser contains connections that do not make semantic
	 * sense - namely, EndNodeRef's next pointer. We break the list on those 
	 * @param parsed
	 * @return 
	 */
	List<List<AstNode>> breakList( List<? extends AstNode> parsed ) {
		List<List<AstNode>> res = new LinkedList<>();
		List<AstNode> cur = new LinkedList<>();
		
		for ( AstNode node : parsed ) {
			cur.add( node );
			if ( node instanceof AstEndNode ) {
				res.add( cur );
				cur = new LinkedList<>();
			}
		}
		res.add( cur );
		
		return res;
	}
	
	/**
	 * Builds nodes from parsed node references
	 * @param nodes the parsed node reference list,
	 * @param chart The chart being built (nodes are added to it)
	 * @param defaultNode the node to go to when the chart ends. In effect, when a 
	 *		              strand of nodes ends in a non-terminating reference, this node
	 *					  is returned.
	 * @return the node at the root of the execution path.
	 */
	private Node buildNodes( final List<? extends AstNode> nodes, final DecisionGraph chart, final Node defaultNode ) {
		
		AstNode.Visitor<Node> builder = new AstNode.Visitor<Node>(){
            int nextEndId = 0;
			@Override
			public Node visit(AstAskNode askRef) {
				AskNode res = new AskNode( askRef.getId() );
				
				res.setText( askRef.getTextNode().getText() );
				for ( AstTermSubNode termRef : askRef.getTerms() ) {
					res.addTerm(termRef.getTerm(), termRef.getExplanation() );
				}
				
				Node syntacticallyNext = buildNodes(C.tail(nodes), chart, defaultNode );
				
				for ( AstAnswerSubNode ansRef : askRef.getAnswers() ) {
					res.setNodeFor( Answer(ansRef.getAnswerText()), 
								    buildNodes(ansRef.getSubGraph(), chart, syntacticallyNext)
								  );
				}
				
				// if the question is implied binary, we add implied answers.
				for ( Answer ans : impliedAnswers(res) ) {
					res.setNodeFor(ans, syntacticallyNext);
				} 
				
				return chart.add( res );
			}

			@Override
			public Node visit(AstCallNode callRef) {
				CallNode res = new CallNode( callRef.getId() );
				res.setCalleeNodeId( callRef.getCalleeId() );
				if ( ! callNodesToLink.containsKey(res.getCalleeNodeId()) ) {
					callNodesToLink.put( res.getCalleeNodeId(), new LinkedList<CallNode>() );
				}
				callNodesToLink.get(res.getCalleeNodeId()).add( res );
				res.setNextNode( buildNodes(C.tail(nodes), chart, defaultNode) );
				return chart.add( res );
			}

			@Override
			public Node visit(AstEndNode endRef) {
                String id = (endRef.getId() != null)  ? endRef.getId() : "end_$" + (nextEndId++);
				return chart.add(new EndNode( id ));
			}	

            @Override
            public Node visit(AstRejectNode setRef) {
                return chart.add(new RejectNode( setRef.getId(), setRef.getReason() ));
            }
            
			@Override
			public Node visit(AstSetNode setRef) {
//                try {
//                    SetNode res = new SetNode( buildDataTags(setRef), setRef.getId() );
//                    res.setNextNode(buildNodes( C.tail(nodes), chart, defaultNode));
//                    return chart.add( res );
//                } catch (BadSetInstructionException ex) {
//                    throw new RuntimeException("Bad Set", ex );
//                }
                throw new UnsupportedOperationException();
			}

			@Override
			public Node visit(AstTodoNode todoRef) {
				TodoNode res = new TodoNode( todoRef.getId(), todoRef.getTodoText() );
				res.setNextNode( buildNodes( C.tail(nodes), chart, defaultNode) );
				return chart.add(res);
			}
		};
		
        // TODO this may not be needed. Maybe empty list at this point is a syntax error.
		return nodes.isEmpty() ? defaultNode : C.head( nodes ).accept(builder);
	}
	
	CompoundValue buildDataTags( AstSetNode nodeRef ) throws BadSetInstructionException {
		CompoundValue topLeveltypeInstance = topLevelType.createInstance();
        
//        for ( String slotName : nodeRef.getSlotNames() ) {
//            TagValueLookupResult slr = topLevelType.lookupValue( slotName, nodeRef.getValue(slotName));
//            
//            try {
//                slr.accept( setOrFail(topLeveltypeInstance, slr) );
//            } catch ( BadSetInstructionException ble ) {
//                throw new BadSetInstructionException(ble.getBadResult(), nodeRef);
//            }
//        }
        
		return topLeveltypeInstance;
	}

    /**
     * Return an object that, for successful matches, builds the compound value to 
     * contain the matched value. For failures - throws an error.
     * @param topLevelInstance the value that's added to
     * @param lookupResult the result of the value lookup
     * @return Object building {@code topLevelInstance}.
     */
    private TagValueLookupResult.SuccessFailVisitor<CompoundValue, BadSetInstructionException> 
    setOrFail(final CompoundValue topLevelInstance, final TagValueLookupResult lookupResult) {
        return new TagValueLookupResult.SuccessFailVisitor<CompoundValue, BadSetInstructionException>() {
            @Override
            public CompoundValue visitSuccess(TagValueLookupResult.Success s) throws BadSetInstructionException {
                buildValue(topLevelInstance, C.tail(buildTypePath(s.getValue())), s.getValue());
                
                return topLevelInstance;
            }
            
            @Override
            public CompoundValue visitFailure(TagValueLookupResult s) throws BadSetInstructionException {
                throw new BadSetInstructionException(lookupResult, null);
        }};
    }
    
    void buildValue( final CompoundValue value, final List<TagType> path, final TagValue endValue ) {
        C.head(path).accept(new TagType.VoidVisitor() {

            @Override
            public void visitAtomicTypeImpl(AtomicType t) {
                value.set(endValue);
            }

            @Override
            public void visitAggregateTypeImpl(AggregateType t) {
                if ( value.get(t) == null ) {
                    value.set( t.createInstance() );
                }
                ((AggregateValue)value.get(t)).add( ((AggregateValue)endValue).getValues() );
            }

            @Override
            public void visitCompoundTypeImpl(CompoundType t) {
                if ( value.get(t) == null ) {
                    value.set( t.createInstance() );
                }
                buildValue( (CompoundValue)value.get(t), C.tail(path), endValue);
            }

            @Override
            public void visitTodoTypeImpl(ToDoType t) {
                value.set(endValue);
            }
        });
    }
	
	List<Answer> impliedAnswers( AskNode node ) {
		Set<Answer> answers = node.getAnswers();
		if ( answers.size() > 1 ) return Collections.emptyList();
		if ( answers.isEmpty() ) return Arrays.asList( Answer.NO, Answer.YES ); // special case, where both YES and NO lead to the same options. 
		// MAYBE issue a warning/suggestion to make this a (todo: ) node.
		Answer onlyAns = answers.iterator().next();

        String ansText = onlyAns.getAnswerText().trim().toLowerCase();
		switch( ansText ) {
			case "yes": return Collections.singletonList( Answer.NO );
			case "no" : return Collections.singletonList( Answer.YES );
			default: return Collections.emptyList();	
		}
	}
    
	/**
	 * Inits all the ids of the nodes in the list. Also, collects the 
	 * user-assigned ids in {@link #id2NodeRef}.
	 * @param nodeRefs 
	 */
	protected void initIds( List<? extends AstNode> nodeRefs ) {
		final AstNode.Visitor<Void> visitor = new AstNode.Visitor<Void>() {
			
			private int nextId=0;
			
			@Override
			public Void visit(AstAskNode askRef) {
				visitSimpleNode( askRef );
				
				for ( AstAnswerSubNode ans : askRef.getAnswers() ) {
					for ( AstNode inr : ans.getSubGraph() ) {
						inr.accept(this);
					}
				}
				
				return null;
			}

			@Override
			public Void visit(AstCallNode callRef) {
				return visitSimpleNode(callRef);
			}

			@Override
			public Void visit(AstEndNode nedRef) {
				return visitSimpleNode(nedRef);
			}

			@Override
			public Void visit(AstSetNode setRef) {
				return visitSimpleNode(setRef);
			}

			@Override
			public Void visit(AstTodoNode todoRef) {
				return visitSimpleNode(todoRef);
			}
            
            @Override
            public Void visit( AstRejectNode rej ) {
                return visitSimpleNode(rej);
            }
			
			private Void visitSimpleNode( AstNode simpleNodeRef ) {
				if ( simpleNodeRef.getId() == null ) {
					simpleNodeRef.setId(nextId());
				} 
				id2NodeRef.put(simpleNodeRef.getId(), simpleNodeRef);
                
				return null;
			}
			
			private String nextId() {
				return "$"+(nextId++);
			}
		};
		
		for ( AstNode inr: nodeRefs ) {
			inr.accept(visitor);
		}
	}
    
    /**
     * Builds a path, made of {@link TagType} instances, from {@link #topLevelType}
     * to the passed value.
     * @param value
     * @return Types, from the top to the value's type.
     */
    List<TagType> buildTypePath( TagValue value ) {
        LinkedList<TagType> path = new LinkedList<>();
        
        TagType tp = value.getType();
        while( tp != null ) {
            path.addFirst(tp);
            tp = parentType.get(tp);
        }
        
        return path;
    }
    
    void buildParentTypeRelations() {
        final Deque<TagType> types = new LinkedList<>();
        types.add( topLevelType );
        
        while ( ! types.isEmpty() ) {
            types.pop().accept(new TagType.VoidVisitor() {

                @Override
                public void visitCompoundTypeImpl(CompoundType t) {
                    for ( TagType fieldType : t.getFieldTypes() ) {
                        parentType.put(fieldType, t);
                        if ( fieldType instanceof CompoundType ) {
                            types.add( fieldType );
                        }
                    }
                }

                @Override public void visitAtomicTypeImpl(AtomicType t) {}
                @Override public void visitAggregateTypeImpl(AggregateType t) {}
                @Override public void visitTodoTypeImpl(ToDoType t) {}
            });
        }
    }

}
