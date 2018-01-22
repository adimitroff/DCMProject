sap.ui.define(['jquery.sap.global', 'sap/ui/core/mvc/Controller', 'sap/m/MessageToast'],
	function(jQuery, Controller, MessageToast) {
	"use strict";

	var PageController = Controller.extend("net.cb.dcm.frontend.controller.logon", {
		gvLoggedIn : '',
		getLoggedIn : function(){
			return this.gvLoggedIn;
		},
		OnLogon : function(evt){
			var lvUserExist = 0;
			var lvEmail = this.getView().byId("email").getValue();
			var lvPassword = this.getView().byId("password").getValue();
			var lvUrl = "/DCMProject/DCMService.svc/Users/$count?$filter=(Email%20eq%20'" +
			 lvEmail + "'%20and%20Password%20eq%20'" + lvPassword +"')";
			var aData = jQuery.ajax({
				type : "GET",
				contentType : "application/json",
				url : lvUrl,
				dataType : "json",
				async: false, 
				success : function(data,textStatus, jqXHR) {
					lvUserExist = data;
				},
				error : function(response) {
					alert("Error connecting to database");
				},
			});
			
			if (lvUserExist > 0){
				this.gvloggedIn = 'X';
				var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				oRouter.navTo("Main");
			} else {
				sap.m.MessageBox.show(
			      "Username or password is incorrect!", {
			          icon: sap.m.MessageBox.Icon.ERROR,
			          title: "Error",
			          actions: [sap.m.MessageBox.Action.CANCEL]
			      }
			      );
			}
		},
		onInit : function(oEvent) {
		}
	});

	return PageController;
});