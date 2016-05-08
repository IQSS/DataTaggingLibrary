package edu.harvard.iq.datatags.visualizers.html;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
*
* @author tzulang
*/
public class DesicionGraphToJSONTest {
    
    private final CompoundType emptyTagSpace = new CompoundType("","");
    private DecisionGraphParser parser ;
    private DecisionGraph dg;
    
    public DesicionGraphToJSONTest() {
    }
    
    @Before
    public void setUp() {
        parser= new DecisionGraphParser();
        dg =null;
            }

    @Test
    public void toDoTest()throws Exception {
        
        String code = "[>idOfTodo< todo: Just Do It!][>e< end]";
        String expectedString =
                "{\n" +
                "	'idOfTodo': {\n" +
                "		'nodes': [{\n" +
                "			'id': 'idOfTodo',\n" +
                "			'text': 'Just Do It!',\n" +
                "			'type': 'TodoNode'\n" +
                "		}, {\n" +
                "			'id': 'e',\n" +
                "			'type': 'EndNode'\n" +
                "		}],\n" +
                "		'edges': [{\n" +
                "			'source': 'idOfTodo',\n" +
                "			'target': 'e'\n" +
                "		}]\n" +
                "	},\n" +
                "	'$startNode': 'idOfTodo'\n" +
                "}";
        dg= parser.parse(code).compile(emptyTagSpace);
        dg.setTopLevelType(emptyTagSpace);
        
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
        JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
    };
    
    @Test
    public void callNodeTest() throws Exception{
        String code = "[>idOfCall< call: idOfCalled][>e< end]";
        String expectedString =
            "{\n" +
            "	'idOfCall': {\n" +
            "		'nodes': [{\n" +
            "			'id': 'idOfCall',\n" +
            "			'type': 'CallNode',\n" +
            "			'CalleeId': 'idOfCalled'\n" +
            "		}, {\n" +
            "			'id': 'e',\n" +
            "			'type': 'EndNode'\n" +
            "		}],\n" +
            "		'edges': [{\n" +
            "			'source': 'idOfCall',\n" +
            "			'target': 'e'\n" +
            "		}]\n" +
            "	},\n" +
            "	'$startNode': 'idOfCall'\n" +
            "}";             
        dg= parser.parse(code).compile(emptyTagSpace);
        dg.setTopLevelType(emptyTagSpace);
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
        JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
    }
    
    @Test
    public void endNodeTest() throws Exception{
        String code = "[>e< end]";
        String expectedString =
                   "{\n" +
                    "	'e': {\n" +
                    "		'nodes': [{\n" +
                    "			'id': 'e',\n" +
                    "			'type': 'EndNode'\n" +
                    "		}],\n" +
                    "		'edges': []\n" +
                    "	},\n" +
                    "	'$startNode': 'e'\n" +
                    "}";

        
        dg= parser.parse(code).compile(emptyTagSpace);
        dg.setTopLevelType(emptyTagSpace);
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
        JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
    }
    
    
    @Test
    public void rejectTest() throws Exception{
        
        String code = "[>idOfReject< reject: reason of reject][>e< end]";
        String expectedString =
            "{\n" +
            "	'idOfReject': {\n" +
            "		'nodes': [{\n" +
            "			'reason': 'reason of reject',\n" +
            "			'id': 'idOfReject',\n" +
            "			'type': 'RejectNode'\n" +
            "		}],\n" +
            "		'edges': []\n" +
            "	},\n" +
            "	'e': {\n" +
            "		'nodes': [{\n" +
            "			'id': 'e',\n" +
            "			'type': 'EndNode'\n" +
            "		}],\n" +
            "		'edges': []\n" +
            "	},\n" +
            "	'$startNode': 'idOfReject'\n" +
            "}";
        dg= parser.parse(code).compile(emptyTagSpace);
        dg.setTopLevelType(emptyTagSpace);
        
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
            JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
            
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
        
    }
//
    @Test
    public void setTest() throws Exception{
        AtomicType t1 = new AtomicType("t1", "");
        t1.registerValue("a", "");
        t1.registerValue("b", "");
        t1.registerValue("c", "");
        
        AtomicType t2Items = new AtomicType("t2Items","");
        AggregateType t2 = new AggregateType("t2","", t2Items);
        t2Items.registerValue("b", "");
        t2Items.registerValue("c", "");
        
        CompoundType ct = new CompoundType("topLevel", "");
        ct.addFieldType(t1);
        ct.addFieldType(t2);
        
        String code = "[>set< set: t1=a; t2+= b,c][>e< end]";
        String expectedString =
            "{\n" +
            "	'set': {\n" +
            "		'nodes': [{\n" +
            "			'assignments': [{\n" +
            "				't2': ['b', 'c']\n" +
            "			}, {\n" +
            "				't1': 'a'\n" +
            "			}],\n" +
            "			'id': 'set',\n" +
            "			'type': 'setNode'\n" +
            "		}, {\n" +
            "			'id': 'e',\n" +
            "			'type': 'EndNode'\n" +
            "		}],\n" +
            "		'edges': [{\n" +
            "			'source': 'set',\n" +
            "			'target': 'e'\n" +
            "		}]\n" +
            "	},\n" +
            "	'$startNode': 'set'\n" +
            "}";
        dg = parser.parse(code).compile(ct);
        dg.setTopLevelType(ct);
        
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
        JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
    }
    
    @Test
    public void setTest2() throws Exception{
        
        String tsCode = "top: consists of mid1, mid2. "
                + "mid1: one of A, B, C, D. "
                + "mid2: consists of bottom1, bottom2. "
                + "bottom1: one of Q, W, E.\n"
                + "bottom2: some of A,S,D,F.";
        String code = "[>idOfSet< set: mid1=B; bottom1=W; bottom2+=S,D,F][>e< end]";
        String expectedString =
                "{\n" +
                "	'$startNode': 'idOfSet',\n" +
                "	'idOfSet': {\n" +
                "		'nodes': [{\n" +
                "			'assignments': [{\n" +
                "				'mid1': 'B'\n" +
                "			}, {\n" +
                "				'mid2': [{\n" +
                "					'bottom1': 'W'\n" +
                "				}, {\n" +
                "					'bottom2': ['S', 'D', 'F']\n" +
                "				}]\n" +
                "			}],\n" +
                "			'id': 'idOfSet',\n" +
                "			'type': 'setNode'\n" +
                "		}, {\n" +
                "			'id': 'e',\n" +
                "			'type': 'EndNode'\n" +
                "		}],\n" +
                "		'edges': [{\n" +
                "			'source': 'idOfSet',\n" +
                "			'target': 'e'\n" +
                "		}]\n" +
                "	}\n" +
                "}";
        CompoundType ts = new TagSpaceParser().parse(tsCode).buildType("top").get();
        dg = parser.parse(code).compile(ts);
        dg.setTopLevelType(ts);
        
        JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
        JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//        p(stringified,expected);
        assertEquals(expected.toJSONString(), stringified.toJSONString());
    }
    
        @Test
	public void askTest() throws Exception {
            String code = 
                      "[>idOfAsk< ask: {text: why?} "
                    + " {terms: { duh : ?????}"
                    + "         { dunno: dont knot }}"
                    + " {answers: {dunno:[>de< end]} "
                    + "           {why not:[>wnc< call: duh]}}]"
                    + "[>e< end]";
           String expectedString =
                    "{\n" +
                    "	'$startNode': 'idOfAsk',\n" +
                    "	'idOfAsk': {\n" +
                    "		'nodes': [{\n" +
                    "			'id': 'de',\n" +
                    "			'type': 'EndNode'\n" +
                    "		}, {\n" +
                    "			'id': 'wnc',\n" +
                    "			'type': 'CallNode',\n" +
                    "			'CalleeId': 'duh'\n" +
                    "		}, {\n" +
                    "			'id': 'e',\n" +
                    "			'type': 'EndNode'\n" +
                    "		}, {\n" +
                    "			'question': 'why?',\n" +
                    "			'terms': [{\n" +
                    "				'term': 'duh',\n" +
                    "				'explanation': '?????'\n" +
                    "			}, {\n" +
                    "				'term': 'dunno',\n" +
                    "				'explanation': 'dont knot'\n" +
                    "			}],\n" +
                    "			'answers': [{\n" +
                    "				'answer_sub_graph_id': 'de',\n" +
                    "				'text': 'dunno'\n" +
                    "			}, {\n" +
                    "				'answer_sub_graph_id': 'wnc',\n" +
                    "				'text': 'why not'\n" +
                    "			}],\n" +
                    "			'id': 'idOfAsk',\n" +
                    "			'type': 'AskNode'\n" +
                    "		}],\n" +
                    "		'edges': [{\n" +
                    "			'source': 'idOfAsk',\n" +
                    "			'target': 'de'\n" +
                    "		}, {\n" +
                    "			'source': 'idOfAsk',\n" +
                    "			'target': 'wnc'\n" +
                    "		}, {\n" +
                    "			'source': 'wnc',\n" +
                    "			'target': 'e'\n" +
                    "		}]\n" +
                    "	}\n" +
                    "}";
                                     
          dg= parser.parse(code).compile(emptyTagSpace);
          dg.setTopLevelType(emptyTagSpace);
          JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
          JSONObject stringified =JsonFactory.decisionGraphToJSON(dg);
//          p(stringified,expected);
          assertEquals(expected.toJSONString(), stringified.toJSONString());
	}
}

