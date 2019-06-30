/**
* Scripts for enabling components on a webpage based on cookie values.
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
* [online] https://www.ccg.leeds.ac.uk/software/ 
*
* --End of Copyright notice-- 
*
**/



/**
* Script variables
**/
var debug = true;



/**
* Variable for setting up color changes on drop target.
**/
var dragTargetColor;


/**
* At start of drag and drop, set the content of dataTransfer to the dragged object.
* Also adjust cursor.
* ToDo: Cursor doesn't seem to work, though nothing breaks.
**/
function dragStart(ev) {

	var quizDivs = getElementsByClassName("quiz");
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
* ToDo: At moment this color change only  works in Chrome, unlike the others. 
**/
function dragEnter(ev) {
	ev.preventDefault(); // Stops the droptarget being rejected as the target.
	ev.stopPropagation(); // Not convinced this does anything.
	dragTargetColor = ev.target.style.color; // Record current target color for resetting later.
	ev.target.style.color = "#0000ff";  // Change to blue to show user they've dragged into right spot.
}


/**
* Reset cursor and target color on dragging out.
**/
function dragLeave(ev) {
	var quizDivs = getElementsByClassName("quiz");
	for (var i = 0; i < quizDivs.length; i++) {
		quizDivs[i].style.cursor = "default";
	} 
	ev.target.style.color = dragTargetColor;
}


/**
* Reset cursor and target color on dragging out.
**/
function dragEnd(ev) {
	var quizDivs = getElementsByClassName("quiz");
	for (var i = 0; i < quizDivs.length; i++) {
		quizDivs[i].style.cursor = "default";
	} 
	ev.target.style.color = dragTargetColor;
}


/**
* Variable to record how many answers they have correct.
**/
var correct = 0;

/**
* Reveals answers to quizes.
**/
function reveal() {

	var quizDivs = getElementsByClassName("quiz");
	for (var i = 0; i < quizDivs.length; i++) {

		// Find questions and answers for this DIV.	
		var elems = quizDivs[i].children;
		var quizQuestion;
		var answerList;
		
		for (var j = 0; j < elems.length; j++) {
		
			if ((elems[j].id.indexOf('q') == 0) && (elems[j].tagName == "P"))  {
				quizQuestion = elems[j];
			}

			if (elems[j].tagName == "OL")  {
				answerList = elems[j];
			}

		}
		
		var answers = answerList.children;
		var rightAnswer = quizQuestion.id.substring(2);

		
		// Get target area text.
		var text = quizQuestion.innerHTML;

		
		// Strip out underscores used to show location for drop of answer.
		var firstIndex = text.indexOf("__");
		var lastIndex = text.lastIndexOf("__");
		if (firstIndex > 0) var frontText = text.substring(0, firstIndex);
		if (lastIndex > 0) var backText = text.substring(lastIndex + 2);
		
		
		for (var j = 0; j < answers.length; j++) {
			
			answers[j].draggable = false;
			
			
			if(answers[j].id.substring(2) == rightAnswer) {
				
				// Add HTML from source.
				quizQuestion.innerHTML = frontText + answers[j].innerHTML + backText;
				answers[j].innerHTML = "";
			
			}
		}
		
		// Display any explanatory text.
		var explanation = document.getElementById("e" + quizQuestion.id.substring(1,2));
		if (explanation != null) {
			explanation.style.display="block";
		}
	}

	document.getElementById("continue").style.display="block";	
	
}

/**
* Variable to record how many answers they have correct.
* Works out correct answer by comparing value at index = 3 in source id and target id.
* Source ids should be "a for answer" + "code-letter" + "answer number", e.g. ad1, ad2, ad3, etc...
* Source ids should be "q for question" + "code-letter" + "correct answer number", e.g. qd2.
* Number of correct answers needed is given in "data-proceed" tag in BODY.
* ToDo: Check also index = 2 positions match to confirm right answer for right quiz.
**/
function drop(ev) {

	var quizDivs = getElementsByClassName("quiz");
	for (var i = 0; i < quizDivs.length; i++) {
		quizDivs[i].style.cursor = "default";
	} 
	
	ev.preventDefault();

	// Code to assess whether the right answer is given.
	var data=ev.dataTransfer.getData("Text");
	ev.target.style.color = dragTargetColor;
	
	// Check the answer is an answer for this quiz, and 
	// then that the right answer has been picked.
	if ((data.substring(1,2) == ev.target.id.substring(1,2)) && 
		((parseInt(data.substring(2))) == (parseInt(ev.target.id.substring(2))))) {

		// Treat differently depending on target area. 
		// So far only P and SPAN.
		if ((ev.target.nodeName == "P") || (ev.target.nodeName == "SPAN")) {
		
			// Get target area text.
			var text = ev.target.innerHTML;
			
			// Strip out underscores used to show location for drop of answer.
			var firstIndex = text.indexOf("__");
			var lastIndex = text.lastIndexOf("__");
			if (firstIndex > 0) var frontText = text.substring(0, firstIndex);
			if (lastIndex > 0) var backText = text.substring(lastIndex + 2);
			
			// Add HTML from source.
			ev.target.innerHTML = frontText + document.getElementById(data).innerHTML + backText;
			
			// Give flash of green to show users they got it right.
			var elementColor = ev.target.style.color;
			ev.target.style.color = "#00cc00";
			setTimeout(function() {ev.target.style.color = elementColor;},500);
			
			// Increment correct answers.
			correct++;
			
			// Stop them dragging the answer here again.
			document.getElementById(data).draggable = false;
			document.getElementById(data).innerHTML = "";
			
			// Display any explanatory text.
			var explanation = document.getElementById("e" + data.substring(1,2));
			if (explanation != null) {
				explanation.style.display="block";
				elementColor = explanation.style.color;
				explanation.style.color = "#00cc00";
				setTimeout(function() {explanation.style.color = elementColor;},500);
			}
			
			// If there are enough correct answers, show way on to next tutorial 
			// and update cookie so any elements on the forthcoming page can be shown.
			if (correct == (parseInt(document.body.dataset.proceed))) {
				document.getElementById("continue").style.display="block";
				upDateCookie();
			}
		}
		
	} else {  // if answer dragged is wrong.
		// Give a flash of red as feedback.
		var elementColor = ev.target.style.color;
		ev.target.style.color = "#ff0000";
		setTimeout(function() {ev.target.style.color = elementColor;},500);
	}
	
} // End of drop code.




/**
* Runs through iFrames, replacing content. Used with a tag only recognised by IE 9 and under in HEAD:
* <!--[if lte IE 9]><SCRIPT type="text/javascript">window.onload = replaceSlides;</SCRIPT><![endif]-->
*/
function replaceSlides() {

	var frames = document.getElementsByTagName("IFRAME");

	for (var i = 0; i < frames.length; i++) {
	
		var content = (frames[i].contentWindow || frames[i].contentDocument);

		var links = content.document.getElementsByTagName("LINK");
		while (links.length > 0) links[0].parentNode.removeChild(links[0]);
		
		var meta = content.document.getElementsByTagName("META");
		while (meta.length > 0) meta[0].parentNode.removeChild(meta[0]);
		
		var scpts = content.document.getElementsByTagName("SCRIPT");
		while (scpts.length > 0) scpts[0].parentNode.removeChild(scpts[0]);
		
		var html = "<P style=\"margin-top:30px;text-align:center;font-family: 'Courier new', Courier, monospace;color:#777777;\"><A href=index.html target=_new style=\"font-family: 'Courier new', Courier, monospace;\">Slides</A><BR />[Opens in new tab]<BR /><BR />Please note that this site works better in IE 10 or above.</P>";
		
		content.document.getElementsByTagName("BODY")[0].innerHTML = html;  

	}

}





/**
* Validates radio buttons injected for IE 9 and below. Uses tags only recognised by IE 9 and below thus:
* <LI id="ac1" class=quizli draggable="true" ondragstart="dragStart(event)" ondragend="dragEnd(event)">
* <!--[if lte IE 9]> <input type="radio" name="radiosc" value="ac1" id = "ac1R" onClick= validateRadio(this)> <![endif]-->write; debug; debug; debug; cry.</LI>
* Author: Rachel Oldroyd
**/
function validateRadio(radio)	{

	//Method is called onClick, so double check radio is selected
	if (radio.checked==true)	{
	
		//Scale DOM to find question  
		var answerLi 	 = document.getElementById(radio.id).parentNode; 
		var answerOl 	 = answerLi.parentNode; 
		var questionDivs = answerOl.parentNode.children; 
	
		for (var i = 0; i < questionDivs.length; i++)	{
			if (questionDivs[i].tagName == "P" && questionDivs[i].id.substring(0,1)== "q")	{
				var question = questionDivs[i]; 
			} 
		}
	
		//Compare selected answer ID to question ID.
		if (question.id.substring(1,3) == radio.id.substring(1,3))	{
			
			// Get target area text.
			var text = question.innerHTML;
			
			//Disable radio buttons
			var radios = document.getElementsByName("radios"+radio.id.substring(1,2)); 
			for (var i = 0; i < radios.length; i++)	{
				if (radios[i].id.substring(1,2) == radio.id.substring(1,2))	{
					radios[i].disabled = true; 
				}				
			}
			
			// Strip out underscores used to show location for drop of answer.
			var firstIndex = text.indexOf("__");
			var lastIndex = text.lastIndexOf("__");
			if (firstIndex > 0) var frontText = text.substring(0, firstIndex);
			if (lastIndex > 0) var backText = text.substring(lastIndex + 2);
			
			// Add HTML from source.
			question.innerHTML = frontText + answerLi.innerText + backText;
			answerLi.innerHTML = ""; 
			
			// Give flash of green to show users they got it right.
			var elementColor = question.style.color;
			question.style.color = "#00cc00";
			setTimeout(function() {question.style.color = elementColor;},500);
			
			//Reveal explantion text
			var explanation = document.getElementById("e" + question.id.substring(1,2));
			
			if (explanation != null) {
				explanation.style.display="block";
				elementColor = explanation.style.color;
				explanation.style.color = "#00cc00";
				setTimeout(function() {explanation.style.color = elementColor;},500);
			}
		}
	
		else 	{
			//Flash of red to show incorrect answer. 
			var elementColor = question.style.color;
			question.style.color = "#ff0000";
			setTimeout(function() {question.style.color = elementColor;},500);
		}
	}
}
	