package views


import scala.collection.mutable.ArrayBuffer

import play.api.templates._


object Helpers {
	
def makeUpper(input: String) = {
	var returnable = ""
	if (isPhraseEnglishAndLower(input)) {
		var upper = ArrayBuffer[String]()
		val test = input.split(" ")

		for (i <- 0 to test.length-1) {
			if (isWordLowerEnglish(test(i))) {
				upper += test(i).capitalize
			} else {
				upper += test(i)
			}
		}
		returnable = upper.mkString(" ")
	} else {
		returnable = input
	}
	returnable
}



def isPhraseEnglishAndLower (input: String) = {
	var shouldBeChanged = false
	val test = input.split(" ")
	for (i <- 0 to test.length-1) {
		if (isWordLowerEnglish(test(i))) {
			shouldBeChanged = true
		}
	}
	shouldBeChanged
}



def isWordLowerEnglish(input: String) = {
	val isPunctuation = input.endsWith("?") || input.endsWith("!") || input.endsWith(".")
	val isLastLowerLetter = input.charAt(input.length-1).isLetter && !input.charAt(input.length-1).isUpper

	var isLower = true
	for (i <- 0 to input.length-2) {
		if (!input.charAt(i).isLetter || input.charAt(i).isUpper) {
			isLower = false
		}
	}
	if (!isLastLowerLetter && !isPunctuation ) {
		isLower = false
	}
	isLower
}





def bulletPoint (paragraph: String) = {
	var complete = paragraph
	if (paragraph.contains("*")) {
		val split = paragraph.split("\n")

		val formatted = split.map( _.trim ).map( line => if (line.startsWith("*")) "<li>" + line.drop(1) + "</li>"
			else "<p>%s</p>".format(line))

		val reformatted = formatted.tail.foldLeft(List(List(formatted.head)))( (l,s) => {
			if (l.last.head(1) == s(1)) {
				l.dropRight(1) :+ (l.last :+ s)
			} else {
				l :+ List(s)
			}
		} )

		val stringList = reformatted.map( group => if (group.head.contains("<li>")) "<ul>"+group.mkString+"</ul>" else group.mkString)

		complete = stringList.mkString
	}
	play.twirl.api.Html(complete)
	}

}