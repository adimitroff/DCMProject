/**
 * To use this class you have to create this HTML object - <object id="id_LfdSystem" classid="clsid:SAMSUNG-INFOLINK-SEF">    </object>
 * and to put the "id" tag of the object like argument in the "init" function
 */

var lfdSystemObj;

var LfdSystem =
{
};

//First parameter is the id of the html object
LfdSystem.initObj = function( Str_Id_Html_Obj )
{
	lfdSystemObj = document.getElementById( Str_Id_Html_Obj );
	lfdSystemObj.Open("LFD", "1.000", "LFD");
	lfdSystemObj.OnEvent = LfdSystem.lfdOnEvent;
};

LfdSystem.close = function()
{
	lfdSystemObj.Close();
};

LfdSystem.rebootApp = function( strLogMessage )
{
	lfdSystemObj.Execute("rebootSystemCmd", strLogMessage );
};

LfdSystem.sendLFDMsg = function( hexMessage )
{
	return lfdSystemObj.Execute("SendLFDMsg", hexMessage );
};

LfdSystem.getSystemUpTime = function( str )
{
	lfdSystemObj.Execute("getSystemUpTime", str);
};

LfdSystem.lfdDisplayOn = function()
{
	return LfdSystem.sendLFDMsg( "F9000100" );
};

LfdSystem.lfdDisplayOff = function()
{
	return LfdSystem.sendLFDMsg( "F9000101" );
};

LfdSystem.lfdOnEvent = function( eventType, param1, param2 )
{
	App.logInfo( "Event[" + eventType + "].param1[" + param1 + "].param2[" + param2 + "]" );
};
