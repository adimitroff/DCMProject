sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
				  'sap/m/MessageToast'],
				function(Controller, History, MessageToast) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_device_status",
									{	
										relPath:"",
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
											var loRouter = sap.ui.core.UIComponent.getRouterFor(this);
											
											var loRoute = loRouter.getRoute("ListDeviceStatus");
											if (loRoute !== undefined) {												
												loRoute.attachMatched(this._onRouteMatched, this);
											}
											
										},
										_onRouteMatched : function (ioEvent) {
											var loArgs, loView;
											loArgs = ioEvent.getParameter("arguments");
											var lvId = loArgs.id;
											this.relPath = "/Devices(&1L)";
											this.relPath = this.relPath.replace("&1",lvId);
											this.updateTableBinding();
//											sap.m.MessageToast.show("Test Success",{closeOnBrowserNavigation: false });
										},
										updateTableBinding: function(){
											var oTable = this.getView().byId("idStatusTable"); 
											var oTemplate = new sap.m.ColumnListItem({
											    cells:[
											        new sap.m.ObjectIdentifier({title:"{Id}"}),
											        new sap.m.Text({text:"{PropertyDetails/Name}"}),
											        new sap.m.Text({text:"{Value}"})
											        ]
											});
											var path = this.relPath + "/DeviceStatusDetails/DeviceStatusValueDetails";
											oTable.bindItems(path,oTemplate);
										}
									});

					return PageController;
				});
