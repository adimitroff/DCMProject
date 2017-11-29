var widgetAPI = new Common.API.Widget();
var tvKey = new Common.API.TVKeyValue();

var pluginObjSEF;
//var pluginDownload;

var SSSP =
{
};

SSSP.currentFilesForDownload = [];
SSSP.originalPlayList = [];
SSSP.isInitialized = 0;

SSSP.init = function()
{
	if(SSSP.isInitialized) {
		return;
	}
	this.logInfo("init()");

	widgetAPI.sendReadyEvent();


	pluginObjSEF = document.getElementById("pluginObjSEF");

	LfdSystem.initObj("id_LfdSystem");
	LfdSystem.lfdDisplaySerialNumber();
	AudioSystem.initObj("id_AudioSys");
	NetworkSystem.initObj("id_Network");
	BasicPlayer.initObj("id_Basic_Player");
//	LfdPlayer.initObj("id_lfd_player");
	FileSystemCustom.initObj("id_file_system");
	FileUploader.initObj("id_uploader");

	SSSP.isInitialized = 1;
};

SSSP.close = function()
{
	LfdSystem.close();
	AudioSystem.closeObj();
	NetworkSystem.closeObj();
	BasicPlayer.closeObj();
	LfdPlayer.closeObj();
	FileSystemCustom.closeObj();
	FileUploader.close();
};

SSSP.logWarning = function(message)
{
	App.logWarning("<SSSP 1.0> " + message);
};

SSSP.logInfo = function(message)
{
	App.logInfo("<SSSP 1.0> " + message);
};

SSSP.keyEvent = function(keyCode)
{

	switch(keyCode)
	{
		case tvKey.KEY_RETURN:
		case tvKey.KEY_PANEL_RETURN:
			SSSP.logInfo("KEY RETURN");
			widgetAPI.sendReturnEvent();
			break;
//		case tvKey.KEY_LEFT:
//			SSSP.logInfo("KEY LEFT");
//			break;
//		case tvKey.KEY_RIGHT:
//			SSSP.logInfo("KEY RIGHT");
//			break;
//		case tvKey.KEY_UP:
//			SSSP.logInfo("KEY UP");
//			break;
//		case tvKey.KEY_DOWN:
//			SSSP.logInfo("KEY DOWN");
//			break;
		case tvKey.KEY_ENTER:
		case tvKey.KEY_PANEL_ENTER:
			SSSP.logInfo("KEY ENTER");
			break;
		default:
			SSSP.logInfo("Unhandled key");
			break;
	}
};

SSSP.isEmulator = function()
{
	if(this.getFirmware() == "0") {
		this.logInfo("isEmulator(): TRUE");
		return true;
	}
	this.logInfo("isEmulator(): FALSE");
	return false;
};

SSSP.getFirmware = function()
{
	pluginObjSEF.Open('Device','1.000','Device');
	var firmware = pluginObjSEF.Execute('Firmware');
	pluginObjSEF.Close();
	this.logInfo("getFirmware(): " + firmware);
	return firmware;
};

SSSP.getServerURL = function()
{
	pluginObjSEF.Open('LFDControl', '1.000', 'LFDControl');
	var serverUrl = pluginObjSEF.Execute("GetURLLauncherAddress");;
	pluginObjSEF.Close();
	this.logInfo("getServerURL(): " + serverUrl);
	return serverUrl;
};

SSSP.reboot = function(strLogMessage)
{
	LfdSystem.rebootApp("Rebooting system!");
};

SSSP.lfdDisplayOn = function()
{
	LfdSystem.lfdDisplayOn();
};

SSSP.lfdDisplayOff = function()
{
	LfdSystem.lfdDisplayOff();
};

SSSP.isFileExist = function( filePath )
{
	return FileSystemCustom.isExistedPath( filePath );
};

SSSP.deleteFile = function( filePath )
{
	return FileSystemCustom.deletePathOrFile( filePath );
};

SSSP.getFreeSize = function()
{
	return FileSystemCustom.getFreeSize();
};

SSSP.getUsedSize = function()
{
	return FileSystemCustom.getUsedSize();
};

SSSP.getNetworkIPAddress = function()
{
	return NetworkSystem.getIp();
};

SSSP.getVolume = function()
{
	return AudioSystem.getVolume();
};

SSSP.setVolume = function(volume)
{
	var success = AudioSystem.setVolume(volume);
	return success;
};

SSSP.setMute = function(isMute)
{
	var success = 0;
	if(isMute) {
		AudioSystem.setMute();
	} else {
		AudioSystem.setUnMute();
	}
	return success;
};

/////////////////////////////////
// Video player

SSSP.startVideoPlay = function(videoUrl)
{
	return BasicPlayer.start(videoUrl);
};

SSSP.stopVideoPlay = function()
{
	return BasicPlayer.stop();
};

SSSP.pauseVideoPlay = function()
{
	return BasicPlayer.pause();
};

SSSP.resumeVideoPlay = function()
{
	return BasicPlayer.resume();
};


///////////////////////////////////
// Playlist

SSSP.stopPlayList = function()
{
	LfdPlayer.stopPlayList();
};

SSSP.startPlayList = function( Int_X, Int_Y, Int_Width, Int_Height)
{
	LfdPlayer.startPlayList(Int_X,Int_Y,Int_Width,Int_Height);
};

SSSP.pushPlayList = function( videoPath )
{
	LfdPlayer.pushPlayList(videoPath);
};

SSSP.clearPlayList = function()
{
	LfdPlayer.clearPlayList();
};


//////////////////////////////////
// Screen capture
SSSP.captureCurrentScreen = function()
{
	pluginObjSEF.Open("LFD", "1.000", "LFD");
	var genImgPath = pluginObjSEF.Execute("GetScreenCapture");
	pluginObjSEF.Close();

	// Copy file
	var destPath = '/mtd_down/common/';
	SSSP.logInfo( "Widget name : " + curWidget.id ); // global system variable
	SSSP.logInfo( "Widget free space : " + FileSystemCustom.getFreeSize() );

	SSSP.logInfo( "Copy generated image:" + FileSystemCustom.copy(genImgPath, destPath));
	var imgPath = '/mtd_down/common/capture_screen.jpg';
	SSSP.logInfo("Is file copied:" + FileSystemCustom.isExistedPath(imgPath) );

	var fileSysTest = new FileSystem();
	SSSP.logInfo( "Is picture exist: " + fileSysTest.isValidCommonPath( imgPath ) );

	return imgPath;
};

///////////////////////////////////
// Download

SSSP.startDownloadFile = function(fileUrl, fileName)
{
	FileSystemCustom.startDownFile(fileUrl, APP_RESOURCE_PATH + fileName, 0, 10 );
};

SSSP.startDownloadListOfFiles = function(listFilesInfo)
{
	SSSP.originalPlayList = listFilesInfo;	
	
	SSSP.logInfo( "Init list for downloads:" );
	var listOfNames = [];
	for( var i=0; listFilesInfo.length > i; i++){
		var fileInfo = listFilesInfo[i];
		if( fileInfo.location == "local") {
			listOfNames.push(fileInfo.name);
			if(FileSystemCustom.isExistedPath( APP_RESOURCE_PATH + fileInfo.name ) == 0 ) {
				SSSP.currentFilesForDownload.push(fileInfo);
			}
		}	
	}
	
	SSSP.logInfo( "Local files:" );
	var jsonFileList = FileSystemCustom.getListFiles( APP_RESOURCE_PATH );
	if( jsonFileList.length > 10 ){
		var listLocalF = JSON.parse(jsonFileList);
		
		// Just print local files
		for( var i=0; listLocalF.length > i; i++){
			SSSP.logInfo( "File" + i + " : " + listLocalF[i] );
		}
	
		// Delete previous downloaded local files
		for(var y=0; ( listLocalF.length - 1 ) > y; y++){
			if( listOfNames.indexOf(listLocalF[y]) === -1 ){
				SSSP.logInfo("Delete file:" + listLocalF[y] + "Success : " + SSSP.deleteFile( APP_RESOURCE_PATH + listLocalF[y] ) );
			}
		}
	}
	
	SSSP.logInfo( "Files for download : " + SSSP.currentFilesForDownload.length );

	FileSystemCustom.setOnCompleteCallBack( SSSP.nextFile );
	FileSystemCustom.setActivateCallBack( 'true' );
	SSSP.nextFile();
};

SSSP.nextFile = function()
{
	if( SSSP.currentFilesForDownload.length > 0 ){
		nextFile = SSSP.currentFilesForDownload.shift();
		FileSystemCustom.startDownFile( nextFile.src, APP_RESOURCE_PATH + nextFile.name, 0, 20 );
	} else {
		SSSP.currentFilesForDownload = [];
		FileSystemCustom.setActivateCallBack( 'false' );
		PlayListHandler.initObj( "fullScreenImg", "fullScreenVideo", SSSP.originalPlayList, "file://"+APP_RESOURCE_PATH, 10 );
		PlayListHandler.startPlayList();
	}
};

/// TODO test this also

//
//
//SSSP.onDownloadComplete = function(param1) {
//	this.logInfo("onDownloadComplete() " + param1);
//	var strList = param1.split('?');
//
//	if (strList[0] == '1000') {
//		if(strList[1] == '1') {
//			// success
//			this.logInfo("onDownloadComplete() success");
//			Main.onDownloadComplete(true);
//		} else {
//			// error
//			this.logWarning("onDownloadComplete() error");
//			Main.onDownloadComplete(false);
//		}
//	} else if (strList[0] == '1001' ) { // DownRatio : 0~100
//		Main.onDownloadProgress(strList[1]);
//	} else if (strList[0] == '1002' ) { // Down Speed : Bytes/Sec : It will be reach after Ratio
//		Main.onDownloadSpeed(strList[1]);
//	}
//};



////////////////////////////////////////////////////////////////////////////////////
//End of file
//////////////////////////////
App.logInfo("SSSP.js loaded.");
