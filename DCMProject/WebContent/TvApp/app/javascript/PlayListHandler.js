/**
 * 
 */

var PlayListHandler =
{

};

PlayListHandler.playList;
PlayListHandler.currentFileN = 0;
PlayListHandler.localResources;

PlayListHandler.shiftTimeS;
PlayListHandler.currentIntervalId;

PlayListHandler.videoTagId;
PlayListHandler.imageTagId;

PlayListHandler.initObj = function( strImageTagId, strVideoTagId, pList, strLocalResPath, intShiftTimeInSeconds )
{
	App.logInfo( "Init play list ..." );
	PlayListHandler.imageTagId = strImageTagId;
	PlayListHandler.videoTagId = strVideoTagId;
	PlayListHandler.playList = pList;
	PlayListHandler.localResources = strLocalResPath;
	PlayListHandler.shiftTimeS = intShiftTimeInSeconds;
	PlayListHandler.currentFileN = 0;
};

PlayListHandler.startPlayList = function()
{
	if(PlayListHandler.currentIntervalId == 0 ){
		PlayListHandler.currentIntervalId = setInterval(PlayListHandler.slidePlayList, PlayListHandler.shiftTimeS*1000);
	} else {
		clearInterval( PlayListHandler.currentIntervalId );
		PlayListHandler.currentIntervalId = setInterval(PlayListHandler.slidePlayList, PlayListHandler.shiftTimeS*1000);
	}
	
	PlayListHandler.slidePlayList();
};

PlayListHandler.stopPlayList = function()
{
	if( PlayListHandler.currentIntervalId != 0 ){
			clearInterval( PlayListHandler.currentIntervalId );
	}
	PlayListHandler.currentIntervalId = 0;
	PlayListHandler.hideVideo();
	PlayListHandler.hideImage();

};


PlayListHandler.slidePlayList = function()
{
	if( PlayListHandler.playList.length > 0 ){
		if( PlayListHandler.playList.length > PlayListHandler.currentFileN ){
			var fileInfo = PlayListHandler.playList[PlayListHandler.currentFileN];
			PlayListHandler.playFile(fileInfo);
	 	} else {
			PlayListHandler.currentFileN = 0;
			var fileInfo = PlayListHandler.playList[PlayListHandler.currentFileN];
			PlayListHandler.playFile(fileInfo);
		}
	} else {
		if(PlayListHandler.currentIntervalId != 0 ){
			clearInterval( PlayListHandler.currentIntervalId );
		}
	}
};

PlayListHandler.playFile = function( fileInfo )
{
	PlayListHandler.currentFileN ++;

	var srcF = "";
	if( fileInfo.location.indexOf('local') !== -1 ){
		srcF = PlayListHandler.localResources + fileInfo.name;
	} else {
		srcF = fileInfo.src; // remote url
	}

	App.logInfo( "Play file ..." + srcF);
	if( fileInfo.type.indexOf('image') !== -1 ){
		PlayListHandler.playImage( srcF );
	} else if( fileInfo.type.indexOf('video') !== -1 ){
		PlayListHandler.playVideo( srcF );
	}
};

PlayListHandler.playImage = function( imagePath )
{

	// if( ( PlayListHandler.currentFileN - 2 ) >= 0 && PlayListHandler.playList[PlayListHandler.currentFileN - 2].indexOf('mp4') !== -1 )
	// 	PlayListHandler.hideVideo();
//	PlayListHandler.hideImage();
	var image = document.getElementById(PlayListHandler.imageTagId);
	image.src = imagePath;
	image.className = "";
//	image.style.opacity = ""+ 0;
//	fadeImage( PlayListHandler.imageTagId );
//	image.style.position = 'absolute';
//	image.style.left = 0 + 'px';
//	image.style.top = 0 + 'px';
//	image.width = 640;
//	image.height = 480;
};

PlayListHandler.playVideo = function( videoPath )
{
//	PlayListHandler.hideImage();

	clearInterval( PlayListHandler.currentIntervalId );
	PlayListHandler.currentIntervalId = 0;

	var video = document.getElementById(PlayListHandler.videoTagId);
//	video.addEventListener( 'ended', PlayListHandler.videoHandler, false );
	video.src = videoPath;
//	video.style.position = 'absolute';
//	video.style.display ="block";
//	video.style.left =  0 + 'px';
//	video.style.top = 0 + 'px';
//	video.width = 640;
//	video.height = 480;
	video.autoplay = true;
	video.volume = 0.1;
	video.className = "";
	PlayListHandler.hideImage();
};

PlayListHandler.videoHandler = function( ) //e
{
	PlayListHandler.hideVideo();
	PlayListHandler.startPlayList();
};

PlayListHandler.hideVideo = function()
{
	App.logInfo( "Hide video ..." );
	if(document.getElementById(PlayListHandler.videoTagId)) {
		var video = document.getElementById(PlayListHandler.videoTagId);
		video.className = "hidden";
		video.pause();
	}
};

PlayListHandler.hideImage = function()
{
	App.logInfo( "Hide image ..." );
	if(document.getElementById(PlayListHandler.imageTagId)) {
		var image = document.getElementById(PlayListHandler.imageTagId);
		image.className = "hidden";
	}
};


function fadeImage( tagId )
{
	var elem = document.getElementById(tagId);   
	var value = 1;
	var id = setInterval(frame, 50);
	function frame() {
	  if (value == 20) {
	    clearInterval(id);
	  } else {
	    value++; 
	    elem.style.opacity = ""+ (value/100)*5;
	  }
	}
}




