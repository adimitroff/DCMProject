sap.ui.define([ 'sap/ui/core/mvc/Controller', 'sap/ui/core/routing/History' ],
		function(Controller, History) {
			var PageController = Controller.extend(
					"net.cb.dcm.frontend.controller.add_playlist_schedule", {
						onNavigateBack : function(evt) {
							this.navigateBack();
						},
						playlist_id : "",
						onInit : function(oEvent) {
							var loRouter = sap.ui.core.UIComponent
									.getRouterFor(this);
							var loRoute = loRouter
									.getRoute("AddPlaylistSchedule");
							if (loRoute !== undefined) {
								loRoute.attachMatched(this._onRouteMatched,
										this);
							}
						},
						_onRouteMatched : function(ioEvent) {
							var loArgs, loView;
							loArgs = ioEvent.getParameter("arguments");
							var lvId = loArgs.id;
							this.playlist_id = lvId;
							loView = this.getView();

							if (lvId == 0) {
								this.clearScreenFields();
								return;
							}

							loView.bindElement({
								path : "/PlaylistSchedule(" + lvId + ")",
								events : {
									change : this._onBindingChange.bind(this),
									dataRequested : function(oEvent) {
										loView.setBusy(true);
									},
									dataReceived : function(oEvent) {
										loView.setBusy(false);
									}
								}
							});
						},
						_onBindingChange : function(oEvent) {
							// No data for the binding
							// if (!this.getView().getBindingContext()) {
							// this.getRouter().getTargets().display("notFound");
							// }
						},
						handleCancelPress : function() {
							this.navigateBack();
						},
						handleSavePress : function(evt) {
							var oModel = this.getView().getModel();

							var vProperties = {};
							vProperties.Id = 0;
//							vProperties.PlaylistId = this.playlist_id;
							vProperties.Type = this.getView().byId("type")
									.getSelectedKey();
							vProperties.Date = this.getView().byId("date")
									.getValue();
							// vProperties.Month = this.getView().byId(
							// "month").getValue();
							vProperties.DayOfMoth = this.getView().byId(
									"day_of_month").getValue();
							vProperties.DayOfWeek = this.getView().byId(
									"day_of_week").getValue();
							vProperties.StartTime = this.getView().byId(
									"start_time").getValue();
							vProperties.EndTime = this.getView().byId(
									"end_time").getValue();

							var lvError = false;
							switch (vProperties.Type) {
							case "1":
								if (vProperties.Date == null ||
										vProperties.Date == "") {
									lvError = true;
								}
								break;
							case "2":
								if (vProperties.DayOfMoth == null ||
										vProperties.DayOfMoth == "") {
									lvError = true;
								}
								break;
							case "3":
								if (vProperties.DayOfWeek == null ||
										vProperties.DayOfWeek == "") {
									lvError = true;
								}
								break;
							case "5":
								if (vProperties.Date == null ||
										vProperties.Date == "") {
									lvError = true;
								}
								break;
							}
							if (vProperties.StartTime == null ||
									vProperties.StartTime == "" ||
									vProperties.EndTime == null ||
									vProperties.EndTime == ""){
								lvError = true;
							}
								
							if (lvError) {
								jQuery.sap.require("sap.m.MessageToast");
								sap.m.MessageToast.show(
										"Error: missing fields values", {
											closeOnBrowserNavigation : false
										});
								return;
							}

							if (vProperties.Id == "") {
								vProperties.Id = 0;
								oModel.createEntry("/PlaylistSchedules",
										vProperties);
							} else {
								var oEntry = {};
								var mParameters = {};
								mParameters.context = this.getView()
										.getBindingContext();
								mParameters.success = this._fnSuccess;
								mParameters.error = this._fnError;
								oModel.update("", vProperties, mParameters);
							}
							oModel
									.submitChanges(this._fnSuccess,
											this._fnError);
							oModel.refresh();
							this.navigateBack();

						},
						_fnSuccess : function() {
							jQuery.sap.require("sap.m.MessageToast");
							sap.m.MessageToast.show("Success", {
								closeOnBrowserNavigation : false
							});
						},
						_fnError : function() {
							jQuery.sap.require("sap.m.MessageToast");
							sap.m.MessageToast.show("Error", {
								closeOnBrowserNavigation : false
							});
						},
						navigateBack : function() {
							var oHistory = History.getInstance();
							var sPreviousHash = oHistory.getPreviousHash();

							if (sPreviousHash !== undefined) {
								window.history.go(-1);
							} else {
								var oRouter = sap.ui.core.UIComponent
										.getRouterFor(this);
								oRouter.navTo("Main", true);
							}
						},
						onSelectionChange : function(evt) {
							var loItem = evt.getParameter('selectedItem');
							var lvKey = loItem.getKey();
							this.getView().byId("date").setVisible(
									(lvKey == "1" || lvKey == "5") ? true
											: false);
							this.getView().byId("date_label").setVisible(
									(lvKey == "1" || lvKey == "5") ? true
											: false);
							this.getView().byId("day_of_month").setVisible(
									lvKey == "3" ? true : false);
							this.getView().byId("dom_label").setVisible(
									lvKey == "3" ? true : false);
							this.getView().byId("day_of_week").setVisible(
									lvKey == "2" ? true : false);
							this.getView().byId("dow_label").setVisible(
									lvKey == "2" ? true : false);
						},
						clearScreenFields : function() {
							// this.getView().byId("id").setValue("");
							// this.getView().byId("name").setValue("");
							// this.getView().byId("description").setValue("");
							//						
							// this.getView().byId("default").selected = false;
							// this.getView().byId("active").selected = false;
							//						
							// this.getView().byId("priority").setValue("");
						}
					});

			return PageController;
		});