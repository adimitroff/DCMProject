var smartDevice;
var countFilesToDownload;
var douwnloadFileCounter;


var App =
{

};

App.updateUrl;
App.jsFiles;
App.playList;
App.dataRssFeed;
App.rssFeedPlayer;

App.onLoad = function()
{
	App.logInfo("App.onLoad()");
	App.updateUrl = SERVER_URL;
	App.playList = [];
	App.jsFiles = [];
	smartDevice = SSSP;
	smartDevice.init();
	
	App.logInfo("Used size(kB): " + smartDevice.getUsedSize());
	App.logInfo("Free size(kB): " + smartDevice.getFreeSize());
};

App.initContent = function()
{
	App.logInfo("App.initContent()");
	isAppUpdating = false;
	if(TEST_MODE) {
//		var img = document.getElementById("fullScreenImg");
//		img.src = "file://" + APP_RESOURCE_PATH + "test_image.jpg";//"https://vergecampus.com/wp-content/uploads/2015/11/hunger-games-katniss.jpg";
//		img.className = '';
//		setTimeout(function() {
//			App.hidePlayers();
//			var video = document.getElementById("fullScreenVideo");
//			video.src = TEST_VIDEO_URL;
//			video.className = '';
//			video.autoplay = true;
//		}, 4000);
//		setTimeout(App.hidePlayers, 7000);
//		setTimeout(App.hidePlayers, 8000);
//		setTimeout(function() {
//			var img = document.getElementById("fullScreenImg");
//			img.classList.remove("hidden");
//		}, 9000);
		// Make tests
		smartDevice = SSSP;
		smartDevice.init();
	}
};


App.onUnload = function()
{
	App.logInfo("App.onUnload()");
	smartDevice.close();
};

App.logInfo = function(message)
{
	console.log("[INFO] " + message);
	Main.debugInfo("[INFO] " + message);
	if(ENABLE_WEB_LOG) {
		App.httpGet(App.updateUrl + "logInfo=[INFO] " + message);
	}
};

App.logWarning = function(message)
{
	console.log("[WARNING] !!! " + message + " !!!");
	Main.debugInfo("[WARNING] !!! " + message);
	if(ENABLE_WEB_LOG) {
		App.httpGet(App.updateUrl + "logWarning=[WARNING] " + message);
	}
};

App.hidePlayers = function()
{
	var elem = document.getElementById("fullScreenImg");
	if(elem) {
		elem.classList.add("hidden");
	}
	elem = document.getElementById("fullScreenVideo");
	if(elem) {
		elem.classList.add("hidden");
	}
//	elem = document.getElementById("rssFeed");
//	if(elem) {
//		elem.classList.add("hidden");
//	}
	App.stopHtmlVideo();
	App.stopPlayList();
	App.stopBasicPlayer();
//	App.stopRssFeed();
};

App.loadScript = function (url, callback)
{
    // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
};

App.update = function(url)
{
	requestCounter++;
	if(requestCounter > 2147483000) {
		requestCounter = 1000;
	}
	
	var mediaName = "";
	var mediaTime = "";
	
	if( SSSP.originalPlayList.length > 0 ){
		var fileInfo;
		if( PlayListHandler.currentFileN != 0 && PlayListHandler.currentFileN <= SSSP.originalPlayList.length ){
			fileInfo = SSSP.originalPlayList[PlayListHandler.currentFileN - 1];
	 	} else {
			fileInfo = SSSP.originalPlayList[0];
		}
		
		mediaName = fileInfo.name;
		if( fileInfo.type.indexOf('video') !== -1 ){
			var video = document.getElementById( "fullScreenVideo" );
			mediaTime = parseInt( video.currentTime );
		}
	}
	
	// Check for new update if the app isn't waiting for update or it is waiting > 3 min.
	if(!isAppUpdating || new Date().getTime() > (lastUpdateTime + (3 * 60 * 1000)) ) {
		isAppUpdating = true;
		LfdSystem.lfdDisplayTemp();
		lastUpdateTime = new Date().getTime();
		App.updateUrl = url;
//		App.logInfo("update() url: " + url );
		var ipAddress = smartDevice.getNetworkIPAddress();
		App.httpGet(url + "devfeed?request=" + requestCounter
				+ "&ip=" + ipAddress
				+ "&dataId=" + currentDataId
				+ "&usedSize=" + smartDevice.getUsedSize()
				+ "&freeSize=" + smartDevice.getFreeSize()
				+ "&mediaName=" + mediaName
				+ "&mediaTime=" + mediaTime
				+ "&temperature=" + LfdSystem.currentTemp
				+ "&serialN=" + LfdSystem.deviceSerialNumber
				+ "&display=" + LfdSystem.displayStatus,
				App.onNewData_ReadyStateChange);
	}
};

App.onNewData_ReadyStateChange = function() {
	if (this.readyState == 4) { 
		if (this.status == 200) {
			var data = JSON.parse(this.responseText);
			if(data.redirectUrl.length > 0) {
				// Update from redirect url
				isAppUpdating = false;
				App.update(data.redirectUrl);
			} else if(data.dataId > currentDataId) {
				currentDataId = data.dataId;
				Main.clearDebugInfo();
				App.logInfo("New data version: " + currentDataId);
				
				switch(data.dataType) {
				case "new_version": 
				{
					// Just wait few ms and start update to new version
					setTimeout(function(){
						App.jsFiles = data.jsFiles;
						// Check for new html content
						if(data.htmlContent.length > 0) {
							if(data.htmlContent.indexOf(".html") == (data.htmlContent.length - 5)) {
								// Get html content from server file
								App.logInfo("Updating html content from: " + data.htmlContent);
								App.httpGet(App.updateUrl + data.htmlContent + "?version=" + requestCounter, App.onNewContent_ReadyStateChange);
							} else {
								App.updateHtmlContent(data.htmlContent);
								App.loadJsFiles();
							}
						} else {
							// Load js files
							App.loadJsFiles();
						}
					}, 200);
					break;
				}
				case "command":
				{
					App.dataRssFeed = data.rssFeed;
					// List and execute commands
					var commands = data.commands;
					for(var i = 0; i < commands.length; i++) {
						if(commands[i].indexOf("!") > 0) {
							// Do not execute command ending with '!'
							continue;
						}
						App.executeCommand( commands[i] );
					}
					isAppUpdating = false;
					break;
				}
				case "playlist": 
				{
					var iSettings = data.imageGlobalSettings;
					var vSettings = data.videoGlobalSettings;
					App.playList = data.playListFiles;

					App.logInfo( "Loaded files in the playlist:" );
					for(var i = 0; i < App.playList.length; i++) {
						App.logInfo( App.playList[i].src );
					}
					
					smartDevice.startDownloadListOfFiles(App.playList);
					isAppUpdating = false;
					break;
				}
				default:
					isAppUpdating = false;
				}
			} else {
				isAppUpdating = false;
			}
		} else {
			isAppUpdating = false;
		}
	}
};

App.loadJsFiles = function()
{
	countFilesToDownload = 0;
	douwnloadFileCounter = 0;
	if(!App.jsFiles) {
		App.initContent();
		return;
	}
	// Get js files
	countFilesToDownload = App.jsFiles.length;
	for(var i = 0; i < App.jsFiles.length; i++) {
		App.loadScript(App.updateUrl + App.jsFiles[i] + "?version=" + requestCounter,
				function() { App.onJsFileLoaded();}
		);
	}
};

App.onJsFileLoaded = function()
{
	douwnloadFileCounter++;
	App.logInfo("DouwnloadFileCounter: " + douwnloadFileCounter);
	if(douwnloadFileCounter == countFilesToDownload) {
		countFilesToDownload = 0;
		douwnloadFileCounter = 0;
		App.initContent();
	}
};

App.onNewContent_ReadyStateChange = function()
{
	if (this.readyState == 4) { 
		App.logInfo("onNewContent_ReadyStateChange");
		if (this.status == 200) { 
			App.updateHtmlContent(this.responseText);
			App.loadJsFiles();
		} else {
			App.loadJsFiles();
		}
	}
};

App.updateHtmlContent = function(htmlContent)
{
	App.logInfo("Updating html content.");
	var content = document.getElementById("content");
	content.innerHTML = htmlContent;
};


App.httpGet = function(url, callback)
{
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = callback;
	xhttp.open("GET", url, true);
	xhttp.send();
};

App.executeCommand = function( strCommandName )
{
	if( strCommandName == "reboot"){
		App.rebootApp();
	} else if( strCommandName == "lfdDisplayOn" ){
		App.lfdDisplayOn();
	} else if( strCommandName == "lfdDisplayOff" ){
		App.lfdDisplayOff();
	} else if( strCommandName == "startHtmlVideo" ){
		App.startHtmlVideo();
	} else if( strCommandName == "stopHtmlVideo" ){
		App.stopHtmlVideo();
	} else if( strCommandName == "audio1" ){
		App.audio1();
	} else if( strCommandName == "audio2" ){
		App.audio2();
	} else if( strCommandName == "basicPlayer" ){
		App.basicPlayer();
	} else if( strCommandName == "stopBasicPlayer" ){
		App.stopBasicPlayer();
	} else if( strCommandName == "showPicture" ){
		App.showPicture();
	} else if( strCommandName == "hidePicture" ){
		App.hidePicture();
	} else if( strCommandName == "captureScreen" ){
		App.captureScreen();
	} else if( strCommandName == "lfdPlayer" ){
		App.lfdPlayer();
	} else if( strCommandName == "getIp" ){
		App.getIp();
	} else if(strCommandName == 'getHost') {
		App.logInfo("Server URL: " + smartDevice.getServerURL());
	} else if( strCommandName == "downloadTest" ){
		App.downloadTest();
	} else if( strCommandName == "downloadTestImage" ){
		App.downloadTestImage();
	} else if( strCommandName == "deleteDownloadTest" ){
		App.deleteDownloadTest();
	} else if( strCommandName == "startPlayList" ){
		App.startPlayList();
	} else if( strCommandName == "stopPlayList" ){
		App.stopPlayList();
	} else if( strCommandName == "loadRssFeed" ) {
		App.loadRssFeed();
	} else if( strCommandName == "stopRssFeed" ) {
		App.stopRssFeed();
	} else if(strCommandName.indexOf("!") == -1) {
		App.logInfo("Executed command is not supported : " +  strCommandName );
	}
	
};

//Custom test commands ---------------------------------------------------------------------------------------
App.loadRssFeed = function() 
{	
	// first stop previous rss
	if(App.rssFeedPlayer) {
		App.rssFeedPlayer.stop();
	}
	App.logInfo("loadRssFeed()" );
	if(!App.dataRssFeed) {
		App.logWarning("loadRssFeed() - No data" );
		return;
	}
	
	if(App.dataRssFeed.url.length > 0) {
		// Request rss feed from url
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				App.logInfo("loadRssFeed() - onreadystatechange()" );
				App.rssFeedPlayer = new RssFeed(this.responseText, "rssFeed");
				App.rssFeedPlayer.play(8000);
//				var parser = new DOMParser();
//				var xmlDoc = parser.parseFromString(this.responseText, 'text/xml');
//				var items = xmlDoc.getElementsByTagName('item');        
//				nextItemFeed( items );	        
			}
		};
	  
		xhttp.open("POST", App.dataRssFeed.url, true);
		xhttp.send();
	} else {
		// Use rss xml content
    	App.rssFeedPlayer = new RssFeed(App.dataRssFeed.xmlContent, "rssFeed");
    	App.rssFeedPlayer.play(8000);
	}
};

App.stopRssFeed = function()
{
	if(App.rssFeedPlayer) {
		App.rssFeedPlayer.stop();
	}
};

//function nextItemFeed( itemsList )
//{   
//	var value = 0;
//	var id = setInterval(frame, 10*1000);
//	function frame() {
//	  if (itemsList.length == value) {
//	    clearInterval(id);
//	  } else {
//	    document.getElementById("id_rssText").innerHTML = "";
//	    myLoop(itemsList[value]);
//	    value++; 
//	  }
//	}
//}
//
//function myLoop(x) 
//{
//  var i, y, xLen, txt;
//  txt = "";
//  x = x.childNodes;
//  xLen = x.length;
//  for (i = 0; i < xLen ;i++) {
//    y = x[i];
//    if (y.nodeType != 3) {
//      if (y.childNodes[0] != undefined) {
//        txt += myLoop(y);
//      }
//    } else {
//    	document.getElementById("id_rssText").innerHTML += y.nodeValue + "<br>";
//    }
//  }
//  return txt;
//}

App.startPlayList = function()
{
	App.hidePlayers();
	App.logInfo("Startting playlist!" );
	smartDevice.startDownloadListOfFiles(App.playList);
};

App.stopPlayList = function()
{
	App.logInfo("Stop playlist ...!" );
	PlayListHandler.stopPlayList();
};

App.rebootApp = function()
{
	App.logInfo("Rebooting system ...!" );
	smartDevice.reboot();
};

App.lfdDisplayOn = function()
{
	App.logInfo("Display on ..." );
	smartDevice.lfdDisplayOn();
};

App.lfdDisplayOff = function()
{
	App.logInfo("Display off ..." );
	smartDevice.lfdDisplayOff();
};

App.deleteDownloadTest = function()
{
	App.logInfo("Start deleting file (test_video)...");
	App.logInfo("Is file test_video.mp4 exist:" + smartDevice.isFileExist(APP_RESOURCE_PATH + 'test_video.mp4') );
	App.logInfo("Is file test_image_1.jpg exist:" + smartDevice.isFileExist(APP_RESOURCE_PATH + 'test_image.jpg') );

	App.logInfo("Delete result:" + smartDevice.deleteFile( APP_RESOURCE_PATH + 'test_video.mp4' ) );
	App.logInfo("Delete result:" + smartDevice.deleteFile( APP_RESOURCE_PATH + 'test_image.jpg' ) );

	App.logInfo("Is file test_video.mp4 exist:" + smartDevice.isFileExist( APP_RESOURCE_PATH + 'test_video.mp4') );
	App.logInfo("Is file test_image_1.jpg exist:" + smartDevice.isFileExist( APP_RESOURCE_PATH + 'test_image.jpg') );
};

App.startHtmlVideo = function()
{
	App.hidePlayers();
	App.logInfo("Play video ..." );

	var video = document.createElement('video');
	video.setAttribute("id", "id_htmlVideo");
	document.getElementById("content").appendChild(video);
	video.src = TEST_VIDEO_URL;
	video.style.position = 'absolute';
	video.style.display ="block";
	video.style.left =  100 + 'px';
	video.style.top = 280 + 'px';
	video.width = 640;
	video.height = 480;
	video.autoplay = true;
	video.volume = 0.1;

};

App.stopHtmlVideo = function()
{
	if(document.getElementById("id_htmlVideo")) {
		var video = document.getElementById("id_htmlVideo");
		video.parentNode.removeChild(video);
	}
};

App.audio1 = function()
{
	App.logInfo("Set low level(4) audio ..." );
	smartDevice.setVolume(4);
};

App.audio2 = function()
{
	App.logInfo("Set middle level(40) audio ..." );
	smartDevice.setVolume(40);
};

App.basicPlayer = function()
{
	App.hidePlayers();
	
	App.logInfo("Start basicPlayer ..." );
//	document.body.className = 'no_background';
	smartDevice.startVideoPlay(TEST_VIDEO_URL);
};

App.stopBasicPlayer = function()
{
	App.logInfo("Stop basicPlayer ..." );
	document.body.className = '';
	smartDevice.stopVideoPlay();

};

App.showPicture = function()
{
	App.logInfo("Show picture ..." );

	var htmlImage = document.createElement('img');
	htmlImage.setAttribute("id", "id_htmlImage");
	document.body.appendChild(htmlImage);
	htmlImage.src = SERVER_URL + "images/mtel_760.png";
	htmlImage.style.position = 'absolute';
	htmlImage.style.left = '100px';
	htmlImage.style.top = '80px';
};

App.hidePicture = function()
{
	App.logInfo("Hide picture ..." );

	if(document.getElementById("id_htmlImage")) {
		var image = document.getElementById("id_htmlImage");
		image.parentNode.removeChild(image);
	}
};

App.captureScreen = function()
{
	App.logInfo("Capture screen ..." );

	var imagePath = smartDevice.captureCurrentScreen();

	var htmlImage = document.createElement('img');
	document.body.appendChild(htmlImage);
	htmlImage.src = "file://" + imagePath + "?time="+new Date();
	htmlImage.style.position = 'absolute';
	htmlImage.style.left =  480+ 'px';
	htmlImage.style.top = 240 + 'px';
	htmlImage.style.display ="block";
	htmlImage.width = 640;
	htmlImage.height = 360;

	// Upload after capture
//	App.logInfo("Init upload");
//	FileUploader.initObj( "id_uploader", "id_debugLog");
//	App.logInfo("Start Upload");
//	App.logInfo( "Upload resource" + FileUploader.uploadFile(imagePath) );

};

App.getFirmware = function()
{
	return smartDevice.getFirmware();
};

App.lfdPlayer = function()
{
	App.hidePlayers();
	App.logInfo("Start lfdPlayer ..." );

	smartDevice.pushPlayList(TEST_VIDEO_URL);
	smartDevice.startPlayList(960, 0, 960, 540);

};

App.getIp = function()
{
	var ip = smartDevice.getNetworkIPAddress();
	App.logInfo("IP: " +  ip );
};

App.downloadTest = function()
{
	App.logInfo("Start download file (test_video)...");
	smartDevice.startDownloadFile(TEST_DOWNLOAD_VIDEO, 'test_video.mp4');
};

App.downloadTestImage = function()
{
	App.logInfo("Start download file (test_image)...");
	smartDevice.startDownloadFile(TEST_DOWNLOAD_IMAGE_URL, 'test_image.jpg');
};
//End test commands ---------------------------------------------------------------------------------------

App.keyDown = function()
{
//	if(document.body.className) {
//		document.body.className = '';
//	} else {
//		document.body.className = 'hidden';
//	}
	var keyCode = event.keyCode;
	App.logInfo("Key pressed: " + keyCode);
	smartDevice.keyEvent(keyCode);

};

//App.downloadFile = function(sourceUrl, fileName)
//{
//	if(!downloading) {
//		downloading = true;
//		smartDevice.downloadFile(sourceUrl, fileName);
//	} else {
//		App.logWarning("downloadFile() Can't downlod file " + sourceUrl + ". Waiting previous download to complete.");
//	}
//};
//
//App.onDownloadComplete = function(status)
//{
//	downloading = false;
//};
//
//App.onDownloadProgress = function(progress)
//{
//
//};
//
//App.onDownloadSpeed = function(speed)
//{
//
//};


////////////////////////////////////////////////////////////////////////////////////
//End of file
//////////////////////////////
App.logInfo("App.js loaded.");
