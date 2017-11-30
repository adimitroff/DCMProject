/**
 * To use this class you have to create this HTML object - <object id="id_LfdSystem" classid="clsid:SAMSUNG-INFOLINK-SEF">    </object>
 * and to put the "id" tag of the object like argument in the "init" function
 */

var COMMAND_DISPLAY_STATUS_RESPONSE_SIZE = 22;
var COMMAND_SERIAL_NUMBER_RESPONSE_SIZE = 46;

var lfdSystemObj;

var LfdSystem =
{
};

LfdSystem.currentTemp = 0;
LfdSystem.deviceSerialNumber = "";

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

LfdSystem.lfdDisplayTemp = function()
{
	return LfdSystem.sendLFDMsg( "0D0000" );  
};

LfdSystem.lfdDisplaySerialNumber = function()
{
	return LfdSystem.sendLFDMsg( "0B0000" );
};

LfdSystem.lfdOnEvent = function( eventType, param1, param2 )
{
//	App.logInfo( "Event[" + eventType + "].param1[" + param1 + "].param2[" + param2 + "]" );
	if( typeof param1 == "string" ){
		if( param1.length == COMMAND_DISPLAY_STATUS_RESPONSE_SIZE ){
			LfdSystem.currentTemp = LfdSystem.extractTemperatureFromTvResponse( param1 );
//			App.logInfo( "Current Temperature[" + LfdSystem.currentTemp + "]");
		} else if( param1.length == COMMAND_SERIAL_NUMBER_RESPONSE_SIZE ){
			LfdSystem.deviceSerialNumber = LfdSystem.extractSerialNumberFromTvResponse( param1 );
//			App.logInfo( "Serial number[" + LfdSystem.deviceSerialNumber + "]");
		}
	}
	
};

LfdSystem.extractSerialNumberFromTvResponse = function( response )
{
	var serialNumberInHex = response.substring( 10, COMMAND_SERIAL_NUMBER_RESPONSE_SIZE - 6);
//	App.logInfo( "Serial numberHex[" + serialNumberInHex + "]");
	var serialNumberInString = "";
	for( var i=0; i < serialNumberInHex.length; i+=2 ){
		var hexNumber = serialNumberInHex.substring( i, i+2 );
		var decNumber = hextoDec( hexNumber );
		serialNumberInString += String.fromCharCode(decNumber);
	}
	
	return serialNumberInString;
};

LfdSystem.extractTemperatureFromTvResponse = function( response )
{
	var tempInHex = response.substring(COMMAND_DISPLAY_STATUS_RESPONSE_SIZE - 4, COMMAND_DISPLAY_STATUS_RESPONSE_SIZE - 2);
	return hextoDec( tempInHex );
};

//Convert Hex to Decimal.
function hextoDec(val) 
{
	 // Reversed the order because the added values need to 16^i for each value since 'F' is position 1 and 'E' is position 0
	 var hex = val.split('').reverse().join('');
	 // Set the Decimal variable as a integer
	 var dec = 0;
	 // Loop through the length of the hex to iterate through each character
	 for (i = 0; i < hex.length; i++) {
	     // Obtain the numeric value of the character A=10 B=11 and so on..
	     // you could also change this to var conv = parseInt(hex[i], 16) instead
	     var conv = '0123456789ABCDEF'.indexOf(hex[i]);
	   
	     // Calculation performed is the converted value * (16^i) based on the position of the character
	     // This is then added to the original dec variable.  'FE' for example
	     // in Reverse order [E] = (14 * (16 ^ 0)) + [F] = (15 * (16 ^ 1)) 
	     dec += conv * Math.pow(16, i);
	 }
	 // Returns the added decimal value
	 return dec;
}
