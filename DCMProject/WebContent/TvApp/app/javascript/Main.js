var requestCounter; // Global variable
var currentDataId = 1; // Global variable
var isAppUpdating;
var lastUpdateTime;

var Main =
{

};

Main.debugInfo = function(message)
{
	if(TEST_MODE && document.getElementById("id_debugLog")) {
		var debugHtml = document.getElementById("id_debugLog").innerHTML;

		var countRows = (debugHtml.match(/<br>/g) || []).length;
		// allow only 21 debug rows
		if(countRows > 20) {
			// clear first row
			var indexOfEndOfFirstRow = debugHtml.indexOf("<br>");
			document.getElementById("id_debugLog").innerHTML = debugHtml.substring(indexOfEndOfFirstRow + 4);
		}

		document.getElementById("id_debugLog").innerHTML += (message + "<br>");
	}
};

Main.clearDebugInfo = function()
{
	if(document.getElementById("id_debugLog")) {
		document.getElementById("id_debugLog").innerHTML = "";
	}
};

Main.onLoad = function()
{
	Main.debugInfo("Main.onLoad()");
	if(document.getElementById("id_debugLog")) {
		if(TEST_MODE) {
			document.getElementById("id_debugLog").style.display = "block";
		} else {
			document.getElementById("id_debugLog").style.display = "none";
		}
	}
	requestCounter = 0;
	isAppUpdating = false;
	
	// Load app
	App.onLoad();
	// Check for update every half second (500ms)
	setInterval(Main.onUpdate, 500);
};

Main.onUnload = function()
{
	App.logInfo("onUnload()");
	App.onUnload();
};


Main.onUpdate = function()
{
	// check for new version
	App.update(SERVER_URL);
};



////////////////////////////////////////////////////////////////////////////////////
//End of file
//////////////////////////////
Main.debugInfo("Main.js loaded.");
