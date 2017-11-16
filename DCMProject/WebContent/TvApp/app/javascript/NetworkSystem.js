/**
 * To use this class you have to create this HTML object - <object id="id_Network" classid="clsid:SAMSUNG-INFOLINK-SEF">    </object>
 * and to put the "id" tag of the object like argument in the "init" function 
 */

var networkSysObj;

var NetworkSystem =
{
};

//First parameter is the id of the html object
NetworkSystem.initObj = function( Str_Id_Html_Obj )
{
	networkSysObj = document.getElementById(Str_Id_Html_Obj);
	networkSysObj.Open("Network", "1.000", "Network");
	
};

NetworkSystem.closeObj = function()
{
	networkSysObj.Close();
};

NetworkSystem.getIp = function()
{
	return networkSysObj.Execute("GetIP", "1");
};