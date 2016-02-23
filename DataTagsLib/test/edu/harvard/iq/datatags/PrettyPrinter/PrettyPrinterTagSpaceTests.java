/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.PrettyPrinter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author goldr
 */
public class PrettyPrinterTagSpaceTests {
    PrettyPrinterForTagSapce _ppt;
    
    public PrettyPrinterTagSpaceTests(){
        _ppt = new PrettyPrinterForTagSapce();
    }
    
    // this test checks the case of an Aggregate slot 
    @Test
    public void testCheckSumOf()
    { 
     String test = "Subject [Who will be greeted]: some of world [Planet, Earth], planet [Any planet], moon."; 
     test = this._ppt.initTagSapcePrettyPrinter(test);
     String expected = "Subject [Who will be greeted]: some of\n\t world [Planet, Earth],\n\t planet [Any planet],\n\t moon.\n\t";
        assertEquals(expected,test);
    }
    // this test checks the case of Compound slot
    @Test
    public void testCheckConsistsOf()
    {
     String test = "DataTags [Top level slot - will contain the tagging result]: consists of Code[code issues], Handling[handling issues], Legal[handling issues], Assertions[assertions issues].";
     test = this._ppt.initTagSapcePrettyPrinter(test);
     String expected = "DataTags [Top level slot - will contain the tagging result]: consists of\n\t Code[code issues],\n\t Handling[handling issues],\n\t Legal[handling issues],\n\t Assertions[assertions issues].\n\t";
     assertEquals(expected,test);
    } 
    // this test checks the case of Atomic slot
    @Test
    public void testCheckOneOf()
    {
     String test = "Greeting [A friendly greeting]: one of Code[code issues], Handling[handling issues], Legal[handling issues], Assertions[assertions issues].";
     test = this._ppt.initTagSapcePrettyPrinter(test);
     String expected = "Greeting [A friendly greeting]: one of\n\t Code[code issues],\n\t Handling[handling issues],\n\t Legal[handling issues],\n\t Assertions[assertions issues].\n\t";
     assertEquals(expected,test);
    } 
    // this test checks the case of line comment
    @Test 
     public void checkLineComment ()
     {
      String test =  "Subject [Who will be greeted]: some of world [Planet, Earth],<-- line comment\n planet [Any planet], moon.";
      test = this._ppt.initTagSapcePrettyPrinter(test);
      String expected = "Subject [Who will be greeted]: some of\n\t world [Planet, Earth],<-- line comment\n\t planet [Any planet],\n\t moon.\n\t";
        assertEquals(expected,test);
     }
     // this test checks the case of block comment
     @Test
     public void checkBlockComment ()
     {
      String test = "Subject [Who will be greeted]: some of world [Planet, Earth],<* this is a block comment*> planet [Any planet], moon.";
      test = this._ppt.initTagSapcePrettyPrinter(test);
      String expected = "Subject [Who will be greeted]: some of\n\t world [Planet, Earth],\n\t<* this is a block comment*> planet [Any planet],\n\t moon.\n\t";
      assertEquals(expected,test);
     }
     // this test checks the case of line comment at the end of the file
     @Test
     public void CheckLineCommentEndOfFile ()
     {
      String test = "Subject [Who will be greeted]: some of world [Planet, Earth], planet [Any planet] moon.<-- line comment\n";
      test = this._ppt.initTagSapcePrettyPrinter(test);
      String expected = "Subject [Who will be greeted]: some of\n\t world [Planet, Earth],\n\t planet [Any planet] moon.\n\t<-- line comment\n";
      assertEquals(expected,test);
     }
     // this test checks the case of block comment at the end of the file
     @Test
     public void CheckBlockCommentEndOfFile ()
     {
       String test = "Subject [Who will be greeted]: some of world [Planet, Earth], planet [Any planet], moon.<* block comment *>";
       test = this._ppt.initTagSapcePrettyPrinter(test);
       String expected = "Subject [Who will be greeted]: some of\n\t world [Planet, Earth],\n\t planet [Any planet],\n\t moon.\n\t<* block comment *>";
       assertEquals(expected,test);
     }
     // this test checks the case of both line and block comment in a text
     @Test
     public void checkCombineBlockAndLineComment ()
     {
         String test = "Subject [Who will be greeted]: some of world <* this is block comment *> [Planet, Earth], <-- this is line commnet\n planet [Any planet], moon, unrecognizedOrbitingObject [Hopefully they're friendly]. <-- That's no moon!\n";
         test = this._ppt.initTagSapcePrettyPrinter(test);
         String expected = "Subject [Who will be greeted]: some of\n\t world <* this is block comment *> [Planet, Earth], <-- this is line commnet\n\t planet [Any planet],\n\t moon,\n\t unrecognizedOrbitingObject [Hopefully they're friendly]. <-- That's no moon!\n";
         assertEquals(expected,test);
     }
     // this test checks if there is a new line after 80 chars in a row
     @Test
     public void checkDownALineAfter80Chars ()
     {
         String test = "Subject [Who will be greeted]: some of world [111111111111111111111111111111111111111111111111111111111111111111111111111].";
         test = this._ppt.initTagSapcePrettyPrinter(test);
         String expected = "Subject [Who will be greeted]: some of world [1111111111111111111111111111111111\n1111111111111111111111111111111111111111].";
         assertEquals(expected,test);
     }
     // this test checks the case of Compound, Aggregate and Atomic slots and
     // line comment in one text 
    @Test
    public void  checkCombineAllTest ()
    {
        String test = "DataTags [Top level slot - will contain the tagging result]: consists of  Greeting,    Subject. Subject [Who will be greeted]: some of  world [Planet Earth], <-- this is Planet Earth\n   planet [ Any planet], moon, unrecognizedOrbitingObject [Hopefully they're friendly]. Greeting [ Type of greeting to perform ]: one of ignore [The null greeting ], hug [Physically hugging the other person ].";
        test = this._ppt.initTagSapcePrettyPrinter(test);
        String expected = "DataTags [Top level slot - will contain the tagging result]: consists of\n\t Greeting,\n\t Subject.\n\t Subject [Who will be greeted]: some of\n\t world [Planet Earth], <-- this is Planet Earth\n\t planet [ Any planet],\n\t moon,\n\t unrecognizedOrbitingObject [Hopefully they're friendly].\n\t Greeting [ Type of greeting to perform ]: one of\n\t ignore [The null greeting ],\n\t hug [Physically hugging the other person ].\n\t";
        assertEquals(expected,test);
    }
    
          





}
