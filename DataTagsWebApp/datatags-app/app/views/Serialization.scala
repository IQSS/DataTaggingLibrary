package views

import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import models._


object Serialization {


def encodeClientAnswers(oldClientAnswers: String, newestAnswer: Answer, answerDictionary: Map[String, String]) = {
    var newClientAnswers = "";
    for (key <- answerDictionary.keys) {
    	// scala.Console.println("Key: " + key)
        answerDictionary.get(key).foreach( value => if (value.equals(newestAnswer.getAnswerText)) {
        newClientAnswers = oldClientAnswers + key
        // scala.Console.println("Value: " + answerDictionary.get(key))
    	// scala.Console.println("Answer: " + newestAnswer.getAnswerText)
      }
    )
   }
   // scala.Console.println(newClientAnswers)
   newClientAnswers
  }



  def decodeClientAnswers(clientAnswers: String, answerDictionary: Map[String, String], userServer: UserSession) = {
  	var currentAskNode: AskNode = userServer.answerHistory.head.question; // tracks current question

  	var updatedAnswerHistory: Seq[AnswerRecord] = Seq() // new answer history
  	var updatedNodeHistory: Seq[Node] = Seq() // new node history

  	// scala.Console.println("length of client answers: " + clientAnswers.length)

  	for (index <- 0 to clientAnswers.length-1) { // go through all answers

  		// scala.Console.println("Answer dictionary keys: " + answerDictionary.keys)

  		for (key <- answerDictionary.keys) {

  			// scala.Console.println("Each key: " + key)
  			// scala.Console.println("Client answers: " + clientAnswers.charAt(index))

  			if (key.equals(clientAnswers.charAt(index).toString)) { // match digit to yes/no answer
  				val currentAnswer = new Answer(answerDictionary(key)) // create the corresponding answer
  				updatedAnswerHistory = updatedAnswerHistory :+ AnswerRecord(currentAskNode, currentAnswer) // update the serverside answer history
  				updatedNodeHistory = updatedNodeHistory :+ currentAskNode

  				// scala.Console.println("Next node: " + currentAskNode.getNodeFor(currentAnswer))

  				if (index < clientAnswers.length-1) { // while we are not on the last answer, update AskNode
  					currentAskNode = currentAskNode.getNodeFor(currentAnswer).asInstanceOf[AskNode];
  				}
  			}
  		}
  	}

  	// runtime engine state is null because it can be more easily accessed and replaced in the interview
  	// BUT we can also run get the runtime engine state here with a little more work, if necessary
  	val updatedUserServer = userServer.replaceHistory(updatedAnswerHistory, updatedNodeHistory, null)

  	// scala.Console.println("UserSession: " + userServer)
  	updatedUserServer
  	
   }

 }
 