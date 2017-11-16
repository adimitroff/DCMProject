/**
 * To use this class you have to create this HTML object - <object id="id_lfd_player" classid="clsid:SAMSUNG-INFOLINK-SEF"></object>
 * and to put the "id" tag of the object like argument in the "init" function 
 */

var lfdPlayerObj;

var str_video_local_path ;


var LfdPlayer =
{
};

//First parameter is the id of the html object for this player
LfdPlayer.initObj = function( Str_Id_Html_Obj )
{
	//g_listPlay = ["king.avi","yellow.WMV","3.wmv","blue.mp4","yellow.WMV"];
	lfdPlayerObj = document.getElementById(Str_Id_Html_Obj);	
	lfdPlayerObj.OnEvent = LfdPlayerOnEvent;
	lfdPlayerObj.Open("LFD","1.000","LFD");
};

//This function must be called on "onUnLoad" main event.
LfdPlayer.closeObj = function()
{
	lfdPlayerObj.Close();
};

//This function must be called on window "onHide" event.
LfdPlayer.stopPlayList = function()
{
	lfdPlayerObj.Execute("stopplaylist");
};

LfdPlayer.startPlayList = function( Int_X, Int_Y, Int_Width, Int_Height)
{
	lfdPlayerObj.Execute("startplaylist",Int_X,Int_Y,Int_Width,Int_Height);
};

LfdPlayer.pushPlayList = function( Str_Video_Path )
{
	lfdPlayerObj.Execute("pushplaylist", Str_Video_Path );
};

LfdPlayer.clearPlayList = function()
{
	lfdPlayerObj.g_playlist.Execute("clearplaylist");
};

/**
Player event handler
*/
function LfdPlayerOnEvent(eventType, param1, param2)
{
	App.logInfo( "LfdPlayerOnEvent() - PLAYLIST[" + eventType + "].param1[" + param1 + "].param2[" + param2 + "]");
}

