sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
				  'sap/m/MessageToast'],
				function(Controller, History, MessageToast) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_device_schedule",
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
											
											var loRoute = loRouter.getRoute("ListDeviceSchedule");
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
											var oTable = this.getView().byId("idScheduleTable"); 
											var oTemplate = new sap.m.ColumnListItem({
											    cells:[
											    	this.getView().byId("idScheduleId"),
											    	this.getView().byId("idScheduleVFrom"),
											    	this.getView().byId("idScheduleVTo"),
											        this.getView().byId("idLoopList")
											        ]
											});
											var path = this.relPath + "/DeviceScheduleDetails/LoopDetails";
											oTable.bindItems(path,oTemplate);
										},
										formatTime : function(v) {
											var tmpStr = v;
											tmpStr = tmpStr.replace("PT", "");
											var tmpArr = tmpStr.split("H");
											var time = (tmpArr[0].length==1?("0"+tmpArr[0]):tmpArr[0]) + ":";
											tmpArr = tmpArr[1].split("M");
											time = time + (tmpArr[0].length==1?("0"+tmpArr[0]):tmpArr[0]) + ":";
											if (tmpArr.length > 1){
												tmpArr[1] = tmpArr[1].replace("S", "");
												time = time + (tmpArr[1].length==1?("0"+tmpArr[1]):tmpArr[1]);
											} else {
												time = time + "00";
											}
										    return time;
										}
									});

					return PageController;
				});
