/**
* Sorts out IE getElementsByClassName issue. Uses default but drops to 
* Jonathan Snook's script if fails:
* https://snook.ca/archives/javascript/your_favourite_1
**/
function getElementsByClassName(className) {
	if (document.getElementsByClassName) { 
		return document.getElementsByClassName(className); 
	} else { 
		var a = [];
		var re = new RegExp('(^| )'+className+'( |$)');
		var els = document.getElementsByTagName("*");
		for(var i=0,j=els.length; i<j; i++)
			if (re.test(els[i].className)) a.push(els[i]);
		return a;
	}
	 
}

/**
* Recursively collects all children of an element.
**/ 
function getAllChildren(element, elements) {

	if ((element.nodeType == 1) || (element.nodeType == 3)) elements.push(element);
	
	if (element.hasChildNodes()) {
		for (var i = 0; i < element.childNodes.length; i++) {
			elements.push(getAllChildren(element.childNodes[i], elements));
		}
	} 
	
	return elements;

}