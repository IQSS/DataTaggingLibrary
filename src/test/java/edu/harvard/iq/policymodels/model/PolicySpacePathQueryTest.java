package edu.harvard.iq.policymodels.model;

import edu.harvard.iq.policymodels.model.PolicySpacePathQuery;
import edu.harvard.iq.policymodels.model.PolicySpacePathQuery.Result;
import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import edu.harvard.iq.policymodels.parser.policyspace.TagSpaceParseResult;
import edu.harvard.iq.policymodels.parser.policyspace.TagSpaceParser;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author michael
 */
public class PolicySpacePathQueryTest {
    
    private final static String SOURCE 
            = "top: consists of agg, ato, cmp, toodoo.\n"
            + "agg: some of A, B, C.\n"
            + "ato: one of X, Y, Z.\n"
            + "toodoo:TODO.\n"
            + "cmp: consists of cmp_a, cmp_b.\n"
            + "cmp_a: one of aA, aB, aC.\n"
            + "cmp_b: TODO."
            ;
    
    
    CompoundSlot baseType;
    PolicySpacePathQuery sut;
    
    @Before
    public void before() throws Exception {
        TagSpaceParseResult parse = new TagSpaceParser().parse(SOURCE);
        baseType = parse.buildType("top").get();
        sut = new PolicySpacePathQuery(baseType);
    }

    @Test
    public void testFindSlotL1() {
        List<String> path = Arrays.asList("top","agg");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.SlotTypeResult(baseType.getSubSlot("agg"), path), actual );
    }

    @Test
    public void testFindSlotL2() {
        List<String> path = Arrays.asList("top", "cmp", "cmp_a");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.SlotTypeResult(((CompoundSlot)baseType.getSubSlot("cmp")).getSubSlot("cmp_a"), path), actual );
    }
    
    @Test
    public void testFindAggregateValue() {
        List<String> path = Arrays.asList("top","agg", "B");
        Result actual = sut.get(path);
        AggregateSlot slt = (AggregateSlot) baseType.getSubSlot("agg");
        AbstractValue val = slt.getItemType().valueOf("B");
        assertEquals( new PolicySpacePathQuery.TagValueResult(val, path), actual );
    }
    
    @Test
    public void testFindAtomicValue() {
        List<String> path = Arrays.asList("top","ato", "X");
        Result actual = sut.get(path);
        AtomicSlot slt = (AtomicSlot) baseType.getSubSlot("ato");
        AbstractValue val = slt.valueOf("X");
        assertEquals( new PolicySpacePathQuery.TagValueResult(val, path), actual );
    }
    
    @Test
    public void testFindTodoSlot() {
        List<String> path = Arrays.asList("top","cmp", "cmp_b");
        Result actual = sut.get(path);
        CompoundSlot cmp = (CompoundSlot) baseType.getSubSlot("cmp");
        assertEquals( new PolicySpacePathQuery.SlotTypeResult(cmp.getSubSlot("cmp_b"), path), actual );
    }
    
    @Test
    public void testNotFindAtomicValue() {
        List<String> path = Arrays.asList("top","ato", "XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }

    @Test
    public void testNotFindAggregateValue() {
        List<String> path = Arrays.asList("top","agg", "XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }

    @Test
    public void testNotFindAggregateSubValue() {
        List<String> path = Arrays.asList("agg", "A", "XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    @Test
    public void testNotFindSlot1() {
        List<String> path = Arrays.asList("top","cmp", "XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    @Test
    public void testNotFindSlot2() {
        List<String> path = Arrays.asList("top","cmpXXX", "XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    @Test
    public void testNotFindSlotAcrossTodo() {
        List<String> path = Arrays.asList("top","cmp", "cmp_b","XXX");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    @Test
    public void testNotFindBadBaseName() {
        List<String> path = Arrays.asList("topXXX","cmp", "cmp_b");
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    @Test
    public void testNotFindEmpty() {
        List<String> path = Arrays.asList();
        Result actual = sut.get(path);
        assertEquals( new PolicySpacePathQuery.NotFoundResult(path), actual );
    }
    
    
}
