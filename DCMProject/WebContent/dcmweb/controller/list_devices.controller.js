sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
					'sap/m/MessageToast',
					'sap/m/MessageBox'],
				function(Controller, History) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_devices",
									{	
										onNavigateBack : function(event) {
											var oHistory = History.getInstance();
											var sPreviousHash = oHistory.getPreviousHash();

											if (sPreviousHash !== undefined) {
												window.history.go(-1);
											} else {
												var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
												oRouter.navTo("Main", true);
											}
										},
										onInit : function(evt) {
											
										},
										onDetailPress : function(event) {
											var oRouter = sap.ui.core.UIComponent
													.getRouterFor(this);
											var loBindingContext = event.getSource().getBindingContext();
											var lvId = loBindingContext.getProperty("Id");
											oRouter.navTo("EditDevice", {id:lvId});
										},
										onButtonPress : function(ioEvent) {
											var lvId = ioEvent.getSource().getBindingContext().getProperty("Id");
											
											var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
											oRouter.navTo("ListDeviceStatus", {id:lvId});
										},
										onProcedureButtonPress : function(ioEvent) {
											var lvId = ioEvent.getSource().getBindingContext().getProperty("Id");
											
											var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
											oRouter.navTo("ListDeviceProcedures", {id:lvId});
										},
										onScheduleButtonPress : function(ioEvent) {
											var lvId = ioEvent.getSource().getBindingContext().getProperty("Id");
											
											var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
											oRouter.navTo("ListDeviceSchedule", {id:lvId});
										}
									});

					return PageController;
				});
