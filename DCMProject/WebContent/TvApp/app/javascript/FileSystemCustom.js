/**
 * To use this class you have to create this HTML object - <object id="id_file_system" classid="clsid:SAMSUNG-INFOLINK-SEF"></object>
 * and this <object id="id_download_system" classid="clsid:SAMSUNG-INFOLINK-DOWNLOAD" style="visibility:hidden; position: absolute; width: 0px; height: 0px;"></object>
 * and to put the "id" tag of the object like argument in the "init" function
 */

var fileSystemObj;
var downloadSysObj;

var DIRECTORY_BASE_SYSTEM = '/mtd_down';
var DIRECTORY_COMMON = DIRECTORY_BASE_SYSTEM + '/common';
var DIRECTORY_COMMON_WIDGET = DIRECTORY_COMMON + '/' + widget.id;
var DIRECTORY_WIDGET = DIRECTORY_BASE_SYSTEM + '/' + widget.id;
var APP_RESOURCE_PATH = DIRECTORY_COMMON_WIDGET + '/';

var FileSystemCustom =
{
};

FileSystemCustom.onCompleteCallBack;
FileSystemCustom.activateCallBack;

FileSystemCustom.setOnCompleteCallBack = function( callBack )
{
	FileSystemCustom.onCompleteCallBack = callBack;
};

FileSystemCustom.setActivateCallBack = function( activate )
{
	FileSystemCustom.activateCallBack = activate;
};

//First parameter is the id of the html object for this player
FileSystemCustom.initObj = function( Str_Id_Html_Obj )
{
	fileSystemObj = document.getElementById(Str_Id_Html_Obj);
	fileSystemObj.Open('FileSystem', '1.000', 'FileSystem');
	fileSystemObj.Execute('SetWidgetInfo', 2, DIRECTORY_WIDGET);
	fileSystemObj.Execute('SetWidgetInfo', 2, '/tmp/');

	downloadSysObj = document.getElementById('id_download_system');
	downloadSysObj.OnComplete = downloadStatus;

};

FileSystemCustom.closeObj = function()
{
	fileSystemObj.Close();
};

//Path examples
//var SrcPath = '/mtd_down/common/src/GirlsGeneration.zip';
//var CopyPath = '/mtd_down/common/srcCopy/GirlsGeneration.zip';
//var SrcPath = '/mtd_down/widgets/user/GirlsGeneration.zip';
//var CopyPath = '/mtd_down/widgets/user/GirlsGeneration.zip';
FileSystemCustom.copy = function( Str_Src_Path, Str_Copy_Path )
{
	return fileSystemObj.Execute("Copy", Str_Src_Path, Str_Copy_Path);
};

//Path examples
//var SrcPath = '/mtd_down/test/src/GirlsGeneration.zip';
//var MovePath = '/mtd_down/test/srcMove';
FileSystemCustom.move = function( Str_Src_Path, Str_Move_Path )
{
	return fileSystemObj.Execute("Move", Str_Src_Path, Str_Move_Path);
};

//Path examples
//Str_Src_Path = '/mtd_down/common/src/GirlsGeneration.zip'
//Str_Unzip_Path = '/mtd_down/common/dest');
FileSystemCustom.unzip = function( Str_Src_Path, Str_Unzip_Path )
{
	return fileSystemObj.Execute("Unzip", Str_Src_Path, Str_Unzip_Path);
};

FileSystemCustom.deletePathOrFile = function( Str_Delete_Path )
{
	return fileSystemObj.Execute("Delete", Str_Delete_Path);
};

//folderPath (String folder path),
//searchSubFolder (Number 1 : search recursively; 0 : only input folder),
//searchLink (Number 1 : search link as folder; 0 : no search link)
FileSystemCustom.getFolderSize = function( Str_Folder_Path, Int_Search_Sub_Folder, Int_Search_Link )
{
	return fileSystemObj.Execute('GetFolderSize', Str_Folder_Path, Int_Search_Sub_Folder, Int_Search_Link );
};

FileSystemCustom.isExistedPath = function( Str_Dir_Path )
{
	return fileSystemObj.Execute("IsExistedPath", Str_Dir_Path);
};

FileSystemCustom.getListFiles = function( Src_Dir_Path )
{
	return fileSystemObj.Execute("GetListFiles", Src_Dir_Path);
};

//FileSystem.startDownFile = function( Str_Src_URL, Str_Dest_Path, Int_File_Size, Int_Download_Gap )
//{
//	fileSystemObj.Execute( 'StartDownFile', Str_Src_URL, Str_Dest_Path, Int_File_Size, Int_Download_Gap );
//	var debug = document.getElementById(str_deb_fs_el_id);
//	debug.innerHTML = "[Debug] : " + "FILESYSTEM[" + "Start downloading ..." + "]";
//};

FileSystemCustom.cancelDownload = function()
{
	fileSystemObj.Execute( 'CancelDownload' );
};

FileSystemCustom.getFreeSize = function()
{
	return fileSystemObj.Execute('GetFreeSize');
};

FileSystemCustom.getTotalSize = function()
{
	return fileSystemObj.Execute('GetTotalSize');
};

FileSystemCustom.getFileSize = function( srcPath )
{
	//Example : var SrcPath = '/mtd_down/test/src/GirlsGeneration.zip';
	return fileSystemObj.Execute('GetFileSize', srcPath );
};

FileSystemCustom.getUsedSize = function()
{
	return fileSystemObj.Execute('GetUsedSize');
};

FileSystemCustom.getItemType = function()
{
	return fileSystemObj.Execute('GetItemType');
};

//Download event handler
function downloadStatus( msg )
{
	   var strResultList = msg.split("?");
		  // DownResult: If res=1 success, otherwise ERROR (see end of this file)
	   if (strResultList[0] == 1000) {
		   if(strResultList[1] == 1){
			   App.logInfo("downloadStatus() - Download Complete!");
			   if( typeof FileSystemCustom.onCompleteCallBack === 'function' && FileSystemCustom.activateCallBack === 'true' ){
				   FileSystemCustom.onCompleteCallBack();
			   }
			} else {
				App.logWarning("downloadStatus() - Download Error = " + strResultList[1]);
	    	}
	      // DownRatio: 0~100
	   } else if (strResultList[0] == 1001) {
		   App.logInfo("downloadStatus() - " + "DownRatio = " + strResultList[1] + "%");
	      // Down Speed: Bytes/Sec: It will be reach after Ratio }
	   } else if (strResultList[0] == 1002) {
		   App.logInfo("downloadStatus() - " + "Down Speed = " + strResultList[1]);
	   }
}

FileSystemCustom.startDownFile = function( Str_Src_URL, Str_Dest_Path, Int_File_Size, Int_Download_Gap )
{	
	SSSP.logInfo("FileSystemCustom.startDownFile() -  " + Str_Src_URL);
	SSSP.logInfo("FileSystemCustom.startDownFile() - Save to: " + Str_Dest_Path);
	var status = downloadSysObj.StartDownFile( Str_Src_URL, Str_Dest_Path);//, Int_File_Size, Int_Download_Gap );
	SSSP.logInfo("FileSystemCustom.startDownFile() - Status:" + status);
};
