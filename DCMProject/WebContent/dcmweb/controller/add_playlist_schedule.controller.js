sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller', 'sap/ui/core/routing/History' ],
				function(Controller, History) {
					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.add_playlist_schedule",
									{
										onNavigateBack : function(evt) {
											this.navigateBack();
										},
										onInit : function(oEvent) {
											var loRouter = sap.ui.core.UIComponent
													.getRouterFor(this);
											var loRoute = loRouter
													.getRoute("AddPlaylistSchedule");
											if (loRoute !== undefined) {
												loRoute.attachMatched(
														this._onRouteMatched,
														this);
											}
										},
										_onRouteMatched : function(ioEvent) {
											var loArgs, loView;
											loArgs = ioEvent
													.getParameter("arguments");
											var lvId = loArgs.id;
											window.playlist_id = lvId;
//											loView = this.getView();
//
//											if (lvId == 0) {
//												this.clearScreenFields();
//												return;
//											}
//
//											loView
//													.bindElement({
//														path : "/PlaylistSchedule("
//																+ lvId + ")",
//														events : {
//															change : this._onBindingChange
//																	.bind(this),
//															dataRequested : function(
//																	oEvent) {
//																loView
//																		.setBusy(true);
//															},
//															dataReceived : function(
//																	oEvent) {
//																loView
//																		.setBusy(false);
//															}
//														}
//													});
										},
										_onBindingChange : function(oEvent) {
											// No data for the binding
											// if
											// (!this.getView().getBindingContext())
											// {
											// this.getRouter().getTargets().display("notFound");
											// }
										},
										handleCancelPress : function() {
											this.navigateBack();
										},
										handleSavePress : function(evt) {
											var oModel = this.getView()
													.getModel();

											var vProperties = {};
											if (this.getView().byId("date")
													.getValue() != "") {
												vProperties.Date = new Date(
														this.getView().byId(
																"date")
																.getValue());
											}
											vProperties.DayOfMoth = this
													.getView().byId(
															"day_of_month")
													.getValue();
											if (vProperties.DayOfMoth == "") {
												vProperties.DayOfMoth = 0;
											}
											vProperties.DayOfWeek = this
													.getView().byId(
															"day_of_week")
													.getValue();
											if (vProperties.DayOfWeek == "") {
												vProperties.DayOfWeek = 0;
											}

											var loTimeFormatter = sap.ui.core.format.DateFormat
													.getDateInstance({
														pattern : "PTh'H'm'M's'S'"
													});
											var loTimeFormatter2 = sap.ui.core.format.DateFormat
											.getTimeInstance({
												pattern : "KK:mm:ss a"
											});
											
//											var TZOffsetMS = new Date(0).getTimezoneOffset()*60*1000;
//											var timeStr = loTimeFormatter2.format(new Date(39600000 + TZOffsetMS));
//											var parsedTime = new Date(loTimeFormatter2.parse(timeStr).getTime() - TZOffsetMS); //39600000
											
											
											
											// if (this.getView().byId(
											// "end_time").getValue() != ""){
											// vProperties.EndTime =
											// loTimeFormatter.format(this.getView().byId(
											// "end_time").getDateValue());
											// }
											if (this.getView().byId("end_time")
													.getValue() != "") {
												vProperties.EndTime = this.getView().byId("end_time").getDateValue();
											}
											vProperties.Id = 0;
											vProperties.Month = 0;

											// / / var lvUrl =
											// / /
											// "/DCMProject/DCMService.svc/Playlists("
											// / / +
											// / / this.playlist_id + "L)";
											// / / vProperties.Playlist = lvUrl;
											// / / vProperties.Playlist =
											// / / this.playlist_id + "L";
											vProperties.Playlist = window.playlist_id;

											// if (this.getView().byId(
											// "start_time").getValue() != "") {
											// vProperties.StartTime =
											// loTimeFormatter.format(this.getView().byId(
											// "start_time").getDateValue());
											// }
											if (this.getView().byId("start_time")
													.getValue() != "") {
												vProperties.StartTime = this.getView().byId("start_time").getDateValue();
											}
											vProperties.Type = this.getView()
													.byId("type")
													.getSelectedKey();

											var lvError = false;
											switch (vProperties.Type) {
											case "SINGLE_DAY":
												if (vProperties.Date == null
														|| vProperties.Date == "") {
													lvError = true;
												}
												break;
											case "WEEKLY":
												if (vProperties.DayOfWeek == null
														|| vProperties.DayOfWeek == 0) {
													lvError = true;
												}
												break;
											case "MONTHLY":
												if (vProperties.DayOfMoth == null
														|| vProperties.DayOfMoth == 0) {
													lvError = true;
												}
												break;
											case "YEARLY":
												if (vProperties.Date == null
														|| vProperties.Date == "") {
													lvError = true;
												}
												break;
											}
											
//											if (vProperties.StartTime == null
//													|| vProperties.StartTime == ""
//													|| vProperties.EndTime == null
//													|| vProperties.EndTime == "") {
//												lvError = true;
//											}

											if (lvError) {
												jQuery.sap
														.require("sap.m.MessageToast");
												sap.m.MessageToast
														.show(
																"Error: missing fields values",
																{
																	closeOnBrowserNavigation : false
																});
												return;
											}
											if (vProperties.Id == "") {
												vProperties.Id = 0;
												// / /
												// oModel.createEntry("/PlaylistSchedules",
												oModel
														.createEntry(
																"/Playlists("
																		+ window.playlist_id
																		+ "L)/PlaylistScheduleDetails",
																vProperties);
											} else {
												var oEntry = {};
												var mParameters = {};
												mParameters.context = this
														.getView()
														.getBindingContext();
												mParameters.success = this._fnSuccess;
												mParameters.error = this._fnError;
												oModel.update("", vProperties,
														mParameters);
											}
											oModel.submitChanges(
													this._fnSuccess,
													this._fnError);
											oModel.refresh();
											window.currentController = this;
											window.model = oModel; 
//											this.navigateBack();

										},
										_fnSuccess : function(ioEvent) {
											jQuery.sap
													.require("sap.m.MessageToast");
											sap.m.MessageToast
													.show(
															"Success",
															{
																closeOnBrowserNavigation : false
															});
//											var lvUrl = "/DCMProject/DCMService.svc/Playlists("
//													+ window.playlist_id
//													+ "L)/$links/PlaylistScheduleDetails";
////													+ "L)/PlaylistScheduleDetails/$ref";
//											var lvNewTagUri = "/PlaylistSchedules("
//													+ ioEvent.Id + "L)";
											var lvUrl = "/DCMProject/DCMService.svc/PlaylistSchedules("
												+ ioEvent.Id
												+ "L)/$links/PlaylistDetails";
//												+ "L)/PlaylistDetails/$ref";
//												+ "L)/PlaylistScheduleDetails/$ref";
											var lvNewTagUri = "/Playlists("
												+ window.playlist_id + "L)";
											var aData = jQuery
													.ajax({
														type : "PUT",
														contentType : "application/json",
														data : '{ "uri": "'
																+ lvNewTagUri
																+ '"}',
														url : lvUrl,
														dataType : "json",
														async : false,
														success : function(
																data,
																textStatus,
																jqXHR) {

															sap.m.MessageToast
																	.show(
																			"Success",
																			{
																				closeOnBrowserNavigation : false
																			});
															window.model.refresh();
															window.currentController.navigateBack();
														},
														error : function(
																response) {

															sap.m.MessageToast
																	.show(
																			"Error",
																			{
																				closeOnBrowserNavigation : false
																			});
														},
													});
										},
										_fnError : function(ioEvent) {
											jQuery.sap
													.require("sap.m.MessageToast");
											sap.m.MessageToast
													.show(
															"Error",
															{
																closeOnBrowserNavigation : false
															});
										},
										navigateBack : function() {
											var oHistory = History
													.getInstance();
											var sPreviousHash = oHistory
													.getPreviousHash();

											if (sPreviousHash !== undefined) {
												window.history.go(-1);
											} else {
												var oRouter = sap.ui.core.UIComponent
														.getRouterFor(this);
												oRouter.navTo("Main", true);
											}
										},
										onSelectionChange : function(evt) {
											var loItem = evt
													.getParameter('selectedItem');
											var lvKey = loItem.getKey();
											this
													.getView()
													.byId("date")
													.setVisible(
															(lvKey == "SINGLE_DAY" || lvKey == "YEARLY") ? true
																	: false);
											this
													.getView()
													.byId("date_label")
													.setVisible(
															(lvKey == "SINGLE_DAY" || lvKey == "YEARLY") ? true
																	: false);
											this
													.getView()
													.byId("day_of_month")
													.setVisible(
															lvKey == "MONTHLY" ? true
																	: false);
											this
													.getView()
													.byId("dom_label")
													.setVisible(
															lvKey == "MONTHLY" ? true
																	: false);
											this
													.getView()
													.byId("day_of_week")
													.setVisible(
															lvKey == "WEEKLY" ? true
																	: false);
											this
													.getView()
													.byId("dow_label")
													.setVisible(
															lvKey == "WEEKLY" ? true
																	: false);
										},
										clearScreenFields : function() {
											// / /
											// this.getView().byId("id").setValue("");
											// / /
											// this.getView().byId("name").setValue("");
											// / /
											// this.getView().byId("description").setValue("");
											// / /
											// / /
											// this.getView().byId("default").selected
											// / / = false;
											// / /
											// this.getView().byId("active").selected
											// / / = false;
											// / /
											// / /
											// this.getView().byId("priority").setValue("");
										}
									});

					return PageController;
				});