package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.*;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import org.junit.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class EagerSetCallOptimizerTest {
    private final String tsCode = "DataTags: consists of A, B, X, Y, Z." +
                                  "A: one of AA, BB, CC, DD, EE, FF." +
                                  "B: one of AA, BB, CC, DD, EE, FF." +
                                  "X: one of AA, BB, CC, DD, EE, FF." +
                                  "Z: one of AA, BB, CC, DD, EE, FF." +
                                  "Y: one of AA, BB, CC, DD, EE, FF.";

    private void compareAndTest(String dgCodeOriginal, String dgCodeOpt) throws DataTagsParseException {
        CompoundType ts = new TagSpaceParser().parse(tsCode).buildType("DataTags").get();

        DecisionGraph dgOriginal = new DecisionGraphParser().parse(dgCodeOriginal).compile(ts);
        DecisionGraph dgOptimized = new DecisionGraphParser().parse(dgCodeOpt).compile(ts);

        assertFalse(compareDgs(dgOriginal.getStart(), dgOptimized.getStart()));

        /* Optimize and compare */
        EagerSetCallsOptimizer tagSpaceOptimizer = new EagerSetCallsOptimizer();
        dgOriginal = tagSpaceOptimizer.optimize(dgOriginal);

        assertTrue(compareDgs(dgOriginal.getStart(), dgOptimized.getStart()));
    }

    @Test
    public void startNodeOptimizationTest() throws DataTagsParseException {

        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA; B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; A=AA; B=FF]" +
                "    }" +
                "    {Three:" +
                "       [set: X=AA; Y=FF; A=AA; B=BB]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; B=FF]" +
                "    }" +
                "    {Three:" +
                "       [set: X=AA; Y=FF; B=BB]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    @Test
    public void internalOptimizationTest() throws DataTagsParseException {
        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA; B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; A=AA; B=FF]" +
                "    }" +
                "    {Three:" +
                "" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=FF]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA; B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; A=AA; B=FF]" +
                "    }" +
                "    {Three:" +
                "      [set: X=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip:            " +
                "          }" +
                "          {Blop:" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    @Test
    public void combinedOptimizationTest() throws DataTagsParseException {
        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA; B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; A=AA; B=FF]" +
                "    }" +
                "    {Three:" +
                "" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=FF; A=AA]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF; A=AA]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: B=BB]" +
                "    }" +
                "    {Two:" +
                "       [set: X=AA; Y=FF; B=FF]" +
                "    }" +
                "    {Three:" +
                "      [set: X=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "          }" +
                "          {Blop:" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    @Test
    public void askInSetOptimizationTest() throws DataTagsParseException {
        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: Y=EE; A=AA]" +
                "    }" +
                "    {Two:" +
                "      [set: Y=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB; A=AA]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF; A=AA]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: Y=EE]" +
                "    }" +
                "    {Two:" +
                "      [set: Y=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    /*
    * This function tests a unique optimization -
    * it removes 'set' that you know that will be set to a different values in the sub-tree,
    * (hence it is has no meaning)
    * */
    @Test
    public void removeUnusedSetOptimizationTest() throws DataTagsParseException {
        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: Y=EE; A=AA]" +
                "    }" +
                "    {Two:" +
                "      [set: A=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB; A=AA]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF; A=AA]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";


        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: Y=EE]" +
                "    }" +
                "    {Two:" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    /*
     * This function tests a unique optimization -
     * it removes 'set' that you know that will be set to a different values in the sub-tree,
     * (hence it is has no meaning)
     * */
    @Test
    public void mergeSetsWhileOptimizationTest() throws DataTagsParseException {
        String dgCodeOriginal = "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: A=AA]" +
                "       [set: Y=EE]" +
                "       [set: Z=AA]" +
                "" +
                "    }" +
                "    {Two:" +
                "      [set: Y=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB; A=AA]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF; A=AA]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        String dgCodeOpt = "[set: A=AA]" +
                "[ask:" +
                "  {text: Choose path one or two?}" +
                "  {answers:" +
                "    {One: " +
                "       [set: Y=EE; Z=AA]" +
                "    }" +
                "    {Two:" +
                "      [set: Y=FF]" +
                "      [ask:" +
                "        {text: Blip or Blop?}" +
                "        {answers: " +
                "          {Blip: " +
                "            [set: X=BB]" +
                "          }" +
                "          {Blop:" +
                "            [set: X=FF]" +
                "          }" +
                "        }" +
                "      ]" +
                "    }" +
                "  }" +
                "]";

        compareAndTest(dgCodeOriginal, dgCodeOpt);
    }

    private boolean compareDgs(Node node1, Node node2) {
        if (node1 instanceof EndNode) {
            if (!(node2 instanceof EndNode)) {
                return false;
            }

            return true;
        }

        if (node1 instanceof RejectNode) {
            if (!(node2 instanceof RejectNode)) {
                return false;
            }

            return true;
        }

        if (node1 instanceof ToDoNode) {
            if (!(node2 instanceof ToDoNode)) {
                return false;
            }

            ToDoNode n1 = (ToDoNode) node1;
            ToDoNode n2 = (ToDoNode) node2;
            // 1. Compare text
            if (!(n1.getTodoText().equals(n2.getTodoText()))) {
                return false;
            }

            // 2. Compare child
            return n1.hasNextNode() && n2.hasNextNode() && compareDgs(n1.getNextNode(), n2.getNextNode());

        }

        if (node1 instanceof CallNode) {
            if (!(node2 instanceof CallNode)) {
                return false;
            }

        }

        if (node1 instanceof SetNode) {
            if (!(node2 instanceof SetNode)) {
                return false;
            }

            SetNode n1 = (SetNode) node1;
            SetNode n2 = (SetNode) node2;
            // 1. Compare tags
            if (! (n1.getTags().equals(n2.getTags()))) {
                return false;
            }

            // 2. Compare child
            if (n1.hasNextNode() && n2.hasNextNode()) {
                return compareDgs(n1.getNextNode(), n2.getNextNode());
            }

            return false;
        }

        if (node1 instanceof AskNode) {
            if (!(node2 instanceof AskNode)) {
                return false;
            }

            AskNode n1 = (AskNode) node1;
            AskNode n2 = (AskNode) node2;

            // Compare question
            if (!(n1.getText().equals(n2.getText())))
                return false;

            // Compare answers
            if (!(n1.getAnswers().equals(n2.getAnswers()))) {
                return false;
            }

            // Recursive check childs
            for (Answer a1 : n1.getAnswers()) {
                if (! compareDgs(n1.getNodeFor(a1), n2.getNodeFor(a1))) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }
}
