sap.ui.define(['jquery.sap.global', 'sap/ui/core/mvc/Controller', 'sap/m/MessageToast'],
	function(jQuery, Controller, MessageToast) {
	"use strict";

	var PageController = Controller.extend("net.cb.dcm.frontend.controller.main", {
		onTileClick : function(evt) {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			var oTile = evt.getSource();
			var lsId = oTile.getId();
			var laStrings = lsId.split("--");
			if (laStrings.length == 2){
				lsId = laStrings[1]; 
			}
			switch(lsId){
			case "tileTags":
				oRouter.navTo("ListTags");
				break;
			case "tileDevices":
				oRouter.navTo("ListDevices");
				break;
			case "tileMediaObjects":
				oRouter.navTo("ListMediaObjects");
				break;
			case "tileOptions":
				break;
			case "tilePlaylists":
				oRouter.navTo("ListPlaylists");
				break;
			}
		},
		onInit : function(oEvent) {
//			var loODataModel = sap.ui.getCore().getModel();
//			var laDevices = loODataModel.getProperty("/Devices");
			var laDevices;
			var lvNewDevices = "No New Devices";
			var lvNewDevicesCount = 0;
			var lvDeviceCount = 0;
			if (laDevices != undefined){
				lvDeviceCount = laDevices.length;
				for (var i = 0; i < laDevices.length; i++){
					var laDevice = laDevices[i];
					if (laDevice.Ip != undefined && laDevice.Ip != "" &&
							laDevice.Ip == ""){
						lvNewDevices++;
					}
				}
			}
			var loModel = new sap.ui.model.json.JSONModel();
			if(lvNewDevicesCount > 0){
				lvNewDevices = lvNewDevicesCount + " New Devices";
			}
			loModel.setData({
			    NewDevCount: lvNewDevices,
			    DevCount: lvDeviceCount
			});
			this.getView().setModel(loModel);
		}
	});

	return PageController;
});