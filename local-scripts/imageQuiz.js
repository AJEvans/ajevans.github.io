/**
* Scripts for drag and drop quizzes and other interactive stuff.
* Author: Andy Evans https://www.geog.leeds.ac.uk/people/a.evans
*
* --Copyright notice-- 
*
* Copyright (c) School of Geography, University of Leeds. 
* https://www.geog.leeds.ac.uk/
* This software is licensed under 'The Artistic License' which can be found at 
* the Open Source Initiative website at... 
* https://www.opensource.org/licenses/artistic-license.php
* Please note that the optional Clause 8 does not apply to this code.
*
* The Standard Version source code, and associated documentation can be found at... 
* [online] https://mass.leeds.ac.uk
*
* --End of Copyright notice-- 
*
**/


/**
* This program allows you to make little drag and drop quizzes and other interactive stuff.
* It works by showing and hiding different elements within a webpage.
* 
* For example, when a correct piece of code ("answer") is dragged onto another ("question"), both are hidden, and replaced 
* with a revealed "postquestion" object which combines the two. At the same time, a blank "preresult" area is hidden and 
* replaced with a revealed "postresult" object showing the result of the code having run, and a "postexplanation" piece of text 
* replaces a "preexplanation piece of text.
*
* Explanation:
*
* Objects on a webpage have tags, which include information like the id of a specific element and the class of a group of similar elements.
* The objects dragged have an id, for example "aa1" shows it is an answer (a) for quiz a (a) and is the first of several potential answers (1).
* The object they are dragged onto also has an id "qa1" showing it is a question (q) for quiz a (a) and the answer to the quiz is answer 1 (1).
* The objects dragged has the class "answer".
* The object dragged onto has the class "question".
* In addition, there is a potential area for results, class "preresult".
* There are also "postquestion", to show the result of the drag and drop, and "postresult", to show results in the result area. Both start hidden.
* Finally, there is an "postexplanation" class object, for example a paragraph of text, which is also un-hidden and a 
* "preexplanation" object which is hidden.
*
* When the correct answer is dragged onto the question, the other answers are disabled,
* the dragged answer and the question are hidden, and replaced with the postquestion object, which is 
* revealed. At the same time, the preresult and preexplanation objects are hidden and the postresult and postexplanation revealed.
*
**/




/**
* Works out correct answer by comparing value at position = 3 in source id and target id.
* Source ids should be "a for answer" + "code-letter" + "answer number", e.g. ad1, ad2, ad3, etc...
* Source ids should be "q for question" + "code-letter" + "correct answer number", e.g. qd2.
* ToDo: Check also positions = 2 match to confirm right answer for right quiz.
**/
function drop(ev) {

	// Find all the quizzes.
	var quizDivs = getElementsByClassName("quiz");
	
	// Loop through all the quizzes.
	for (var i = 0; i < quizDivs.length; i++) {

		// Set the mouse cursor from the drag icon to the standard icon.
		//quizDivs[i].style.cursor = "default";
	
		// Turn off drag and drop default.
		ev.preventDefault();

		// Get the id of the dragged object.
		var data=ev.dataTransfer.getData("Text");
		
		// Check the answer is an answer for this quiz, and 
		// then that the right answer has been picked.
		if ((data.substring(1,2) == ev.target.id.substring(1,2)) && 
			((parseInt(data.substring(2))) == (parseInt(ev.target.id.substring(2))))) {

			// Find all elements in the quiz.
			var elems = [];
			elems = getAllChildren(quizDivs[i], elems); 
			
			var prequestion;
			var postquestion;
			var preresult;
			var postresult;
			var preexplanation;
			var postexplanation;
			var answers = [];
			var countAnswers = 0;
			
			// Loop through all the elements and find the hidden and revealed bits.
			for (var j = 0; j < elems.length; j++) {
			
				if (elems[j].className == "prequestion") {
					prequestion = elems[j];
				}

				if (elems[j].className == "postquestion") {
					postquestion = elems[j];
				}
				
				if (elems[j].className == "preresult") {
					preresult = elems[j];
				}
				
				if (elems[j].className == "postresult") {
					postresult = elems[j];
				}
				
				if (elems[j].className == "preexplanation") {
					preexplanation = elems[j];
				}
				
				if (elems[j].className == "postexplanation") {
					postexplanation = elems[j];
				}
				if (elems[j].className = "answer")  {
					answers[countAnswers++] = elems[j];
				}

			}
			
			// Stop any other answers being dragged.
			for (var j = 0; j < answers.length; j++) {
				
				answers[j].draggable = false;
				
				// Hide the dragged answer.
				if (answers[j].id == data) answers[j].style.display="none";
	
			}
			
			// Hide the revealed ones and reveal the hidden ones.
			
			prequestion.style.display="none";
			postquestion.style.display="block";
			preresult.style.display="none";
			postresult.style.display="block";
			preexplanation.style.display="none";
			postexplanation.style.display="block";
				
		}
	} 
	
} // End of drop code.




/**
* At start of drag and drop, set the content of dataTransfer to the dragged object.
* Also adjust cursor.
* ToDo: Cursor doesn't seem to work, though nothing breaks.
**/
function dragStart(ev) {

	var quizDivs = getElementsByClassName("answer");
	for (var i = 0; i < quizDivs.length; i++) {
		quizDivs[i].style.cursor = "move";
	}
	ev.dataTransfer.setData("Text",ev.target.id);
}



/**
* Allows drop.
**/
function dragOver(ev) {
	ev.preventDefault(); 
}



/**
* Allow target to respond to potential drop.
**/
function dragEnter(ev) {
	ev.preventDefault(); // Stops the droptarget being rejected as the target.
	ev.stopPropagation(); // Not convinced this does anything.
}



/**
* Reset cursor on dragging out.
**/
function dragEnd(ev) {
	var quizDivs = getElementsByClassName("answer");
	for (var i = 0; i < quizDivs.length; i++) {
		quizDivs[i].style.cursor = "default";
	} 
}











	