package edu.harvard.iq.datatags.visualizers.html;

import edu.harvard.iq.datatags.model.types.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class TagSpaceToJSONTest {
	static AtomicType atomicType, atomicType2;
        static AggregateType aggregateType;
        static ToDoType todoType;
        static CompoundType compoundType;
        
	public TagSpaceToJSONTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
             
            atomicType = new AtomicType("Atomic", "note of Test atomicType");
                atomicType.registerValue("1", null);
                atomicType.registerValue("2", null);
                atomicType.registerValue("3",  "note of 3");
                
         
            
            atomicType2 = new AtomicType("Atomic 2", "note of Test atomicType 2");
                atomicType2.registerValue("11", "not of 11");
                atomicType2.registerValue("22", null);
                atomicType2.registerValue("33",  null);
                
            aggregateType=new AggregateType("aggregate","note of Test aggregateType",  atomicType);
            
            todoType = new ToDoType("todo_type", "to do info");
            
            compoundType= new CompoundType("compound"," note of Compound");
            compoundType.addFieldType(atomicType);
            compoundType.addFieldType(aggregateType);
//            compoundType.addFieldType(atomicType2);
            compoundType.addFieldType(todoType);
         
                    
                  
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	 
	@Test
	public void atomicTest() {               
            String expectedString = 
                    "{ 'note':'note of Test atomicType'," +
                    "  'values':[ {'name':'1','ordinal':0},"+
                    "             {'name':'2','ordinal':1}," + 
                    "             {'name':'3','ordinal':2, 'note': 'note of 3' } ]," +
                    "  'name':'Atomic','type':'AtomicType' } ";
            
            JSONObject expected =(JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
            JSONObject stringified= JsonFactory.toJson(atomicType);
            assertEquals(expected.toJSONString(), stringified.toJSONString());
            
	}
        
        
	@Test
	public void AggregateTest() {
            String expectedString = 
                "{" 
              + " 'type':'AggregateType' "
              + " 'name':'aggregate'," 
              + " 'note':'note of Test aggregateType',"
              + "	'values':[{'name':'1','ordinal':0},"
              + "			  {'name':'2','ordinal':1},"
              + "			  {'name':'3','ordinal':2,'note':'note of 3'}],"
              + "}";
            
            JSONObject expected = (JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
            JSONObject stringified= JsonFactory.toJson(aggregateType);
            
            assertEquals(expected.toJSONString(), stringified.toJSONString());                
            
	};
        
        @Test
	public void CompoundTest() {
            String expectedString = 
                "{"
               + " 'type':'CompoundType'"
               + " 'name':'compound',"
               + " 'note':'note of Compound',"
               + " 'fieldTypes':["
               + "               { 'type':'AggregateType',"
               + "                 'name':'aggregate',"
               + " 			   'note':'note of Test aggregateType',"
               + "                 'values':[{'name':'1','ordinal':0},"
               + "                           {'name':'2','ordinal':1},"
               + "                           {'note':'note of 3','name':'3','ordinal':2}],"
               + "				  } ,"				
               + "               {"
               + "                 'type':'ToDoType'"
               + "                 'name':'todo_type',"
               + " 				'note':'to do info',"
               + "				  },"
               + "				  {"
               + "					'type':'AtomicType'" 
               + "                 'name':'Atomic',"
               + "                 'note':'note of Test atomicType',"
               + "                 'values':[ {'name':'1','ordinal':0},"
               + "                            {'name':'2','ordinal':1},"
               + "                            {'note':'note of 3','name':'3','ordinal':2}],"
               + "               }],"
               + "}";
            
            JSONObject expected = (JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
            JSONObject stringified= JsonFactory.toJson(compoundType);           
            assertEquals(expected.toJSONString(), stringified.toJSONString());    
	};
        
        @Test
	public void ToDoTest() {
            String expectedString = 
                 "{"
                + " 'type':'ToDoType'"
                + " 'name':'todo_type',"
                + " 'note':'to do info',"
                + "}";
            JSONObject expected = (JSONObject) JSONValue.parse( expectedString.replaceAll("'", "\\\""));
            JSONObject stringified= JsonFactory.toJson(todoType);           
            assertEquals(expected.toJSONString(), stringified.toJSONString());    
	};
        
       
	
        
}
