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
	if (paragraph.contains('*')) {
		var split = paragraph.split("\n")

		for (line <- 0 until split.length) {
			split(line) = split(line).trim
			if (split(line).startsWith("*")) {
				split(line) = split(line).replace("*", "<li>")
				split(line) = split(line).concat("</li>")
			}
		}

		var list = split.mkString
		complete = list.substring(0, list.indexOf("<li>")) + "<ul>" + list.substring(list.indexOf("<li>"), list.length) + "</ul>"
	}
	Html(complete)
}

}