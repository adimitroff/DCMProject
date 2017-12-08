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
				oRouter.navTo("ListSchemas");
				break;
			case "tilePlaylists":
				oRouter.navTo("ListPlaylists");
				break;
			}
		},
		onInit : function(oEvent) {
			var lvAllDevicesCount = 0;
			var lvNewDevicesCount = 0;
			var aData = jQuery.ajax({
				type : "GET",
				contentType : "application/json",
				url : "/DCMProject/DCMService.svc/Devices/$count",
				dataType : "json",
				async: false, 
				success : function(data,textStatus, jqXHR) {
					lvAllDevicesCount = data;
				},
				error : function(response) {
					//alert("Error");
				},
			});
			var aData = jQuery.ajax({
				type : "GET",
				contentType : "application/json",
				url : "/DCMProject/DCMService.svc/Devices/$count?$filter=(Name%20eq%20null)",
				dataType : "json",
				async: false, 
				success : function(data,textStatus, jqXHR) {
					lvNewDevicesCount = data;
				},
				error : function(response) {
					//alert("Error");
				},
			});

			var lvNewDevices = "No New Devices";
			var loModel = new sap.ui.model.json.JSONModel();
			if(lvNewDevicesCount > 0){
				lvNewDevices = lvNewDevicesCount + " New Devices";
			}
			loModel.setData({
			    NewDevCount: lvNewDevices,
			    DevCount: lvAllDevicesCount
			});
			this.getView().setModel(loModel);
		}
	});

	return PageController;
});