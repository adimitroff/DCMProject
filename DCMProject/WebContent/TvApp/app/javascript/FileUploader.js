/**
 * To use this class you have to create this HTML object - <object id="id_uploader" classid="clsid:SAMSUNG-INFOLINK-SEF" style="opacity: 0.0; background-color: #000; width: 0px; height: 0px;"></object>
 * and to put the "id" tag of the object like argument in the "init" function 
 */

var fileUploaderObj;


var FileUploader =
{
};

//First parameter is the id of the html object
FileUploader.initObj = function( Str_Id_Html_Obj )
{
	fileUploaderObj = document.getElementById(Str_Id_Html_Obj);
//	FileUploader.debug("Initialization getId!");
	fileUploaderObj.Open("Download", "1.0", "Download");
	fileUploaderObj.OnEvent = OnFileUploadEvent;
//	FileUploader.debug("Initialization complete successfull!");
};

FileUploader.close = function()
{
	fileUploaderObj.Close();
};

FileUploader.makeHeader = function()
{
	var header = '';
	header += 'POST '+ UPLOAD_URL_PATH + ' HTTP/1.1\r\n';
	return header;
};

FileUploader.makeBody = function() 
{
	var body = '';
	body += '[[[FILE_BINARY]]]\r\n';
	body += '\r\r';
	return body;
};

FileUploader.uploadFile = function(str_path) {
//	FileUploader.debug("Start upload!");
	var header = this.makeHeader();
	var body = this.makeBody();
	var ratio = 10;
	var serverType = 1;
	
	var res = fileUploaderObj.Execute("StartUpload", SERVER_HOST, SERVER_PORT, header, body, str_path, 10, 1 );
//	FileUploader.debug("Upload result:" + res);
	return 	res;
};

function OnFileUploadEvent(event, data1, data2)
{
	App.logInfo('OnEventKHW1111() - ' +'event: ' + event + ', data1: ' + data1 + ', data2: ' + data2);
}


