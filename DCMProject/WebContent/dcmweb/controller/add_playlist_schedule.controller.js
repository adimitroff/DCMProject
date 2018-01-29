sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				'sap/ui/core/routing/History' ],
		function(Controller, History) {
			var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.add_playlist_schedule", {
		            onNavigateBack : function(evt) {
						this.navigateBack();
					},
					playlist_id: "",
					onInit : function(oEvent) {
						var loRouter = sap.ui.core.UIComponent
						.getRouterFor(this);
						var loRoute = loRouter.getRoute("AddPlaylistSchedule");
						if (loRoute !== undefined) {
							loRoute.attachMatched(this._onRouteMatched, this);
						}
					},
					_onRouteMatched : function (ioEvent) {
						var loArgs, loView;
						loArgs = ioEvent.getParameter("arguments");
						var lvId = loArgs.id;
						this.playlist_id = lvId; 
						loView = this.getView();
						
						if (lvId == 0){
							this.clearScreenFields();
							return;
						}
						
						loView.bindElement({
							path : "/PlaylistSchedule(" + lvId + ")",
							events : {
								change: this._onBindingChange.bind(this),
								dataRequested: function (oEvent) {
									loView.setBusy(true);
								},
								dataReceived: function (oEvent) {
									loView.setBusy(false);
								}
							}
						});
					},
					_onBindingChange : function (oEvent) {
						// No data for the binding
//						if (!this.getView().getBindingContext()) {
//							this.getRouter().getTargets().display("notFound");
//						}
					},
					handleCancelPress : function() {
						this.navigateBack();
					},
					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.PlaylistId = this.playlist_id;
						vProperties.Type = this.getView().byId(
								"type").getSelectedKey();
						vProperties.Date = this.getView().byId(
							"date").getValue();
						vProperties.Month = this.getView().byId(
							"month").getValue();
						vProperties.DayOfMoth = this.getView().byId(
							"day_of_month").getValue();
						vProperties.DayOfWeek = this.getView().byId(
							"day_of_week").getValue();
						vProperties.StartTime = this.getView().byId(
							"start_time").getValue();
						vProperties.EndTime = this.getView().byId(
							"end_time").getValue();
						if (vProperties.Id == "") {
							vProperties.Id = 0;
							oModel.createEntry("/PlaylistSchedule", vProperties);
						} else {
							var oEntry = {};
							var mParameters = {};
							mParameters.context = this.getView()
									.getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							oModel.update("", vProperties, mParameters);
						}
						oModel.submitChanges(this._fnSuccess, this._fnError);
						oModel.refresh();
						this.navigateBack();

					},
					_fnSuccess : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Success",{
							    closeOnBrowserNavigation: false });
					},
					_fnError : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Error",{
						    closeOnBrowserNavigation: false });
					},
					navigateBack : function(){
						var oHistory = History.getInstance();
						var sPreviousHash = oHistory.getPreviousHash();

						if (sPreviousHash !== undefined) {
							window.history.go(-1);
						} else {
							var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
							oRouter.navTo("Main", true);
						}
					},
					clearScreenFields : function(){
//						this.getView().byId("id").setValue("");
//						this.getView().byId("name").setValue("");
//						this.getView().byId("description").setValue("");
//						
//						this.getView().byId("default").selected = false;
//						this.getView().byId("active").selected = false;
//						
//						this.getView().byId("priority").setValue("");
					}
			});

					return PageController;
		});