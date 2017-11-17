sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				'sap/ui/core/routing/History' ],
		function(Controller, History) {
			var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.edit_device", {
		            onNavigateBack : function(evt) {
						this.navigateBack();
					},
					onInit : function(oEvent) {
						var loRouter = sap.ui.core.UIComponent
						.getRouterFor(this);
						var loRoute = loRouter.getRoute("EditDevice");
						if (loRoute !== undefined) {
							loRoute.attachMatched(this._onRouteMatched, this);
						}
					},
					_onRouteMatched : function (ioEvent) {
						var loArgs, loView;
						loArgs = ioEvent.getParameter("arguments");
						var lvId = loArgs.id;
						loView = this.getView();

						loView.bindElement({
							path : "/Devices(" + lvId + ")",
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

					handleDeletePress : function() {
						var oModel = this.getView().getModel();
						var lvId = this.getView().byId("id").getValue();
						oModel.remove("/Tags(" + lvId + "L)");
						oModel.refresh();
						this.navigateBack();
					},

					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.Name = this.getView()
								.byId("name").getValue();
						vProperties.Description = this.getView().byId(
								"description").getValue();
						if (vProperties.Id == "") {
							vProperties.Id = 0;
							oModel.createEntry("/Devices", vProperties);

						} else {
							var oEntry = {};
							var mParameters = {};
							mParameters.context = this.getView()
									.getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							oEntry.Id = vProperties.Id;
							oEntry.Name = vProperties.Name;
							oEntry.Description = vProperties.Description;
							oModel.update("", oEntry, mParameters);
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
					}
			});

					return PageController;
		});