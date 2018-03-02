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
											var oItem = event.getParameter("listItem");
											var aCells = oItem.getCells();
											var lvId = aCells[0].getTitle();
											
											
											var oModel = this.getView().getModel();
//											var lvDeletePath = "/PlaylistSchedules(" + lvId + ")/$links/PlaylistDetails";
											var lvDeletePath = "/PlaylistSchedules(" + lvId + ")";
											oModel.remove(lvDeletePath, {success: this._fnSuccess, error: this._fnError});
											oModel.refresh();
										},
										updateTableBinding: function(){
											var oTable = this.getView().byId("idMediaContentsTable"); 
											var oTemplate = new sap.m.ColumnListItem({
											    cells:[
											    	this.getView().byId("id"),
											    	this.getView().byId("type"),
											    	this.getView().byId("start_time"),
											        this.getView().byId("end_time"),
											        this.getView().byId("date"),
											        this.getView().byId("month"),
											        this.getView().byId("day_of_month"),
											        this.getView().byId("day_of_week")
											        ]
											});
											var path = this.relPath + "/PlaylistScheduleDetails";
											oTable.bindItems(path,oTemplate);
										},
										formatTime : function(v) {											
											var tmpDate = new Date(v);
											tmpDate.setTime( tmpDate.getTime() + tmpDate.getTimezoneOffset()*60*1000 );
											var oDateFormat = sap.ui.core.format.DateFormat.getInstance({pattern: "HH:mm:ss"});
											return oDateFormat.format(tmpDate);
										},
										formatDate : function(v) {											
											var tmpDate = new Date(v);
											if (tmpDate.getTime() > 0) {
												tmpDate.setTime( tmpDate.getTime() + tmpDate.getTimezoneOffset()*60*1000 );
												var oDateFormat = sap.ui.core.format.DateFormat.getInstance({pattern: "dd.MM.YYYY"});
												return oDateFormat.format(tmpDate);
											}
											else {
												return "";
											}
										},
										_fnSuccess : function(ioEvent) {
											sap.m.MessageToast.show("Success",{
											    closeOnBrowserNavigation: false });
										},
										_fnError : function(ioError) {
											sap.m.MessageToast.show("Error",{
											    closeOnBrowserNavigation: false });
										}
									});

					return PageController;
				});
