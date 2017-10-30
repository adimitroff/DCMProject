sap.ui.define(['jquery.sap.global', 'sap/ui/core/mvc/Controller', 'sap/m/MessageToast'],
	function(jQuery, Controller, MessageToast) {
	"use strict";

	var PageController = Controller.extend("net.cb.dcm.frontend.controller.main", {
		press : function(evt) {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			var oTile = evt.getSource();
			var lsId = oTile.getId();
			lsId = lsId.replace("__xmlview0--","");
			switch(lsId){
			case "tileTag":
				oRouter.navTo("ListTags");
				break;
			case "tileDevice":
				break;
			case "tileMediaObjects":
				break;
			case "tileOptions":
				break;
			}
		}
	});

	return PageController;
});