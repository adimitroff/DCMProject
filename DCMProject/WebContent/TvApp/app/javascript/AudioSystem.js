/**
 * To use this class you have to create this HTML object - <object id="id_AudioSys" border="0" classid="clsid:SAMSUNG-INFOLINK-SEF" style="opacity: 0.0; background-color: #000; width: 0px; height: 0px;"></object>
 * and to put the "id" tag of the object like argument in the "init" function 
 */

var audioSystemObj;

var AudioSystem =
{
};

//First parameter is the id of the html object
AudioSystem.initObj = function( strIdHtmlObj )
{
	audioSystemObj = document.getElementById(strIdHtmlObj);
	audioSystemObj.Open("Audio","1.000", "Audio");
};

AudioSystem.closeObj = function()
{
	audioSystemObj.Close();
};

AudioSystem.getVolume = function()
{
	return audioSystemObj.Execute("GetVolume");
};

AudioSystem.setMute = function()
{
	return audioSystemObj.Execute("SetUserMute",1);
};

AudioSystem.setUnMute = function()
{
	return audioSystemObj.Execute("SetUserMute",0);
};

AudioSystem.setVolume = function( intVolume )
{
	return audioSystemObj.Execute("SetVolume",intVolume);
};






