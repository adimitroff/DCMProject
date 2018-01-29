sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
				  'sap/m/MessageToast'],
				function(Controller, History, MessageToast) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_related_schedules",
									{	
										relPath:"",
										playlist_id:"",
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
											var loRoute = loRouter.getRoute("ListPlaylistRelatedSchedules");
											if (loRoute !== undefined) {
												loRoute.attachMatched(this._onRouteMatched, this);
											}
											
										},
										_onRouteMatched : function (ioEvent) {
											var loArgs, loView;
											loArgs = ioEvent.getParameter("arguments");
											var lvId = loArgs.id;
											this.playlist_id = lvId;
											this.relPath = "/Playlists(&1L)";
											this.relPath = this.relPath.replace("&1",lvId);
											this.updateTableBinding();
										},
										onAdd : function(evt) {
											var oRouter = sap.ui.core.UIComponent
											.getRouterFor(this);
											oRouter.navTo("AddPlaylistSchedule", {id:this.playlist_id});
										},
										onDelete : function(event){
											//var loBindingContext = event.getSource().getBindingContext();
											//var lvId = loBindingContext.getProperty("Id");
											
											var oItem = event.getParameter("listItem");
											var aCells = oItem.getCells();
											var lvId = aCells[0].getTitle();
											
											
											var oModel = this.getView().getModel();
											var lvDeletePath = this.relPath + "/$links/PlaylistScheduleDetails" + "(" + lvId + "L)"
											oModel.remove(lvDeletePath);
											oModel.refresh();
										},
										updateTableBinding: function(){
											var oTable = this.getView().byId("idMediaContentsTable"); 
											var oTemplate = new sap.m.ColumnListItem({
											    cells:[
											        new sap.m.ObjectIdentifier({title:"{Id}"}),
											        new sap.m.Text({text:"{Type}"}),
											        new sap.m.Text({text:"{StartTime}"}),
											        new sap.m.Text({text:"{EndTime}"}),
											        new sap.m.Text({text:"{Date}"}),
											        new sap.m.Text({text:"{Month}"}),
											        new sap.m.Text({text:"{DateOfMoth}"}),
											        new sap.m.Text({text:"{DayOfWeek}"})
											        ]
											});
											var path = this.relPath + "/PlaylistScheduleDetails";
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
