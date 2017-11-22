/**
 * To use this class you have to create this HTML object - <object id="id_Basic_Player" classid="clsid:SAMSUNG-INFOLINK-SEF"></object>
 * and to put the "id" tag of the object like argument in the "init" function
 */

var playerObj;

var BasicPlayer =
{
};

//First parameter is the id of the html object
BasicPlayer.initObj = function( strIdHtmlObj )
{
	playerObj = document.getElementById(strIdHtmlObj);
	playerObj.Open("Player","1.000", "Player");

	App.logInfo("<BasicPlayer> : Area:" + playerObj.Execute("SetDisplayArea", 10, 300, 940, 520) 
			+ " !!! " + " Buffer :" + playerObj.Execute("SetInitialBufferSize", 400*1024) );
};

BasicPlayer.closeObj = function()
{
	playerObj.Close();
};

BasicPlayer.start = function(videoUrl)
{
	playerObj.Execute("InitPlayer", videoUrl );
	return playerObj.Execute("StartPlayback");
};

BasicPlayer.stop = function()
{
	return playerObj.Execute("Stop");
};

BasicPlayer.pause = function()
{
	return playerObj.Execute("Pause");
};

BasicPlayer.resume = function()
{
	return playerObj.Execute("Resume");
};
