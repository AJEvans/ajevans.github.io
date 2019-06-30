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
* Cookie getting code.
**/
function readCookie(name) {
	var cookie = document.cookie;
	var index = cookie.indexOf(name + "=");
	if (index < 0) return null;
	index = index + name.length + 1;
	var end = cookie.indexOf(";", index);
	if (end < 0) end = cookie.length;
	cookie = unescape(cookie.substring(index,end));
	return cookie;
}





/**
* Cookie setting code.
**/
function writeCookie(name,value,date,days) {
	date.setDate(date.getDate() + days);
	document.cookie = name + "=" + escape(value) + "; expires=" + date.toUTCString() + "; path=/";
}





/**
* Cookie setting code.
**/
function setTheme(theme) {
	writeCookie("theme", theme, new Date(), 365);
}



/**
* Cookie setting code.
**/
function setAntitheme(antitheme) {
	writeCookie("antitheme", antitheme, new Date(), 365);
}




/**
* Cookie deleting code.
**/
function deleteCookie() {
    document.cookie = "theme=; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/";
    document.cookie = "antitheme=; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/";
}





/**
* Find cookie and sets up page as needed.
**/
function getTheme() {
	return readCookie("theme");
}



/**
* Find cookie and sets up page as needed.
**/
function getAntitheme() {
	return readCookie("antitheme");
}



