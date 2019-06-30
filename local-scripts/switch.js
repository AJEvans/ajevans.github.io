/**
* Scripts an interactive thing that converts switch settings to characters.
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
* Scripts an interactive thing that converts switch settings to characters.
* Shows how ASCII characters are encoded by changing binary switches. 
* Ok, it's stripped down to the point that it's not terribly realistic, but the idea is 
* to show computers are made from switches that control electrical flow.
*
* Characters are encoded within computers using a variety of different formats, but here 
* we're using ASCII (https://en.wikipedia.org/wiki/ASCII) which uses 7 ones or zeros (bits)
*
* The display interacts by displaying and hiding different images, but also by doing some 
* line drawing on a web component called a 'canvas'.
**/




/**
* Called when the user hits a switch.
* Hides one version of the switch picture and displays the other.
**/
function switchIt(on, off) {

	var onSwitch = document.getElementById(on);
	var offSwitch = document.getElementById(off);

	if (onSwitch.style["display"] == "none") {
		onSwitch.style["display"] = "inline";
	} else {
		onSwitch.style["display"] = "none";
	}
	
	if (offSwitch.style["display"] == "none") {
		offSwitch.style["display"] = "inline";
	} else {
		offSwitch.style["display"] = "none";
	}
	
} 

/**
* Used for drawing the wires.
**/
var endPoints = [];




/**
* Called when the webpage loads. Draws the wires etc. and 
* hides or reveals the right switch pictures (on or off) depending on 
* which need displaying.
**/
function start() {

	// Main 'run' button.
	document.getElementById("buttonOff").style["display"] = "block";
	document.getElementById("buttonOn").style["display"] = "none";

	
	// Draw the wires.
	var c = document.getElementById('outwire');
	
	var switchHeight = document.getElementById("switch1off").height;
	c.height = switchHeight * 8;
	c.width = 100;
	
	var ctx = c.getContext('2d');
	ctx.clearRect(0,0,c.width,c.height);
	ctx.strokeStyle="#666666";
	ctx.lineWidth = 1;

	var count = 0; 
	
	count = count + switchHeight / 2;
	endPoints.push(count);

	for (var i = 0; i < 6; i++) {
		count = count + switchHeight + 3;
		endPoints.push(count);
	}

	
	for (var i = 0; i < 7; i++) {
		ctx.beginPath();
		ctx.moveTo(-10, c.height/2);
		ctx.lineTo(c.width, endPoints[i]);
		ctx.stroke();
	}
	
	c = document.getElementById('inwire');
	c.height = switchHeight * 8;
	c.width = 100;
	ctx = c.getContext('2d');
	ctx.clearRect(0,0,c.width,c.height);

	ctx.strokeStyle="#666666";
	ctx.lineWidth = 1;
	
	
	for (var i = 0; i < 7; i++) {
		ctx.beginPath();
		ctx.moveTo(0, endPoints[i]);
		ctx.lineTo(c.width + 10, c.height/2);
		ctx.stroke();
	}
	
	
	// Set the various switches. 
	// Starts with "H" displayed.
	
	document.getElementById("switch1off").style["display"] = "none";
	document.getElementById("switch2on").style["display"] = "none";
	document.getElementById("switch3on").style["display"] = "none";
	document.getElementById("switch4off").style["display"] = "none";
	document.getElementById("switch5on").style["display"] = "none";
	document.getElementById("switch6on").style["display"] = "none";
	document.getElementById("switch7on").style["display"] = "none";
	
}




/**
* Called when the run button clicked. 
* Draws the wires electrified, and displays the right character depending on the switch settings.
**/
function runButton() {

	// Show depressed run switch.
	document.getElementById("buttonOn").style["display"] = "block";
	document.getElementById("buttonOff").style["display"] = "none";
	
	// Display first set of wires electrified.
	var c = document.getElementById('outwire');
	var ctx = c.getContext('2d');
	ctx.strokeStyle="#FFFFFF";
	ctx.lineWidth = 2;
	ctx.shadowBlur=5;
	ctx.shadowColor="#0000FF";

	
	for (var i = 0; i < 7; i++) {
		ctx.beginPath();
		ctx.moveTo(-10, c.height/2);
		ctx.lineTo(c.width, endPoints[i]);
		ctx.stroke();
	}
	
	
	// Repaint the first set of wires unelectrified, and the second electrified, after 300 milliseconds.
	setTimeout(function(){
		ctx.strokeStyle="#666666";
		ctx.lineWidth = 1;
		ctx.shadowBlur=0;
		ctx.clearRect(0,0,c.width,c.height);
		
		for (var i = 0; i < 7; i++) {
			ctx.beginPath();
			ctx.moveTo(-10, c.height/2);
			ctx.lineTo(c.width, endPoints[i]);
			ctx.stroke();
		}
		
		c = document.getElementById('inwire');
		ctx = c.getContext('2d');
		ctx.strokeStyle="#FFFFFF";
		ctx.lineWidth = 2;
		ctx.shadowBlur=5;
		ctx.shadowColor="#0000FF";
		
		for (var i = 0; i < 7; i++) {
			ctx.beginPath();
			ctx.moveTo(0, endPoints[i]);
			ctx.lineTo(c.width + 10, c.height/2);
			ctx.stroke();
		}
	
	},300);
	
	
	// Display the second set of wires unelectrified after 600 milliseconds.
	setTimeout(function(){
	
		c = document.getElementById('inwire');
		ctx = c.getContext('2d');
		ctx.strokeStyle="#666666";
		ctx.lineWidth = 1;
		ctx.shadowBlur=0;
		ctx.clearRect(0,0,c.width,c.height);
			
		for (var i = 0; i < 7; i++) {
			ctx.beginPath();
			ctx.moveTo(0, endPoints[i]);
			ctx.lineTo(c.width + 10, c.height/2);
			ctx.stroke();
		}
	
	},600);
	
	
	
	// Display the result after 900 milliseconds.
	setTimeout(function(){

		// Get all the switches.
		var switches = getElementsByClassName("on");
		
		// Work out which are on or off and build a binary text variable 
		// containing their state.
		var binaryStr = "";
		for (var i = 0; i < switches.length; i++) {
			if (switches[i].style["display"] == "none") { 
				binaryStr = binaryStr + "0"
			} else {
				binaryStr = binaryStr + "1"
			}
		}
		
		// Convert the binary text variable to a character
		var bin = parseInt(binaryStr, 2);

		// Display it.
		document.getElementById('result').innerHTML = String.fromCharCode(bin);
		
		// Display undepressed run button.
		document.getElementById("buttonOn").style["display"] = "none";
		document.getElementById("buttonOff").style["display"] = "block";
	}, 900);
	

}

